package com.github.mybatisq;

/**
 * 值设置
 * @author richterplus
 */
public class Setter {

    /**
     * 新建值设置
     * @param columnName 列名
     * @param operator 操作符
     * @param value 设置值
     */
    public Setter(String columnName, String operator, Object value) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }

    /**
     * 列名
     */
    private final String columnName;

    /**
     * 操作符
     */
    private final String operator;

    /**
     * 设置值
     */
    private final Object value;

    /**
     * 获取列名
     * @return 列名
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * 获取操作符
     * @return 操作符
     */
    public String getOperator() {
        return operator;
    }

    /**
     * 获取设置值
     * @return 设置值
     */
    public Object getValue() {
        return value;
    }
}
