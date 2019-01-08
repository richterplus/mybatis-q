package com.github.mybatisq;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author richterplus
 */
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

    private DatabaseProcessor databaseProcessor;

    private ClassLoader classLoader;

    private final Set<String> aliases = new HashSet<>();

    private Set<String> includeEntityNames;

    private Set<String> excludeEntityNames;

    private String entityPath;

    private String genPath;

    private String mapperPath;

    private ClassLoader getClassLoader() throws MojoExecutionException {
        if (classLoader == null) {
            try {
                List<String> classpathElements = project.getCompileClasspathElements();
                classpathElements.add(project.getBuild().getOutputDirectory());
                classpathElements.add(project.getBuild().getTestOutputDirectory());
                URL[] urls = new URL[classpathElements.size()];

                for (int i = 0; i < classpathElements.size(); ++i) {
                    urls[i] = new File(classpathElements.get(i)).toURI().toURL();
                }
                classLoader = new URLClassLoader(urls, getClass().getClassLoader());
            } catch (MalformedURLException | DependencyResolutionRequiredException e) {
                throw new MojoExecutionException("Couldn't create a classloader.", e);
            }
        }
        return classLoader;
    }

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

    private String lowerCaseFirstChar(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    private List<MappedTable> getTablesForCodeFirst() throws MojoExecutionException, MojoFailureException {
        List<MappedTable> tables;

        URL classPath = getClassLoader().getResource(entityPackage.replace(".", "/"));
        if (classPath == null) {
            getLog().error("Failed to load classpath");
            throw new MojoFailureException("Failed to load classpath");
        }

        File file = new File(classPath.getFile());
        if (file.listFiles() == null) {
            return null;
        }

        try {
            getClassLoader().loadClass(Key.class.getName());
            getClassLoader().loadClass(AutoIncrement.class.getName());
            getClassLoader().loadClass(Ignore.class.getName());
            getClassLoader().loadClass(MapTo.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<Class<?>> classes = new ArrayList<>();
        File[] files = file.listFiles();
        for (File f : files != null ? files : new File[0]) {
            try {
                classes.add(getClassLoader().loadClass(entityPackage + "." + f.getName().replace(".class", "")));
            } catch (ClassNotFoundException e) {
                getLog().error(e);
                throw new MojoFailureException("Can not resolve class name " + entityPackage + "." + f.getName().replace(".class", ""));
            }
        }

        tables = classes.stream().filter(
                c -> (includeEntityNames.size() == 0 || includeEntityNames.contains(c.getSimpleName())) && (excludeEntityNames.size() == 0 || !excludeEntityNames.contains(c.getSimpleName())))
                .map(c -> {
                    List<Field> fields = Arrays.asList(c.getDeclaredFields());
                    Class<?> s = c.getSuperclass();
                    while (s != null) {
                        fields.addAll(Arrays.asList(s.getDeclaredFields()));
                        s = s.getSuperclass();
                    }

                    MapTo tMapTo = c.getAnnotation(MapTo.class);
                    MappedTable table = new MappedTable();
                    table.setOriginalName(tMapTo == null ? c.getSimpleName() : StringUtils.defaultIfEmpty(tMapTo.value(), c.getSimpleName()));
                    table.setMappedName(NamingUtils.generateMappedName(table.getOriginalName()));
                    table.setMappedColumns(fields.stream().filter(f -> !f.isAnnotationPresent(Ignore.class)).map(f -> {
                        MapTo cMapTo = f.getAnnotation(MapTo.class);
                        MappedColumn column = new MappedColumn();
                        column.setOriginalName(cMapTo == null ? f.getName() : StringUtils.defaultIfEmpty(cMapTo.value(), f.getName()));
                        column.setMappedName(NamingUtils.generateMappedName(column.getOriginalName()));
                        column.setDataType(f.getType().getName().replace("java.lang.", ""));
                        column.setIsPrimaryKey(f.isAnnotationPresent(Key.class));
                        column.setIsAutoIncrement(f.isAnnotationPresent(AutoIncrement.class));
                        return column;
                    }).collect(Collectors.toList()));
                    return table;
                }).collect(Collectors.toList());

        return tables;
    }

    private List<MappedTable> getTablesForDatabaseFist() {
        return databaseProcessor.getMappedTables(includeEntityNames, excludeEntityNames);
    }

    private void copyQMapperFiles() throws MojoExecutionException, IOException {
        String fileName = "QMapper.xml";
        if (datasource != null) {
            if (datasource.getUrl().contains(Datasource.MYSQL)) {
                fileName = "QMapper-MySQL.xml";
            } else if (datasource.getUrl().contains(Datasource.POSTGRESQL)) {
                fileName = "QMapper-PostgreSQL.xml";
            }
        }
        InputStream input = getClassLoader().getResourceAsStream("mybatisq/" + fileName);
        if (input == null) {
            throw new RuntimeException("Cannot load QMapper xml file.");
        }
        String content = IOUtils.toString(input, encoding).replace(Query.class.getPackage().getName(), genPackage);
        input.close();
        String path = mapperPath + "/QMapper.xml";
        FileUtils.write(new File(path), content, encoding);
        getLog().info(path);

        input = getClassLoader().getResourceAsStream("mybatisq/QMapper.java.q");
        if (input == null) {
            throw new RuntimeException("Cannot load QMapper java file.");
        }
        content = IOUtils.toString(input, encoding).replace(Query.class.getPackage().getName() + ";", genPackage + ";");
        input.close();
        path = genPath + "/QMapper.java";
        FileUtils.write(new File(path), content, encoding);
        getLog().info(path);
    }

    private void genEntities(Collection<MappedTable> tables) {
        tables.forEach(t -> {
            final StringBuilder builder = new StringBuilder();
            builder.append("package ").append(entityPackage).append(";").append(newLine(2));
            String className = t.getMappedName();
            builder.append(newLine(1)).append("/**");
            if (StringUtils.isNotEmpty(t.getComment())) {
                builder.append(newLine(1)).append(" * ").append(t.getComment());
            }
            builder.append(newLine(1)).append(" * @author richterplus");
            builder.append(newLine(1)).append(" */");
            builder.append(newLine(1)).append("public class ").append(className).append(" {").append(newLine(2));
            t.getMappedColumns().forEach(c -> {
                if (StringUtils.isNotEmpty(c.getComment())) {
                    builder.append(space(4)).append("/**").append(newLine(1));
                    builder.append(space(4)).append(" * ").append(c.getComment()).append(newLine(1));
                    builder.append(space(4)).append(" */").append(newLine(1));
                }
                builder.append(space(4)).append("private ").append(c.getDataType()).append(" ").append(lowerCaseFirstChar(c.getMappedName())).append(";").append(newLine(2));
            });
            t.getMappedColumns().forEach(c -> {
                if (StringUtils.isNotEmpty(c.getComment())) {
                    builder.append(space(4)).append("/**").append(newLine(1));
                    builder.append(space(4)).append(" * 获取").append(c.getComment()).append(newLine(1));
                    builder.append(space(4)).append(" * @return ").append(c.getComment()).append(newLine(1));
                    builder.append(space(4)).append(" */").append(newLine(1));
                }
                builder.append(space(4)).append("public ").append(c.getDataType()).append(" get").append(c.getMappedName()).append("() {").append(newLine(1));
                builder.append(space(8)).append("return ").append(lowerCaseFirstChar(c.getMappedName())).append(";").append(newLine(1));
                builder.append(space(4)).append("}").append(newLine(2));
                if (StringUtils.isNotEmpty(c.getComment())) {
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

    private void genTables(List<MappedTable> tables) {
        tables.forEach(t -> {
            final StringBuilder builder = new StringBuilder();
            builder.append("package ").append(genPackage).append(";").append(newLine(2));
            builder.append("import ").append(Column.class.getName()).append(";").append(newLine(1));
            builder.append("import ").append(Join.class.getName()).append(";").append(newLine(1));
            builder.append("import ").append(DeleteQuery.class.getName()).append(";").append(newLine(1));
            builder.append("import ").append(Query.class.getName()).append(";").append(newLine(1));
            builder.append("import ").append(Insert.class.getName()).append(";").append(newLine(1));
            builder.append("import ").append(Update.class.getName()).append(";").append(newLine(1));
            builder.append("import ").append(Table.class.getName()).append(";").append(newLine(1));
            String className = t.getMappedName() + "Table";
            String tableName = t.getOriginalName();
            String tableAlias = alias(tableName);
            builder.append(newLine(1)).append("/**");
            builder.append(newLine(1)).append(" * @author richterplus");
            builder.append(newLine(1)).append(" */");
            builder.append(newLine(1)).append("public class ").append(className).append(" extends Table {").append(newLine(1));
            builder.append(newLine(1)).append(space(4)).append("private ").append(className).append("() {").append(newLine(1));
            builder.append(space(8)).append("super(\"").append(tableName).append("\", \"").append(tableAlias).append("\");").append(newLine(1));
            builder.append(space(4)).append("}").append(newLine(1));
            builder.append(newLine(1)).append(space(4)).append("public static final ").append(className).append(" ").append(tableName).append(" = new ").append(className).append("();").append(newLine(1));
            builder.append(newLine(1)).append(space(4)).append("public Query<").append(className).append("> query() {").append(newLine(1));
            builder.append(space(8)).append("return new Query<>(").append(tableName).append(");").append(newLine(1));
            builder.append(space(4)).append("}").append(newLine(1));
            builder.append(newLine(1)).append(space(4)).append("public Insert<").append(className).append("> insert() {").append(newLine(1));
            builder.append(space(8)).append("return new Insert<>(").append(tableName).append(");").append(newLine(1));
            builder.append(space(4)).append("}").append(newLine(1));
            builder.append(newLine(1)).append(space(4)).append("public Update<").append(className).append("> update() {").append(newLine(1));
            builder.append(space(8)).append("return new Update<>(").append(tableName).append(");").append(newLine(1));
            builder.append(space(4)).append("}").append(newLine(1));
            builder.append(newLine(1)).append(space(4)).append("public DeleteQuery<").append(className).append("> deleteQuery() {").append(newLine(1));
            builder.append(space(8)).append("return new DeleteQuery<>(").append(tableName).append(");").append(newLine(1));
            builder.append(space(4)).append("}").append(newLine(1));
            builder.append(newLine(1)).append(space(4)).append("public <T extends Table> Join<").append(className).append(", T> inner(T table) {").append(newLine(1));
            builder.append(space(8)).append("return new Join<>(\"inner\", this, table);").append(newLine(1));
            builder.append(space(4)).append("}").append(newLine(1));
            t.getMappedColumns().forEach(c -> {
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
    }

    private void genXmls(List<MappedTable> tables) {
        tables.forEach(t -> {
            final StringBuilder builder = new StringBuilder();
            builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>").append(newLine(1));
            builder.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >").append(newLine(1));
            String className = t.getMappedName();
            String tableName = t.getOriginalName();
            builder.append("<mapper namespace=\"").append(genPackage).append(".").append(className).append("Mapper\">").append(newLine(1));
            builder.append(space(4)).append("<resultMap type=\"").append(entityPackage).append(".").append(className).append("\" id=\"").append(lowerCaseFirstChar(className)).append("\">").append(newLine(1));
            t.getMappedColumns().sort((o1, o2) -> {
                if (o1.getIsPrimaryKey() == o2.getIsPrimaryKey()) {
                    return 0;
                } else if (o1.getIsPrimaryKey()) {
                    return -1;
                } else {
                    return 1;
                }
            });
            t.getMappedColumns().forEach(c -> {
                if (c.getIsPrimaryKey()) {
                    builder.append(space(8)).append("<id column=\"").append(c.getOriginalName()).append("\" property=\"").append(lowerCaseFirstChar(c.getMappedName())).append("\"/>").append(newLine(1));
                } else {
                    builder.append(space(8)).append("<result column=\"").append(c.getOriginalName()).append("\" property=\"").append(lowerCaseFirstChar(c.getMappedName())).append("\"/>").append(newLine(1));
                }
            });

            builder.append(space(4)).append("</resultMap>").append(newLine(2));

            builder.append(space(4)).append("<select id=\"count\" parameterType=\"com.github.mybatisq.Query\" resultType=\"java.lang.Integer\">").append(newLine(1));
            builder.append(space(8)).append("select count(*) <include refid=\"").append(genPackage).append(".QMapper.countFrom\"/>").append(newLine(1));
            builder.append(space(4)).append("</select>").append(newLine(1));

            builder.append(newLine(1)).append(space(4)).append("<select id=\"select\" parameterType=\"com.github.mybatisq.Query\" resultMap=\"").append(lowerCaseFirstChar(className)).append("\">").append(newLine(1));
            builder.append(space(8)).append("select ");
            builder.append("<if test=\"selectedColumns.size > 0\"><foreach collection=\"selectedColumns\" item=\"col\" separator=\",\">${tableAlias}.${col.name}</foreach></if>");
            builder.append("<if test=\"selectedColumns.size == 0\">");
            t.getMappedColumns().stream().map(c -> "${tableAlias}." + c.getOriginalName() + "").reduce((a, b) -> a + "," + b).ifPresent(builder::append);
            builder.append("</if> <include refid=\"").append(genPackage).append(".QMapper.selectFrom\"/>").append(newLine(1));
            builder.append(space(4)).append("</select>").append(newLine(1));

            builder.append(newLine(1)).append(space(4)).append("<insert id=\"insert\" parameterType=\"").append(entityPackage).append(".").append(className).append("\" useGeneratedKeys=\"true\"");
            t.getMappedColumns().stream().filter(c -> c.getIsAutoIncrement() && c.getIsPrimaryKey()).findFirst().ifPresent(c -> builder.append(" keyProperty=\"").append(lowerCaseFirstChar(c.getMappedName())).append("\""));
            builder.append(">").append(newLine(1));
            builder.append(space(8)).append("<trim prefix=\"insert into ").append(tableName).append(" (\" suffix=\")\" suffixOverrides=\",\">\n");
            t.getMappedColumns().forEach(c -> builder.append(space(12)).append("<if test=\"").append(lowerCaseFirstChar(c.getMappedName())).append(" != null\">").append(c.getOriginalName()).append(",</if>").append(newLine(1)));
            builder.append(space(8)).append("</trim>").append(newLine(1));
            builder.append(space(8)).append("<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">").append(newLine(1));
            t.getMappedColumns().forEach(c -> builder.append(space(12)).append("<if test=\"").append(lowerCaseFirstChar(c.getMappedName())).append(" != null\">#{").append(lowerCaseFirstChar(c.getMappedName())).append("},</if>").append(newLine(1)));
            builder.append(space(8)).append("</trim>").append(newLine(1));
            builder.append(space(4)).append("</insert>").append(newLine(1));

            builder.append(newLine(1)).append(space(4)).append("<insert id=\"batchInsert\" useGeneratedKeys=\"true\"");
            t.getMappedColumns().stream().filter(c -> c.getIsAutoIncrement() && c.getIsPrimaryKey()).findFirst().ifPresent(c -> builder.append(" keyProperty=\"").append(lowerCaseFirstChar(c.getMappedName())).append("\""));
            builder.append(">");
            builder.append(newLine(1)).append(space(8)).append("<trim prefix=\"insert into ").append(tableName).append(" (");
            builder.append(t.getMappedColumns().stream().filter(c -> !c.getIsAutoIncrement()).map(c -> "" + c.getOriginalName() + "").reduce((a, b) -> String.join(",", a, b)).orElse(""));
            builder.append(") values \">").append(newLine(1));
            builder.append(space(12)).append("<foreach collection=\"list\" item=\"item\" separator=\",\">").append(newLine(1));
            builder.append(space(16)).append("(").append(t.getMappedColumns().stream().filter(c -> !c.getIsAutoIncrement()).map(c -> "#{item." + lowerCaseFirstChar(c.getMappedName()) + "}").reduce((a, b) -> String.join(",", a, b)).orElse("")).append(")").append(newLine(1));
            builder.append(space(12)).append("</foreach>").append(newLine(1));
            builder.append(space(8)).append("</trim>").append(newLine(1));
            builder.append(space(4)).append("</insert>").append(newLine(1));

            builder.append(newLine(1)).append(space(4)).append("<insert id=\"insertBySelect\" parameterType=\"com.github.mybatisq.Insert\">");
            builder.append(newLine(1)).append(space(8)).append("insert ${table.name}");
            builder.append(newLine(1)).append(space(8)).append("<foreach collection=\"insertColumns\" item=\"c\" open=\"(\" close=\")\" separator=\",\">${c.name}</foreach>");
            builder.append(newLine(1)).append(space(8)).append("select <foreach collection=\"selectedColumns\" item=\"col\" separator=\",\">${tableAlias}.${col.name}</foreach>");
            builder.append(newLine(1)).append(space(8)).append("<include refid=\"").append(genPackage).append(".QMapper.selectFrom\"/>");
            builder.append(newLine(1)).append(space(4)).append("</insert>").append(newLine(1));

            Optional<MappedColumn> keyColumn = t.getMappedColumns().stream().filter(MappedColumn::getIsPrimaryKey).findFirst();
            if (!keyColumn.isPresent()) {
                throw new RuntimeException("Table " + tableName + " must has a primary key column.");
            }

            builder.append(newLine(1)).append(space(4)).append("<update id=\"update\" parameterType=\"").append(entityPackage).append(".").append(className).append("\">").append(newLine(1));
            builder.append(space(8)).append("<trim prefix=\"update ").append(tableName).append(" set\" suffix=\"where ").append(keyColumn.get().getOriginalName()).append("=#{").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append("}\" suffixOverrides=\",\">").append(newLine(1));
            t.getMappedColumns().stream().filter(c -> !c.getIsPrimaryKey()).forEach(c -> builder.append(space(12)).append("<if test=\"").append(lowerCaseFirstChar(c.getMappedName())).append(" != null\">").append(c.getOriginalName()).append("=#{").append(lowerCaseFirstChar(c.getMappedName())).append("},</if>").append(newLine(1)));
            builder.append(space(8)).append("</trim>").append(newLine(1));
            builder.append(space(4)).append("</update>").append(newLine(1));

            builder.append(newLine(1)).append(space(4)).append("<update id=\"batchUpdate\">").append(newLine(1));
            builder.append(space(8)).append("<trim prefix=\"update ").append(tableName).append(" set\" suffixOverrides=\",\">");
            t.getMappedColumns().stream().filter(c -> !c.getIsPrimaryKey()).forEach(c -> builder.append(newLine(1)).append(space(12)).append("<foreach collection=\"entityList\" item=\"item\" open=\"").append(c.getOriginalName()).append("=case ").append(keyColumn.get().getOriginalName()).append(" \" close=\" end,\" separator=\" \">when #{item.").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append("} then #{").append(lowerCaseFirstChar(c.getMappedName())).append("}</foreach>"));
            builder.append(newLine(1)).append(space(8)).append("</trim>");
            builder.append(newLine(1)).append(space(8)).append("<foreach collection=\"list\" item=\"item\" open=\"where ").append(keyColumn.get().getOriginalName()).append(" in (\" close=\")\" separator=\",\">#{item.").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append("}").append("</foreach>");
            builder.append(newLine(1)).append(space(4)).append("</update>").append(newLine(1));

            builder.append(newLine(1)).append(space(4)).append("<update id=\"updateByBuilder\" parameterType=\"").append(Update.class.getName()).append("\">").append(newLine(1));
            builder.append(space(8)).append("<trim prefix=\"update ").append(tableName).append("\">").append(newLine(1));
            builder.append(space(12)).append("<include refid=\"").append(genPackage).append(".QMapper.updateBuilder\"/>").append(newLine(1));
            builder.append(space(8)).append("</trim>").append(newLine(1));
            builder.append(space(4)).append("</update>").append(newLine(1));

            builder.append(newLine(1)).append(space(4)).append("<delete id=\"delete\">").append(newLine(1));
            builder.append(space(8)).append("delete from ").append(tableName).append(" where ").append(keyColumn.get().getOriginalName()).append("=#{").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append("}").append(newLine(1));
            builder.append(space(4)).append("</delete>").append(newLine(1));

            builder.append(newLine(1)).append(space(4)).append("<delete id=\"batchDelete\">").append(newLine(1));
            builder.append(space(8)).append("<trim prefix=\"delete from ").append(tableName).append(" where ").append(keyColumn.get().getOriginalName()).append(" in (\" suffix=\")\"><foreach collection=\"").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append("List\" separator=\",\" item=\"item\">#{item}</foreach></trim>").append(newLine(1));
            builder.append(space(4)).append("</delete>").append(newLine(1));

            builder.append(newLine(1)).append(space(4)).append("<delete id=\"deleteByQuery\" parameterType=\"com.github.mybatisq.DeleteQuery\">").append(newLine(1));
            builder.append(space(8)).append("<trim prefix=\"delete from ").append(tableName).append("\"><include refid=\"").append(genPackage).append(".QMapper.deleteWhere\"/></trim>").append(newLine(1));
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
    }

    private void genMappers(List<MappedTable> tables) {
        tables.forEach(t -> {
            final StringBuilder builder = new StringBuilder();

            String className = t.getMappedName();
            String tableName = t.getOriginalName();

            Optional<MappedColumn> keyColumn = t.getMappedColumns().stream().filter(MappedColumn::getIsPrimaryKey).findFirst();
            if (!keyColumn.isPresent()) {
                throw new RuntimeException("Table " + tableName + " must has a primary key column.");
            }

            builder.append("package ").append(genPackage).append(";").append(newLine(2));
            builder.append("import ").append(Collection.class.getName()).append(";").append(newLine(1));
            builder.append("import ").append(List.class.getName()).append(";").append(newLine(2));
            builder.append("import ").append(Mapper.class.getName()).append(";").append(newLine(1));
            builder.append("import ").append(Param.class.getName()).append(";").append(newLine(2));
            builder.append("import ").append(DeleteQuery.class.getName()).append(";").append(newLine(1));
            builder.append("import ").append(Query.class.getName()).append(";").append(newLine(1));
            builder.append("import ").append(Insert.class.getName()).append(";").append(newLine(1));
            builder.append("import ").append(Update.class.getName()).append(";").append(newLine(1));
            builder.append("import ").append(entityPackage).append(".").append(className).append(";").append(newLine(1));
            builder.append(newLine(1)).append("/**");
            builder.append(newLine(1)).append(" * @author richterplus");
            builder.append(newLine(1)).append(" */");
            builder.append(newLine(1)).append("@Mapper").append(newLine(1));
            builder.append("public interface ").append(className).append("Mapper {").append(newLine(2));
            builder.append(space(4)).append("int count(Query<").append(className).append("Table> query);").append(newLine(2));
            builder.append(space(4)).append("List<").append(className).append("> select(Query<").append(className).append("Table> query);").append(newLine(2));
            builder.append(space(4)).append("int insert(").append(className).append(" ").append(lowerCaseFirstChar(className)).append(");").append(newLine(2));
            builder.append(space(4)).append("int batchInsert(@Param(\"list\") Collection<").append(className).append("> ").append(lowerCaseFirstChar(className)).append(");").append(newLine(2));
            builder.append(space(4)).append("int insertBySelect(Insert<").append(className).append("Table> insert);").append(newLine(2));
            builder.append(space(4)).append("int update(").append(className).append(" ").append(lowerCaseFirstChar(className)).append(");").append(newLine(2));
            builder.append(space(4)).append("int batchUpdate(@Param(\"list\") Collection<").append(className).append("> ").append(lowerCaseFirstChar(className)).append(");").append(newLine(2));
            builder.append(space(4)).append("int updateByBuilder(Update<").append(className).append("Table> update);").append(newLine(2));
            builder.append(space(4)).append("int delete(@Param(\"").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append("\") ").append(keyColumn.get().getDataType()).append(" ").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append(");").append(newLine(2));
            builder.append(space(4)).append("int batchDelete(@Param(\"").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append("List\") Collection<").append(keyColumn.get().getDataType()).append("> ").append(lowerCaseFirstChar(keyColumn.get().getMappedName())).append("List);").append(newLine(2));
            builder.append(space(4)).append("int deleteByQuery(DeleteQuery<").append(className).append("Table> query);").append(newLine(2));
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
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("entity package: " + entityPackage);
        getLog().info("gen package: " + genPackage);

        includeEntityNames = Stream.of(StringUtils.defaultIfEmpty(includeEntities, "").split(",")).map(String::trim).filter(e -> e.length() > 0).collect(Collectors.toSet());
        excludeEntityNames = Stream.of(StringUtils.defaultIfEmpty(excludeEntities, "").split(",")).map(String::trim).filter(e -> e.length() > 0).collect(Collectors.toSet());

        List<MappedTable> tables;

        boolean genEntities = false;

        if (datasource == null) {
            tables = getTablesForCodeFirst();
        } else {
            databaseProcessor = DatabaseProcessorFactory.createMapperFromDatasource(datasource);
            tables = getTablesForDatabaseFist();
            genEntities = true;
        }

        if (tables == null || tables.size() == 0) { return; }

        entityPath = project.getBuild().getSourceDirectory() + "/" + entityPackage.replace(".", "/");
        genPath = project.getBuild().getSourceDirectory() + "/" + genPackage.replace(".", "/");
        mapperPath = project.getBuild().getSourceDirectory().substring(0, project.getBuild().getSourceDirectory().length() - 4) + "resources/" + mapperFolder;

        try {
            copyQMapperFiles();

            if (genEntities) {
                genEntities(tables);
            }

            genTables(tables);

            genXmls(tables);

            genMappers(tables);
        } catch (IOException e) {
            getLog().error(e);
            throw new MojoFailureException("Failed to close input stream: com/github/mybatisq/QMapper.xml");
        }
    }
}
