package com.github.mybatisq;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Postgresql数据类型映射器
 * @author chenjie
 */
public class PostgresqlDatabaseProcessor implements DatabaseProcessor {

    private static final String DB_TYPE_BIGINT = "bigint";

    private static final String DB_TYPE_BIGSERIAL = "bigserial";

    private static final String DB_TYPE_INT = "int";

    private static final String DB_TYPE_SERIAL = "serial";

    private static final String DB_TYPE_DOUBLE = "double";

    private static final String DB_TYPE_REAL = "real";

    private static final String DB_TYPE_DECIMAL = "decimal";

    private static final String DB_TYPE_NUMERIC = "numeric";

    private static final String DB_TYPE_MONEY = "money";

    private static final String DB_TYPE_BYTEA = "bytea";

    private static final String DB_TYPE_DATE = "date";

    private static final String DB_TYPE_TIME = "time";

    private static final String DB_TYPE_BOOLEAN = "boolean";

    private static final String COLUMN_DEFAULT_SEQ = "_seq";

    private Datasource datasource;

    PostgresqlDatabaseProcessor(Datasource datasource) {
        this.datasource = datasource;
    }

    @Override
    public String mapDbTypeToJavaType(String dbType) {
        if (DB_TYPE_BIGINT.equals(dbType) || DB_TYPE_BIGSERIAL.equals(dbType)) {
            return Long.class.getSimpleName();
        } else if (DB_TYPE_SERIAL.equals(dbType) || dbType.contains(DB_TYPE_INT)) {
            return Integer.class.getSimpleName();
        } else if (dbType.contains(DB_TYPE_DOUBLE)) {
            return Double.class.getSimpleName();
        } else if (DB_TYPE_REAL.equals(dbType)) {
            return Float.class.getSimpleName();
        } else if (DB_TYPE_DECIMAL.equals(dbType) || DB_TYPE_NUMERIC.equals(dbType) || DB_TYPE_MONEY.equals(dbType)) {
            return BigDecimal.class.getName();
        } else if (DB_TYPE_BYTEA.equals(dbType)) {
            return byte.class.getSimpleName() + "[]";
        } else if (dbType.contains(DB_TYPE_DATE) || dbType.contains(DB_TYPE_TIME)) {
            return Date.class.getName();
        } else if (DB_TYPE_BOOLEAN.equals(dbType)) {
            return Boolean.class.getSimpleName();
        } else {
            return String.class.getSimpleName();
        }
    }

    @Override
    public List<MappedTable> getMappedTables(Collection<String> includeEntityNames, Collection<String> excludeEntityNames) { ;
        Connection conn;
        Statement statement;
        ResultSet resultSet;
        Map<String, MappedTable> originalTableNameToTable = new HashMap<>(16);

        try {
            Class.forName(datasource.getDriverClassName());
            conn = DriverManager.getConnection(datasource.getUrl(), datasource.getUsername(), datasource.getPassword());
            statement = conn.createStatement();

            Map<String, Set<String>> originalTableNameToPrimaryKeys = new HashMap<>(16);

            resultSet = statement.executeQuery("select table_name from information_schema.tables where table_catalog=current_catalog and table_schema=current_schema() and table_type='BASE TABLE'");
            while (resultSet.next()) {
                MappedTable table = new MappedTable();
                table.setOriginalName(resultSet.getString(1));
                table.setMappedName(NamingUtils.generateMappedName(table.getOriginalName()));
                table.setComment("");
                table.setMappedColumns(new ArrayList<>());
                originalTableNameToTable.put(table.getOriginalName(), table);
            }
            resultSet.close();

            resultSet = statement.executeQuery("select table_name,column_name from information_schema.key_column_usage where table_catalog=current_catalog and table_schema=current_schema() and constraint_catalog=current_catalog and constraint_schema=current_schema()");
            while (resultSet.next()) {
                originalTableNameToPrimaryKeys.computeIfAbsent(resultSet.getString("table_name"), k -> new HashSet<>()).add(resultSet.getString("column_name"));
            }
            resultSet.close();

            for (String originalTableName : originalTableNameToTable.keySet()) {
                resultSet = statement.executeQuery(String.format("select obj_description('%s'::regclass, 'pg_class')", originalTableName));
                if (resultSet.next()) {
                    originalTableNameToTable.get(originalTableName).setComment(resultSet.getString(1));
                }
                resultSet.close();
            }

            resultSet = statement.executeQuery(String.format(
                    "select table_name,column_name,data_type,column_default from information_schema.columns where table_catalog=current_catalog and table_schema=current_schema() and table_name in (%s) order by table_name,ordinal_position asc",
                    originalTableNameToTable.keySet().stream().map(k -> "'" + k + "'").collect(Collectors.joining(","))));
            while (resultSet.next()) {
                MappedTable table = originalTableNameToTable.get(resultSet.getString("table_name"));
                MappedColumn column = new MappedColumn();
                column.setOriginalName(resultSet.getString("column_name"));
                column.setMappedName(NamingUtils.generateMappedName(column.getOriginalName()));
                column.setDataType(mapDbTypeToJavaType(resultSet.getString("data_type")));
                column.setIsAutoIncrement(StringUtils.defaultIfEmpty(resultSet.getString("column_default"), "").contains(COLUMN_DEFAULT_SEQ));
                column.setIsPrimaryKey(originalTableNameToPrimaryKeys.get(table.getOriginalName()).contains(column.getOriginalName()));
                column.setComment("");
                table.getMappedColumns().add(column);
            }
            resultSet.close();

            for (String originalTableName : originalTableNameToTable.keySet()) {
                List<MappedColumn> columns = originalTableNameToTable.get(originalTableName).getMappedColumns();
                for (int i = 0; i < columns.size(); i++) {
                    resultSet = statement.executeQuery(String.format("select col_description('%s'::regclass, %d)", originalTableName, (i + 1)));
                    if (resultSet.next()) {
                        columns.get(i).setComment(resultSet.getString(1));
                    }
                    resultSet.close();
                }
            }

            statement.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

        return new ArrayList<>(originalTableNameToTable.values());
    }
}
