package com.github.mybatisq;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "gencode", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class GenCodeMojo extends AbstractMojo {

    /**
     * 被扫描的实体类所在的包
     */
    @Parameter(required = true)
    private String entityPackage;

    /**
     * 生成的代码所在的包
     */
    @Parameter(required = true)
    private String genPackage;
    
    @Parameter(required = true)
    private String mapperFolder;

    /**
     * 文件编码（默认UTF-8）
     */
    @Parameter(defaultValue = "UTF-8")
    private String encoding;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    private ClassLoader getClassLoader() throws MojoExecutionException {
        try {
            List<String> classpathElements = project.getCompileClasspathElements();
            classpathElements.add(project.getBuild().getOutputDirectory());
            classpathElements.add(project.getBuild().getTestOutputDirectory());
            URL urls[] = new URL[classpathElements.size()];

            for (int i = 0; i < classpathElements.size(); ++i) {
                urls[i] = new File((String) classpathElements.get(i)).toURI().toURL();
            }
            return new URLClassLoader(urls, getClass().getClassLoader());
        } catch (MalformedURLException | DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Couldn't create a classloader.", e);
        }
    }

    final Set<String> aliases = new HashSet<>();

    private String space(int count) {
        String space = "";
        for (int i = 0; i < count; i++) {
            space += " ";
        }
        return space;
    }
    
    private String newLine() {
        return "\r\n";
    }

    private String alias(String tableName) {
        String alias = tableName.substring(0, 1);
        if (aliases.contains(alias)) {
            int i = 0;
            while (aliases.contains(alias + i)) {
                i++;
            }
            alias = alias + i;
        }
        aliases.add(alias);
        return alias;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("entity package: " + entityPackage);
        getLog().info("gen package: " + genPackage);

        ClassLoader cl = getClassLoader();

        URL classPath = cl.getResource(entityPackage.replace(".", "/"));
        if (classPath == null) {
            getLog().error("Failed to load classpath");
            throw new MojoFailureException("Failed to load classpath");
        }

        File file = new File(classPath.getFile());
        if (file == null || file.listFiles() == null) {
            return;
        }

        List<Class<?>> classes = new ArrayList<>();
        for (File f : file.listFiles()) {
            try {
                classes.add(cl.loadClass(entityPackage + "." + f.getName().replace(".class", "")));
            } catch (ClassNotFoundException e) {
                getLog().error(e);
                throw new MojoFailureException("Can not resolve class name " + entityPackage + "." + f.getName().replace(".class", ""));
            }
        }

        String outPath = project.getBuild().getSourceDirectory() + "/" + genPackage.replace(".", "/");
        
        String mapperPath = project.getBuild().getSourceDirectory().substring(0, project.getBuild().getSourceDirectory().length() - 4) + "resources/" + mapperFolder;

        classes.forEach(c -> {
            getLog().info("entity class: " + c.getName());
        });

        try {
            InputStream input = cl.getResourceAsStream("mybatisq/QMapper.xml");
            String content = IOUtils.toString(input, encoding).replace("com.github.mybatisq", genPackage);
            input.close();
            String path = mapperPath + "/QMapper.xml";
            FileUtils.write(new File(path), content, encoding);
            getLog().info(path);

            input = cl.getResourceAsStream("mybatisq/QMapper.java.q");
            content = IOUtils.toString(input, encoding).replace("com.github.mybatisq", genPackage);
            input.close();
            path = outPath + "/QMapper.java";
            FileUtils.write(new File(path), content, encoding);
            getLog().info(path);

            classes.forEach(c -> {
                final StringBuilder builder = new StringBuilder();
                builder.append("package " + genPackage + ";" + newLine() + newLine());
                builder.append("import com.github.mybatisq.Column;" + newLine());
                builder.append("import com.github.mybatisq.Join;" + newLine());
                builder.append("import com.github.mybatisq.Query;" + newLine());
                builder.append("import com.github.mybatisq.Table;" + newLine());
                String className = c.getSimpleName() + "Table";
                String tableName = c.getSimpleName().toLowerCase();
                String tableAlias = alias(tableName);
                builder.append(newLine() + "public class " + className + " extends Table {" + newLine());
                builder.append(newLine() + space(4) + className + "() {" + newLine());
                builder.append(space(8) + "super(\"" + tableName + "\", \"" + tableAlias + "\");" + newLine());
                builder.append(space(4) + "}" + newLine());
                builder.append(newLine() + space(4) + "public static final " + className + " " + c.getSimpleName() + " = new " + className + "();" + newLine());
                builder.append(newLine() + space(4) + "public Query<" + className + "> query() {" + newLine());
                builder.append(space(8) + "return new Query<>(" + c.getSimpleName() + ");" + newLine());
                builder.append(space(4) + "}" + newLine());
                builder.append(newLine() + space(4) + "public <T extends Table> Join<" + className + ", T> inner(T " + tableAlias + ") {" + newLine());
                builder.append(space(8) + "return new Join<>(\"inner\", this, " + tableAlias + ");" + newLine());
                builder.append(space(4) + "}" + newLine());

                Field[] fields = c.getDeclaredFields();
                for (Field f : fields) {
                    builder.append(newLine() + space(4) + "public Column<" + className + ", " + f.getType().getName().replace("java.lang.", "") + "> " + f.getName() + " = new Column<>(\"" + f.getName() + "\");" + newLine());
                }
                builder.append("}");
                String fileContent = builder.toString();
                String filePath = outPath + "/" + className + ".java";
                try {
                    FileUtils.write(new File(filePath), fileContent, encoding);
                } catch (IOException e) {
                    getLog().error(e);
                }
                getLog().info(filePath);
            });
            
            classes.forEach(c -> {
                final StringBuilder builder = new StringBuilder();
                builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + newLine());
                builder.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >" + newLine());
                String className = c.getSimpleName();
                String tableName = c.getSimpleName().toLowerCase();
                builder.append("<mapper namespace=\"" + genPackage + "." + className + "Mapper\">");
                
                builder.append(newLine() + space(4) + "<select id=\"count\" parameterType=\"com.github.mybatisq.Query\" resultType=\"java.lang.Integer\">" + newLine());
                builder.append(space(8) + "select count(0) <include refid=\"" + genPackage + ".QMapper.countFrom\"/>" + newLine());
                builder.append(space(4) + "</select>" + newLine());
                builder.append(newLine() + space(4) + "<select id=\"select\" parameterType=\"com.github.mybatisq.Query\" resultType=\"" + entityPackage + "." + className + "\">" + newLine());
                builder.append(space(8) + "select ");
                Field[] fields = c.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    if (i > 0) {
                        builder.append(",");
                    }
                    builder.append("${tableAlias}." + fields[i].getName());
                }
                builder.append(" <include refid=\"" + genPackage + ".QMapper.selectFrom\"/>" + newLine());
                builder.append(space(4) + "</select>" + newLine());
                
                builder.append(newLine() + space(4) + "<insert id=\"insert\" parameterType=\"" + entityPackage + "." + className + "\" useGeneratedKeys=\"true\" keyProperty=\"" + fields[0].getName() + "\">" + newLine());
                builder.append(space(8) + "<trim prefix=\"insert " + tableName + " (\" suffix=\")\" suffixOverrides=\",\">\r\n");
                for (Field f : fields) {
                    builder.append(space(12) + "<if test=\"" + f.getName() + " != null\">" + f.getName() + ",</if>" + newLine());
                }
                builder.append(space(8) + "</trim>" + newLine());
                builder.append(space(8) + "<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">" + newLine());
                for (Field f : fields) {
                    builder.append(space(12) + "<if test=\"" + f.getName() + " != null\">#{" + f.getName() + "},</if>" + newLine());
                }
                builder.append(space(8) + "</trim>" + newLine());
                builder.append(space(4) + "</insert>" + newLine());
                
                builder.append(newLine() + space(4) + "<update id=\"update\" parameterType=\"" + entityPackage + "." + className + "\">" + newLine());
                builder.append(space(8) + "<trim prefix=\"update " + tableName + " set\" suffix=\"where " + fields[0].getName() + "=#{" + fields[0].getName() + "}\" suffixOverrides=\",\">" + newLine());
                for (Field f : fields) {
                    builder.append(space(12) + "<if test=\"" + f.getName() + " != null\">" + f.getName() + "=#{" + f.getName() + "},</if>" + newLine());
                }
                builder.append(space(8) + "</trim>" + newLine());
                builder.append(space(4) + "</update>" + newLine());
                
                builder.append(newLine() + space(4) + "<delete id=\"delete\">" + newLine());
                builder.append(space(8) + "delete from " + tableName + " where " + fields[0].getName() + "=#{" + fields[0].getName() + "}" + newLine());
                builder.append(space(4) + "</delete>" + newLine());
                
                builder.append("</mapper>");
                String fileContent = builder.toString();
                String filePath = mapperPath + "/" + className + "Mapper.xml";
                try {
                    FileUtils.write(new File(filePath), fileContent, encoding);
                } catch (IOException e) {
                    getLog().error(e);
                }
                getLog().info(filePath);
            });
            
            classes.forEach(c -> {
                final StringBuilder builder = new StringBuilder();
                
                String className = c.getSimpleName();
                String tableName = c.getSimpleName().toLowerCase();
                Field[] fields = c.getDeclaredFields();
                
                builder.append("package " + genPackage + ";" + newLine() + newLine());
                builder.append("import java.util.List;" + newLine() + newLine());
                builder.append("import org.apache.ibatis.annotations.Mapper;" + newLine());
                builder.append("import org.apache.ibatis.annotations.Param;" + newLine() + newLine());
                builder.append("import com.github.mybatisq.Query;" + newLine());
                builder.append("import " + entityPackage + "." + className + ";" + newLine() + newLine());
                builder.append("@Mapper" + newLine());
                builder.append("public interface " + className + "Mapper {" + newLine() + newLine());
                builder.append(space(4) + "int count(Query<" + className + "Table> query);" + newLine() + newLine());
                builder.append(space(4) + "List<" + className + "> select(Query<" + className + "Table> query);" + newLine() + newLine());
                builder.append(space(4) + "int insert(" + className + " " + tableName + ");" + newLine() + newLine());
                builder.append(space(4) + "int update(" + className + " " + tableName + ");" + newLine() + newLine());
                builder.append(space(4) + "int delete(@Param(\"" + fields[0].getName() + "\") " + fields[0].getType().getName().replace("java.lang.", "") + " " + fields[0].getName() + ");" + newLine() + newLine());
                builder.append("}");
                
                String fileContent = builder.toString();
                String filePath = outPath + "/" + className + "Mapper.java";
                try {
                    FileUtils.write(new File(filePath), fileContent, encoding);
                } catch (IOException e) {
                    getLog().error(e);
                }
                getLog().info(filePath);
            });
        } catch (IOException e) {
            getLog().error(e);
            throw new MojoFailureException("Failed to close input stream: com/github/mybatisq/QMapper.xml");
        }
    }
}
