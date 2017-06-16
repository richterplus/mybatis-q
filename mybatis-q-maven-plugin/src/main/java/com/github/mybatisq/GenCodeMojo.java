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
    
    private String newLine(int count) {
    	String newLine = "";
    	for (int i = 0; i < count; i++) {
    		newLine += newLine();
    	}
    	return newLine;
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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("entity package: " + entityPackage);
        getLog().info("gen package: " + genPackage);
        
        if (includeEntities == null) includeEntities = "";
        if (excludeEntities == null) excludeEntities = "";
        
        Set<String> includeEntityNames = Stream.of(includeEntities.split(","))
    			.map(e -> e.trim()).filter(e -> e.length() > 0).collect(Collectors.toSet());
        
        Set<String> excludedEntityNames = Stream.of(excludeEntities.split(","))
    			.map(e -> e.trim()).filter(e -> e.length() > 0).collect(Collectors.toSet());
        
        List<Table> tables = null;
        
        ClassLoader cl = getClassLoader();
        
        boolean genEntities = false;
        
        if (datasource == null) {
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
            
            tables  = classes.stream()
            		.filter(c -> (includeEntityNames.size() == 0 || includeEntityNames.contains(c.getSimpleName()))
            		&& (excludedEntityNames.size() == 0 || !excludedEntityNames.contains(c.getSimpleName())))
            		.map(c -> {
            			Table table = new Table();
            			table.setName(c.getSimpleName());
            			table.setColumns(Stream.of(c.getDeclaredFields()).map(f -> {
            				Column column = new Column();
            				column.setName(f.getName());
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
        	Connection conn = null;
        	Statement statement = null;
        	ResultSet resultSet = null;
        	try {
				Class.forName(datasource.getDriverClassName());
				conn = DriverManager.getConnection(datasource.getUrl(), datasource.getUsername(), datasource.getPassword());
				statement = conn.createStatement();
				resultSet = statement.executeQuery("show table status");
				tables = new ArrayList<>();
				while (resultSet.next()) {
					Table table = new Table();
					table.setName(resultSet.getString("Name"));
					table.setComment(resultSet.getString("Comment"));
					table.setColumns(new ArrayList<>());
					tables.add(table);
				}
				resultSet.close();
				statement.close();
				
				for (Table table : tables) {
					statement = conn.createStatement();
					resultSet = statement.executeQuery("show full columns from `" + table.getName() + "`");
					while (resultSet.next()) {
						Column column = new Column();
						column.setComment(resultSet.getString("Comment"));
						column.setDataType(mapDbTypeToJavaType(resultSet.getString("Type")));
						column.setIsAutoIncrement(resultSet.getString("Extra").contains("auto_increment"));
						column.setIsPrimaryKey(resultSet.getString("Key").contains("PRI"));
						column.setName(resultSet.getString("Field"));
						table.getColumns().add(column);
					}
					resultSet.close();
					statement.close();
					table.setName(table.getName().substring(0, 1).toUpperCase() + table.getName().substring(1));
				}
				
				genEntities = true;
        	} catch (ClassNotFoundException | SQLException e) {
				throw new MojoFailureException("Failed to initialize datasource driver class", e);
			} finally {
				try {
					if (resultSet != null) {
						resultSet.close();
					}
					if (statement != null) {
						statement.close();
					}
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					throw new MojoFailureException("Failed to close datasource connection", e);
				}
			}
        }

        String entityPath = project.getBuild().getSourceDirectory() + "/" + entityPackage.replace(".", "/");
        String genPath = project.getBuild().getSourceDirectory() + "/" + genPackage.replace(".", "/");
        String mapperPath = project.getBuild().getSourceDirectory()
        		.substring(0, project.getBuild().getSourceDirectory().length() - 4) + "resources/" + mapperFolder;

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
            		builder.append("package " + entityPackage + ";" + newLine(2));
            		String className = t.getName();
            		if (t.getComment() != null && t.getComment().length() > 0) {
            			builder.append("/*" + newLine());
            			builder.append(" * " + t.getComment() + newLine());
            			builder.append(" */" + newLine());
            		}
            		builder.append("public class " + className + " {" + newLine(2));
            		t.getColumns().forEach(c -> {
            			if (c.getComment() != null && c.getComment().length() > 0) {
            				builder.append(space(4) + "/*" + newLine());
            				builder.append(space(4) + " * " + c.getComment() + newLine());
            				builder.append(space(4) + " */" + newLine());
            			}
            			builder.append(space(4) + "private " + c.getDataType() + " " + c.getName() + ";" + newLine(2));
            		});
            		t.getColumns().forEach(c -> {
            			if (c.getComment() != null && c.getComment().length() > 0) {
            				builder.append(space(4) + "/*" + newLine());
            				builder.append(space(4) + " * 获取" + c.getComment() + newLine());
            				builder.append(space(4) + " * @return " + c.getComment() + newLine());
            				builder.append(space(4) + " */" + newLine());
            			}
            			builder.append(space(4) + "public " + c.getDataType() + " get" + c.getName().substring(0, 1).toUpperCase() + c.getName().substring(1) + "() {" + newLine());
            			builder.append(space(8) + "return " + c.getName() + ";" + newLine());
            			builder.append(space(4) + "}" + newLine(2));
            			if (c.getComment() != null && c.getComment().length() > 0) {
            				builder.append(space(4) + "/*" + newLine());
            				builder.append(space(4) + " * 设置" + c.getComment() + newLine());
            				builder.append(space(4) + " * @param " + c.getName() + " " + c.getComment() + newLine());
            				builder.append(space(4) + " */" + newLine());
            			}
            			builder.append(space(4) + "public void set" + c.getName().substring(0, 1).toUpperCase() + c.getName().substring(1) + "(" + c.getDataType() + " " + c.getName() + ") {" + newLine());
            			builder.append(space(8) + "this." + c.getName() + " = " + c.getName() + ";" + newLine());
            			builder.append(space(4) + "}" + newLine(2));
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
                builder.append("package " + genPackage + ";" + newLine(2));
                builder.append("import com.github.mybatisq.Column;" + newLine());
                builder.append("import com.github.mybatisq.Join;" + newLine());
                builder.append("import com.github.mybatisq.Query;" + newLine());
                builder.append("import com.github.mybatisq.Table;" + newLine());
                String className = t.getName() + "Table";
                String tableName = t.getName().substring(0, 1).toLowerCase() + t.getName().substring(1);
                String tableAlias = alias(tableName);
                builder.append(newLine() + "public class " + className + " extends Table {" + newLine());
                builder.append(newLine() + space(4) + className + "() {" + newLine());
                builder.append(space(8) + "super(\"" + tableName + "\", \"" + tableAlias + "\");" + newLine());
                builder.append(space(4) + "}" + newLine());
                builder.append(newLine() + space(4) + "public static final " + className + " " + tableName + " = new " + className + "();" + newLine());
                builder.append(newLine() + space(4) + "public Query<" + className + "> query() {" + newLine());
                builder.append(space(8) + "return new Query<>(" + tableName + ");" + newLine());
                builder.append(space(4) + "}" + newLine());
                builder.append(newLine() + space(4) + "public <T extends Table> Join<" + className + ", T> inner(T " + tableAlias + ") {" + newLine());
                builder.append(space(8) + "return new Join<>(\"inner\", this, " + tableAlias + ");" + newLine());
                builder.append(space(4) + "}" + newLine());
                t.getColumns().forEach(c -> {
                	if (c.getComment() != null && c.getComment().trim().length() > 0) {
                		builder.append(newLine() + space(4) + "/*" + newLine());
                		builder.append(space(4) + " * " + c.getComment() + newLine());
                		builder.append(space(4) + " */");
                	}
                	builder.append(newLine() + space(4) + "public Column<" + className + ", " + c.getDataType() + "> " + c.getName() + " = new Column<>(\"" + c.getName() + "\");" + newLine());
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
                builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + newLine());
                builder.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >" + newLine());
                String className = t.getName();
                String tableName = t.getName().substring(0, 1).toLowerCase() + t.getName().substring(1);
                builder.append("<mapper namespace=\"" + genPackage + "." + className + "Mapper\">");
                
                builder.append(newLine() + space(4) + "<select id=\"count\" parameterType=\"com.github.mybatisq.Query\" resultType=\"java.lang.Integer\">" + newLine());
                builder.append(space(8) + "select count(0) <include refid=\"" + genPackage + ".QMapper.countFrom\"/>" + newLine());
                builder.append(space(4) + "</select>" + newLine());
                builder.append(newLine() + space(4) + "<select id=\"select\" parameterType=\"com.github.mybatisq.Query\" resultType=\"" + entityPackage + "." + className + "\">" + newLine());
                builder.append(space(8) + "select ");
                builder.append(t.getColumns().stream().map(c -> "${tableAlias}." + c.getName()).reduce((a, b) -> a + "," + b).get());
                builder.append(" <include refid=\"" + genPackage + ".QMapper.selectFrom\"/>" + newLine());
                builder.append(space(4) + "</select>" + newLine());
                
                builder.append(newLine() + space(4) + "<insert id=\"insert\" parameterType=\"" + entityPackage + "." + className + "\" useGeneratedKeys=\"true\"");
                t.getColumns().stream().filter(c -> c.getIsAutoIncrement() && c.getIsPrimaryKey()).findFirst().ifPresent(c -> {
                	builder.append(" keyProperty=\"" + c.getName() + "\"");
                });
                builder.append(">" + newLine());
                builder.append(space(8) + "<trim prefix=\"insert " + tableName + " (\" suffix=\")\" suffixOverrides=\",\">\r\n");
                t.getColumns().forEach(c -> {
                	builder.append(space(12) + "<if test=\"" + c.getName() + " != null\">" + c.getName() + ",</if>" + newLine());
                });
                builder.append(space(8) + "</trim>" + newLine());
                builder.append(space(8) + "<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">" + newLine());
                t.getColumns().forEach(c -> {
                	builder.append(space(12) + "<if test=\"" + c.getName() + " != null\">#{" + c.getName() + "},</if>" + newLine());
                });
                builder.append(space(8) + "</trim>" + newLine());
                builder.append(space(4) + "</insert>" + newLine());
                
                Optional<Column> keyColumn = t.getColumns().stream().filter(c -> c.getIsPrimaryKey()).findFirst();
                if (!keyColumn.isPresent()) {
                	throw new RuntimeException("Table " + tableName + " must has a primary key column.");
                }
                builder.append(newLine() + space(4) + "<update id=\"update\" parameterType=\"" + entityPackage + "." + className + "\">" + newLine());
                builder.append(space(8) + "<trim prefix=\"update " + tableName + " set\" suffix=\"where " + keyColumn.get().getName() + "=#{" + keyColumn.get().getName() + "}\" suffixOverrides=\",\">" + newLine());
                t.getColumns().stream().filter(c -> !c.getIsPrimaryKey()).forEach(c -> {
                	builder.append(space(12) + "<if test=\"" + c.getName() + " != null\">" + c.getName() + "=#{" + c.getName() + "},</if>" + newLine());
                });
                builder.append(space(8) + "</trim>" + newLine());
                builder.append(space(4) + "</update>" + newLine());
                
                builder.append(newLine() + space(4) + "<delete id=\"delete\">" + newLine());
                builder.append(space(8) + "delete from " + tableName + " where " + keyColumn.get().getName() + "=#{" + keyColumn.get().getName() + "}" + newLine());
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
            
            tables.forEach(t -> {
                final StringBuilder builder = new StringBuilder();
                
                String className = t.getName();
                String tableName = t.getName().substring(0, 1).toLowerCase() + t.getName().substring(1);
                
                Optional<Column> keyColumn = t.getColumns().stream().filter(c -> c.getIsPrimaryKey()).findFirst();
                if (!keyColumn.isPresent()) {
                	throw new RuntimeException("Table " + tableName + " must has a primary key column.");
                }
                
                builder.append("package " + genPackage + ";" + newLine(2));
                builder.append("import java.util.List;" + newLine(2));
                builder.append("import org.apache.ibatis.annotations.Mapper;" + newLine());
                builder.append("import org.apache.ibatis.annotations.Param;" + newLine(2));
                builder.append("import com.github.mybatisq.Query;" + newLine());
                builder.append("import " + entityPackage + "." + className + ";" + newLine(2));
                builder.append("@Mapper" + newLine());
                builder.append("public interface " + className + "Mapper {" + newLine(2));
                builder.append(space(4) + "int count(Query<" + className + "Table> query);" + newLine(2));
                builder.append(space(4) + "List<" + className + "> select(Query<" + className + "Table> query);" + newLine(2));
                builder.append(space(4) + "int insert(" + className + " " + tableName + ");" + newLine(2));
                builder.append(space(4) + "int update(" + className + " " + tableName + ");" + newLine(2));
                builder.append(space(4) + "int delete(@Param(\"" + keyColumn.get().getName() + "\") " + keyColumn.get().getDataType() + " " + keyColumn.get().getName() + ");" + newLine(2));
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
