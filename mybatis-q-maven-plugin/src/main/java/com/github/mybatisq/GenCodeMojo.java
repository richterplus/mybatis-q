package com.github.mybatisq;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * 包含的数据实体
     */
    @Parameter
    private String includeEntities;

    /**
     * 不包含的数据实体
     */
    @Parameter
    private String excludeEntities;

    /**
     * 数据源
     */
    @Parameter
    private Datasource datasource;

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

    /**
     * 生成的mapper xml文件所在目录
     */
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
                urls[i] = new File(classpathElements.get(i)).toURI().toURL();
            }
            return new URLClassLoader(urls, getClass().getClassLoader());
        } catch (MalformedURLException | DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Couldn't create a classloader.", e);
        }
    }

    private final Set<String> aliases = new HashSet<>();

    private String space(int count) {
        StringBuilder space = new StringBuilder();
        for (int i = 0; i < count; i++) {
            space.append(" ");
        }
        return space.toString();
    }

    private String newLine(int count) {
        StringBuilder newLine = new StringBuilder();
        for (int i = 0; i < count; i++) {
            newLine.append("\n");
        }
        return newLine.toString();
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

    private String mapDbTypeToJavaType(String dbType) {
        String javaType = "String";
        if (dbType.contains("bigint")) {
            javaType = "Long";
        } else if (dbType.contains("int")) {
            javaType = "Integer";
        } else if (dbType.startsWith("bit")) {
            javaType = "Boolean";
        } else if (dbType.startsWith("float")) {
            javaType = "Float";
        } else if (dbType.startsWith("double")) {
            javaType = "Double";
        } else if (dbType.startsWith("decimal")) {
            javaType = "java.math.BigDecimal";
        } else if (dbType.contains("date")) {
            javaType = "java.util.Date";
        } else if (dbType.contains("time")) {
            javaType = "java.util.Date";
        } else if (dbType.contains("binary")) {
            javaType = "byte[]";
        } else if (dbType.contains("blob")) {
            javaType = "byte[]";
        }
        return javaType;
    }

    private String generateMappedName(String originalName) {
        String[] splitWords = originalName.split("[_-]");
        for (int i = 0; i < splitWords.length; i++) {
            splitWords[i] = splitWords[i].substring(0, 1).toUpperCase() + splitWords[i].substring(1);
        }
        return String.join("", splitWords);
    }

    private String lowerCaseFirstChar(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("entity package: " + entityPackage);
        getLog().info("gen package: " + genPackage);

        if (includeEntities == null)
            includeEntities = "";
        if (excludeEntities == null)
            excludeEntities = "";

        Set<String> includeEntityNames = Stream.of(includeEntities.split(",")).map(String::trim).filter(e -> e.length() > 0).collect(Collectors.toSet());

        Set<String> excludedEntityNames = Stream.of(excludeEntities.split(",")).map(String::trim).filter(e -> e.length() > 0).collect(Collectors.toSet());

        List<Table> tables;

        ClassLoader cl = getClassLoader();

        boolean genEntities = false;

        if (datasource == null) {
            URL classPath = cl.getResource(entityPackage.replace(".", "/"));
            if (classPath == null) {
                getLog().error("Failed to load classpath");
                throw new MojoFailureException("Failed to load classpath");
            }

            File file = new File(classPath.getFile());
            if (file.listFiles() == null) {
                return;
            }

            List<Class<?>> classes = new ArrayList<>();
            File[] files = file.listFiles();
            for (File f : files != null ? files : new File[0]) {
                try {
                    classes.add(cl.loadClass(entityPackage + "." + f.getName().replace(".class", "")));
                } catch (ClassNotFoundException e) {
                    getLog().error(e);
                    throw new MojoFailureException("Can not resolve class name " + entityPackage + "." + f.getName().replace(".class", ""));
                }
            }

            tables = classes.stream().filter(
                    c -> (includeEntityNames.size() == 0 || includeEntityNames.contains(c.getSimpleName())) && (excludedEntityNames.size() == 0 || !excludedEntityNames.contains(c.getSimpleName())))
                    .map(c -> {
                        Table table = new Table();
                        table.setOriginalName(c.getSimpleName());
                        table.setMappedName(generateMappedName(table.getOriginalName()));
                        table.setColumns(Stream.of(c.getDeclaredFields()).map(f -> {
                            Column column = new Column();
                            column.setOriginalName(f.getName());
                            column.setMappedName(generateMappedName(column.getOriginalName()));
                            column.setDataType(f.getType().getName().replace("java.lang.", ""));
                            return column;
                        }).collect(Collectors.toList()));
                        return table;
                    }).collect(Collectors.toList());

            tables.forEach(t -> {
                t.getColumns().get(0).setIsPrimaryKey(true);
                t.getColumns().get(0).setIsAutoIncrement(true);
            });
        } else {
            Connection conn;
            Statement statement;
            ResultSet resultSet;
            try {
                Class.forName(datasource.getDriverClassName());
                conn = DriverManager.getConnection(datasource.getUrl(), datasource.getUsername(), datasource.getPassword());
                statement = conn.createStatement();
                resultSet = statement.executeQuery("show table status");
                tables = new ArrayList<>();
                while (resultSet.next()) {
                    Table table = new Table();
                    table.setOriginalName(resultSet.getString("Name"));
                    table.setMappedName(generateMappedName(table.getOriginalName()));
                    table.setComment(resultSet.getString("Comment"));
                    table.setColumns(new ArrayList<>());
                    tables.add(table);
                }
                resultSet.close();
                statement.close();

                for (Table table : tables) {
                    statement = conn.createStatement();
                    resultSet = statement.executeQuery("show full columns from `" + table.getOriginalName() + "`");
                    while (resultSet.next()) {
                        Column column = new Column();
                        column.setComment(resultSet.getString("Comment"));
                        column.setDataType(mapDbTypeToJavaType(resultSet.getString("Type")));
                        column.setIsAutoIncrement(resultSet.getString("Extra").contains("auto_increment"));
                        column.setIsPrimaryKey(resultSet.getString("Key").contains("PRI"));
                        column.setOriginalName(resultSet.getString("Field"));
                        column.setMappedName(generateMappedName(column.getOriginalName()));
                        table.getColumns().add(column);
                    }
                    resultSet.close();
                    statement.close();
                }

                genEntities = true;
                resultSet.close();
                statement.close();
                conn.close();
            } catch (ClassNotFoundException | SQLException e) {
                throw new MojoFailureException("Failed to initialize datasource driver class", e);
            }
        }

        String entityPath = project.getBuild().getSourceDirectory() + "/" + entityPackage.replace(".", "/");
        String genPath = project.getBuild().getSourceDirectory() + "/" + genPackage.replace(".", "/");
        String mapperPath = project.getBuild().getSourceDirectory().substring(0, project.getBuild().getSourceDirectory().length() - 4) + "resources/" + mapperFolder;

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
            path = genPath + "/QMapper.java";
            FileUtils.write(new File(path), content, encoding);
            getLog().info(path);

            if (genEntities) {
                tables.forEach(t -> {
                    final StringBuilder builder = new StringBuilder();
                    builder.append("package ").append(entityPackage).append(";").append(newLine(2));
                    String className = t.getMappedName();
                    if (t.getComment() != null && t.getComment().length() > 0) {
                        builder.append("/**").append(newLine(1));
                        builder.append(" * ").append(t.getComment()).append(newLine(1));
                        builder.append(" */").append(newLine(1));
                    }
                    builder.append("public class ").append(className).append(" {").append(newLine(2));
                    t.getColumns().forEach(c -> {
                        if (c.getComment() != null && c.getComment().length() > 0) {
                            builder.append(space(4)).append("/**").append(newLine(1));
                            builder.append(space(4)).append(" * ").append(c.getComment()).append(newLine(1));
                            builder.append(space(4)).append(" */").append(newLine(1));
                        }
                        builder.append(space(4)).append("private ").append(c.getDataType()).append(" ").append(lowerCaseFirstChar(c.getMappedName())).append(";").append(newLine(2));
                    });
                    t.getColumns().forEach(c -> {
                        if (c.getComment() != null && c.getComment().length() > 0) {
                            builder.append(space(4)).append("/**").append(newLine(1));
                            builder.append(space(4)).append(" * 获取").append(c.getComment()).append(newLine(1));
                            builder.append(space(4)).append(" * @return ").append(c.getComment()).append(newLine(1));
                            builder.append(space(4)).append(" */").append(newLine(1));
                        }
                        builder.append(space(4)).append("public ").append(c.getDataType()).append(" get").append(c.getMappedName()).append("() {").append(newLine(1));
                        builder.append(space(8)).append("return ").append(lowerCaseFirstChar(c.getMappedName())).append(";").append(newLine(1));
                        builder.append(space(4)).append("}").append(newLine(2));
                        if (c.getComment() != null && c.getComment().length() > 0) {
                            builder.append(space(4)).append("/**").append(newLine(1));
                            builder.append(space(4)).append(" * 设置").append(c.getComment()).append(newLine(1));
                            builder.append(space(4)).append(" * @param ").append(lowerCaseFirstChar(c.getMappedName())).append(" ").append(c.getComment()).append(newLine(1));
                            builder.append(space(4)).append(" */").append(newLine(1));
                        }
                        builder.append(space(4)).append("public void set").append(c.getMappedName()).append("(").append(c.getDataType()).append(" ").append(lowerCaseFirstChar(c.getMappedName())).append(") {").append(newLine(1));
                        builder.append(space(8)).append("this.").append(lowerCaseFirstChar(c.getMappedName())).append(" = ").append(lowerCaseFirstChar(c.getMappedName())).append(";").append(newLine(1));
                        builder.append(space(4)).append("}").append(newLine(2));
                    });
                    builder.append("}");
                    String fileContent = builder.toString();
                    String filePath = entityPath + "/" + className + ".java";
                    try {
                        FileUtils.write(new File(filePath), fileContent, encoding);
                    } catch (IOException e) {
                        getLog().error(e);
                    }
                    getLog().info(filePath);
                });
            }

            tables.forEach(t -> {
                final StringBuilder builder = new StringBuilder();
                builder.append("package ").append(genPackage).append(";").append(newLine(2));
                builder.append("import com.github.mybatisq.Column;").append(newLine(1));
                builder.append("import com.github.mybatisq.Join;").append(newLine(1));
                builder.append("import com.github.mybatisq.Query;").append(newLine(1));
                builder.append("import com.github.mybatisq.Table;").append(newLine(1));
                String className = t.getMappedName() + "Table";
                String tableName = t.getOriginalName();
                String tableAlias = alias(tableName);
                builder.append(newLine(1)).append("public class ").append(className).append(" extends Table {").append(newLine(1));
                builder.append(newLine(1)).append(space(4)).append("private ").append(className).append("() {").append(newLine(1));
                builder.append(space(8)).append("super(\"").append(tableName).append("\", \"").append(tableAlias).append("\");").append(newLine(1));
                builder.append(space(4)).append("}").append(newLine(1));
                builder.append(newLine(1)).append(space(4)).append("public static final ").append(className).append(" ").append(tableName).append(" = new ").append(className).append("();").append(newLine(1));
                builder.append(newLine(1)).append(space(4)).append("public Query<").append(className).append("> query() {").append(newLine(1));
                builder.append(space(8)).append("return new Query<>(").append(tableName).append(");").append(newLine(1));
                builder.append(space(4)).append("}").append(newLine(1));
                builder.append(newLine(1)).append(space(4)).append("public <T extends Table> Join<").append(className).append(", T> inner(T table) {").append(newLine(1));
                builder.append(space(8)).append("return new Join<>(\"inner\", this, table);").append(newLine(1));
                builder.append(space(4)).append("}").append(newLine(1));
                t.getColumns().forEach(c -> {
                    if (c.getComment() != null && c.getComment().trim().length() > 0) {
                        builder.append(newLine(1)).append(space(4)).append("/**").append(newLine(1));
                        builder.append(space(4)).append(" * ").append(c.getComment()).append(newLine(1));
                        builder.append(space(4)).append(" */");
                    }
                    builder.append(newLine(1)).append(space(4)).append("public Column<").append(className).append(", ").append(c.getDataType()).append("> ").append(c.getOriginalName()).append(" = new Column<>(\"").append(c.getOriginalName()).append("\");").append(newLine(1));
                });
                builder.append("}");
                String fileContent = builder.toString();
                String filePath = genPath + "/" + className + ".java";
                try {
                    FileUtils.write(new File(filePath), fileContent, encoding);
                } catch (IOException e) {
                    getLog().error(e);
                }
                getLog().info(filePath);
            });

            tables.forEach(t -> {
                final StringBuilder builder = new StringBuilder();
                builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>").append(newLine(1));
                builder.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >").append(newLine(1));
                String className = t.getMappedName();
                String tableName = t.getOriginalName();
                builder.append("<mapper namespace=\"").append(genPackage).append(".").append(className).append("Mapper\">").append(newLine(1));
                builder.append(space(4)).append("<resultMap type=\"").append(entityPackage).append(".").append(className).append("\" id=\"").append(lowerCaseFirstChar(className)).append("\">").append(newLine(1));
                t.getColumns().forEach(c -> {
                    if (c.getIsPrimaryKey()) {
                        builder.append(space(8)).append("<id column=\"").append(c.getOriginalName()).append("\" property=\"").append(lowerCaseFirstChar(c.getMappedName())).append("\"/>").append(newLine(1));
                    } else {
                        builder.append(space(8)).append("<result column=\"").append(c.getOriginalName()).append("\" property=\"").append(lowerCaseFirstChar(c.getMappedName())).append("\"/>").append(newLine(1));
                    }
                });
                builder.append(space(4)).append("</resultMap>").append(newLine(2));
                builder.append(space(4)).append("<select id=\"count\" parameterType=\"com.github.mybatisq.Query\" resultType=\"java.lang.Integer\">").append(newLine(1));
                builder.append(space(8)).append("select count(0) <include refid=\"").append(genPackage).append(".QMapper.countFrom\"/>").append(newLine(1));
                builder.append(space(4)).append("</select>").append(newLine(1));
                builder.append(newLine(1)).append(space(4)).append("<select id=\"select\" parameterType=\"com.github.mybatisq.Query\" resultMap=\"").append(lowerCaseFirstChar(className)).append("\">").append(newLine(1));
                builder.append(space(8)).append("select ");
                builder.append("<if test=\"selectedColumns.size > 0\"><foreach collection=\"selectedColumns\" item=\"col\" separator=\",\">${tableAlias}.`${col.name}`</foreach></if>");
                builder.append("<if test=\"selectedColumns.size == 0\">");
                t.getColumns().stream().map(c -> "${tableAlias}.`" + c.getOriginalName() + "`").reduce((a, b) -> a + "," + b).ifPresent(builder::append);
                builder.append("</if> <include refid=\"").append(genPackage).append(".QMapper.selectFrom\"/>").append(newLine(1));
                builder.append(space(4)).append("</select>").append(newLine(1));

                builder.append(newLine(1)).append(space(4)).append("<insert id=\"insert\" parameterType=\"").append(entityPackage).append(".").append(className).append("\" useGeneratedKeys=\"true\"");
                t.getColumns().stream().filter(c -> c.getIsAutoIncrement() && c.getIsPrimaryKey()).findFirst().ifPresent(c -> builder.append(" keyProperty=\"").append(lowerCaseFirstChar(c.getMappedName())).append("\""));
                builder.append(">").append(newLine(1));
                builder.append(space(8)).append("<trim prefix=\"insert `").append(tableName).append("` (\" suffix=\")\" suffixOverrides=\",\">\n");
                t.getColumns().forEach(c -> builder.append(space(12)).append("<if test=\"").append(lowerCaseFirstChar(c.getMappedName())).append(" != null\">`").append(c.getOriginalName()).append("`,</if>").append(newLine(1)));
                builder.append(space(8)).append("</trim>").append(newLine(1));
                builder.append(space(8)).append("<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">").append(newLine(1));
                t.getColumns().forEach(c -> builder.append(space(12)).append("<if test=\"").append(lowerCaseFirstChar(c.getMappedName())).append(" != null\">#{").append(lowerCaseFirstChar(c.getMappedName())).append("},</if>").append(newLine(1)));
                builder.append(space(8)).append("</trim>").append(newLine(1));
                builder.append(space(4)).append("</insert>").append(newLine(1));

                builder.append(newLine(1)).append(space(4)).append("<insert id=\"batchInsert\" useGeneratedKeys=\"true\">");
                builder.append(newLine(1)).append(space(8)).append("<foreach collection=\"entityList\" item=\"item\" separator=\";\">");
                builder.append(newLine(1)).append(space(12)).append("<trim prefix=\"insert `").append(tableName).append("` (\" suffix=\")\" suffixOverrides=\",\">").append(newLine(1));
                t.getColumns().forEach(c -> builder.append(space(16)).append("<if test=\"item.").append(lowerCaseFirstChar(c.getMappedName())).append(" != null\">`").append(c.getOriginalName()).append("`,</if>").append(newLine(1)));
                builder.append(space(12)).append("</trim>").append(newLine(1));
                builder.append(space(12)).append("<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">").append(newLine(1));
                t.getColumns().forEach(c -> builder.append(space(16)).append("<if test=\"item.").append(lowerCaseFirstChar(c.getMappedName())).append(" != null\">#{item.").append(lowerCaseFirstChar(c.getMappedName())).append("},</if>").append(newLine(1)));
                builder.append(space(12)).append("</trim>").append(newLine(1));
                builder.append(newLine(1)).append(space(8)).append("</foreach>").append(newLine(1));
                builder.append(newLine(1)).append(space(4)).append("</insert>");

                Optional<Column> keyColumn = t.getColumns().stream().filter(Column::getIsPrimaryKey).findFirst();
                if (!keyColumn.isPresent()) {
                    throw new RuntimeException("Table " + tableName + " must has a primary key column.");
                }
                builder.append(newLine(1)).append(space(4)).append("<update id=\"update\" parameterType=\"").append(entityPackage).append(".").append(className).append("\">").append(newLine(1));
                builder.append(space(8)).append("<trim prefix=\"update `").append(tableName).append("` set\" suffix=\"where `").append(keyColumn.get().getOriginalName()).append("`=#{").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append("}\" suffixOverrides=\",\">").append(newLine(1));
                t.getColumns().stream().filter(c -> !c.getIsPrimaryKey()).forEach(c -> builder.append(space(12)).append("<if test=\"").append(lowerCaseFirstChar(c.getMappedName())).append(" != null\">`").append(c.getOriginalName()).append("`=#{").append(lowerCaseFirstChar(c.getMappedName())).append("},</if>").append(newLine(1)));
                builder.append(space(8)).append("</trim>").append(newLine(1));
                builder.append(space(4)).append("</update>").append(newLine(1));

                builder.append(newLine(1)).append(space(4)).append("<update id=\"batchUpdate\">").append(newLine(1));
                builder.append(newLine(1)).append(space(8)).append("<foreach collection=\"entityList\" item=\"item\" separator=\";\">").append(newLine(1));
                builder.append(space(12)).append("<trim prefix=\"update `").append(tableName).append("` set\" suffix=\"where `").append(keyColumn.get().getOriginalName()).append("`=#{item.").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append("}\" suffixOverrides=\",\">").append(newLine(1));
                t.getColumns().stream().filter(c -> !c.getIsPrimaryKey()).forEach(c -> builder.append(space(16)).append("<if test=\"item.").append(lowerCaseFirstChar(c.getMappedName())).append(" != null\">`").append(c.getOriginalName()).append("`=#{item.").append(lowerCaseFirstChar(c.getMappedName())).append("},</if>").append(newLine(1)));
                builder.append(space(12)).append("</trim>").append(newLine(1));
                builder.append(newLine(1)).append(space(8)).append("</foreach>").append(newLine(1));
                builder.append(newLine(1)).append(space(4)).append("</update>");

                builder.append(newLine(1)).append(space(4)).append("<update id=\"batchUpdateByCase\">").append(newLine(1));
                builder.append(newLine(1)).append(space(8)).append("<trim prefix=\"update `").append(tableName).append("` set\" suffixOverrides=\",\">");
                t.getColumns().stream().filter(c -> !c.getIsPrimaryKey()).forEach(c -> builder.append(newLine(1)).append(space(12)).append("<foreach collection=\"entityList\" item=\"item\" open=\"`").append(c.getOriginalName()).append("`=case `").append(keyColumn.get().getOriginalName()).append("` \" close=\" end,\" separator=\" \">when #{item.").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append("} then #{").append(lowerCaseFirstChar(c.getMappedName())).append("}</foreach>"));
                builder.append(newLine(1)).append(space(8)).append("</trim>");
                builder.append(newLine(1)).append(space(8)).append("<foreach collection=\"entityList\" item=\"item\" open=\"where `").append(keyColumn.get().getOriginalName()).append("` in (\" close=\")\" separator=\",\">#{item.").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append("}").append("</foreach>");
                builder.append(newLine(1)).append(space(4)).append("</update>");

                builder.append(newLine(1)).append(space(4)).append("<delete id=\"delete\">").append(newLine(1));
                builder.append(space(8)).append("delete from `").append(tableName).append("` where ").append(keyColumn.get().getOriginalName()).append("=#{").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append("}").append(newLine(1));
                builder.append(space(4)).append("</delete>").append(newLine(1));

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

            tables.forEach(t -> {
                final StringBuilder builder = new StringBuilder();

                String className = t.getMappedName();
                String tableName = t.getOriginalName();

                Optional<Column> keyColumn = t.getColumns().stream().filter(Column::getIsPrimaryKey).findFirst();
                if (!keyColumn.isPresent()) {
                    throw new RuntimeException("Table " + tableName + " must has a primary key column.");
                }

                builder.append("package ").append(genPackage).append(";").append(newLine(2));
                builder.append("import java.util.List;").append(newLine(2));
                builder.append("import org.apache.ibatis.annotations.Mapper;").append(newLine(1));
                builder.append("import org.apache.ibatis.annotations.Param;").append(newLine(2));
                builder.append("import com.github.mybatisq.Query;").append(newLine(1));
                builder.append("import ").append(entityPackage).append(".").append(className).append(";").append(newLine(2));
                builder.append("@Mapper").append(newLine(1));
                builder.append("public interface ").append(className).append("Mapper {").append(newLine(2));
                builder.append(space(4)).append("int count(Query<").append(className).append("Table> query);").append(newLine(2));
                builder.append(space(4)).append("List<").append(className).append("> select(Query<").append(className).append("Table> query);").append(newLine(2));
                builder.append(space(4)).append("int insert(").append(className).append(" ").append(lowerCaseFirstChar(className)).append(");").append(newLine(2));
                builder.append(space(4)).append("int batchInsert(@Param(\"entityList\") List<").append(className).append("> ").append(lowerCaseFirstChar(className)).append(");").append(newLine(2));
                builder.append(space(4)).append("int update(").append(className).append(" ").append(lowerCaseFirstChar(className)).append(");").append(newLine(2));
                builder.append(space(4)).append("int batchUpdate(@Param(\"entityList\") List<").append(className).append("> ").append(lowerCaseFirstChar(className)).append(");").append(newLine(2));
                builder.append(space(4)).append("int delete(@Param(\"").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append("\") ").append(keyColumn.get().getDataType()).append(" ").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append(");").append(newLine(2));
                builder.append("}");

                String fileContent = builder.toString();
                String filePath = genPath + "/" + className + "Mapper.java";
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
