package com.github.mybatisq;

/**
 * Mysql数据类型映射器
 * @author richterplus
 */
public class MysqlDbTypeMapper implements DbTypeMapper {

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

    @Override
    public String mapDbTypeToJavaType(String dbType) {
        String javaType = String.class.getName();
        if (dbType.contains(DB_TYPE_BIGINT)) {
            javaType = "Long";
        } else if (dbType.contains(DB_TYPE_INT)) {
            javaType = "Integer";
        } else if (dbType.startsWith(DB_TYPE_BIT)) {
            javaType = "Boolean";
        } else if (dbType.startsWith(DB_TYPE_FLOAT)) {
            javaType = "Float";
        } else if (dbType.startsWith(DB_TYPE_DOUBLE)) {
            javaType = "Double";
        } else if (dbType.startsWith(DB_TYPE_DECIMAL)) {
            javaType = "java.math.BigDecimal";
        } else if (dbType.contains(DB_TYPE_DATE)) {
            javaType = "java.util.Date";
        } else if (dbType.contains(DB_TYPE_TIME)) {
            javaType = "java.util.Date";
        } else if (dbType.contains(DB_TYPE_BINARY)) {
            javaType = "byte[]";
        } else if (dbType.contains(DB_TYPE_BLOB)) {
            javaType = "byte[]";
        }
        return javaType;
    }
}
