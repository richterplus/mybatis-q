package com.github.mybatisq;

import org.apache.maven.plugin.MojoFailureException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mysql数据类型映射器
 * @author chenjie
 */
public class MysqlDatabaseProcessor implements DatabaseProcessor {

    private static final String DB_TYPE_BIGINT = "bigint";

    private static final String DB_TYPE_INT = "int";

    private static final String DB_TYPE_BIT = "bit";

    private static final String DB_TYPE_FLOAT = "float";

    private static final String DB_TYPE_DOUBLE = "double";

    private static final String DB_TYPE_DECIMAL = "decimal";

    private static final String DB_TYPE_DATE = "date";

    private static final String DB_TYPE_TIME = "time";

    private static final String DB_TYPE_BINARY = "binary";

    private static final String DB_TYPE_BLOB = "blob";

    private Datasource datasource;

    MysqlDatabaseProcessor(Datasource datasource) {
        this.datasource = datasource;
    }

    @Override
    public String mapDbTypeToJavaType(String dbType) {
        String javaType = String.class.getName();
        if (dbType.contains(DB_TYPE_BIGINT)) {
            javaType = Long.class.getSimpleName();
        } else if (dbType.contains(DB_TYPE_INT)) {
            javaType = Integer.class.getSimpleName();
        } else if (dbType.startsWith(DB_TYPE_BIT)) {
            javaType = Boolean.class.getSimpleName();
        } else if (dbType.startsWith(DB_TYPE_FLOAT)) {
            javaType = Float.class.getSimpleName();
        } else if (dbType.startsWith(DB_TYPE_DOUBLE)) {
            javaType = Double.class.getSimpleName();
        } else if (dbType.startsWith(DB_TYPE_DECIMAL)) {
            javaType = BigDecimal.class.getName();
        } else if (dbType.contains(DB_TYPE_DATE) || dbType.contains(DB_TYPE_TIME)) {
            javaType = Date.class.getName();
        } else if (dbType.contains(DB_TYPE_BINARY) || dbType.contains(DB_TYPE_BLOB)) {
            javaType = byte.class.getSimpleName() + "[]";
        }
        return javaType;
    }

    @Override
    public List<MappedTable> getMappedTables(Collection<String> includeEntityNames, Collection<String> excludeEntityNames) {
        List<MappedTable> tables;
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
                MappedTable table = new MappedTable();
                table.setOriginalName(resultSet.getString("Name"));
                table.setMappedName(NamingUtils.generateMappedName(table.getOriginalName()));
                table.setComment(resultSet.getString("Comment"));
                table.setMappedColumns(new ArrayList<>());
                tables.add(table);
            }
            resultSet.close();
            statement.close();

            if (includeEntityNames != null && includeEntityNames.size() > 0) {
                tables = tables.stream().filter(t -> includeEntityNames.contains(t.getMappedName())).collect(Collectors.toList());
            }

            if (excludeEntityNames != null && excludeEntityNames.size() > 0) {
                tables = tables.stream().filter(t -> !excludeEntityNames.contains(t.getMappedName())).collect(Collectors.toList());
            }

            for (MappedTable table : tables) {
                statement = conn.createStatement();
                resultSet = statement.executeQuery("show full columns from `" + table.getOriginalName() + "`");
                while (resultSet.next()) {
                    MappedColumn column = new MappedColumn();
                    column.setComment(resultSet.getString("Comment"));
                    column.setDataType(mapDbTypeToJavaType(resultSet.getString("Type")));
                    column.setIsAutoIncrement(resultSet.getString("Extra").contains("auto_increment"));
                    column.setIsPrimaryKey(resultSet.getString("Key").contains("PRI"));
                    column.setOriginalName(resultSet.getString("Field"));
                    column.setMappedName(NamingUtils.generateMappedName(column.getOriginalName()));
                    table.getMappedColumns().add(column);
                }
                resultSet.close();
                statement.close();
            }

            resultSet.close();
            statement.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return tables;
    }
}
