package com.github.mybatisq;

/**
 * where项
 *
 * @param <T> 表类型
 */
public class Where<T extends Table> {

    /**
     * 新建where项
     *
     * @param columnName 列名
     * @param operator 操作符
     * @param queryValue 查询值
     */
    public Where(String columnName, String operator, Object queryValue) {
        this(columnName, operator, queryValue, null);
    }

    /**
     * 新建where项
     *
     * @param columnName 列名
     * @param operator 操作符
     * @param queryValue 查询值
     * @param queryValue2 第二个查询值
     */
    public Where(String columnName, String operator, Object queryValue, Object queryValue2) {
        this.columnName = columnName;
        this.operator = operator;
        this.queryValue = queryValue;
        this.queryValue2 = queryValue2;
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
     * 查询值
     */
    private final Object queryValue;

    /**
     * 第二个查询值
     */
    private final Object queryValue2;

    /**
     * 获取列名
     *
     * @return 列名
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * 获取操作符
     *
     * @return 操作符
     */
    public String getOperator() {
        return operator;
    }

    /**
     * 获取查询值
     *
     * @return 查询值
     */
    public Object getQueryValue() {
        return queryValue;
    }

    /**
     * 获取第二个查询值
     *
     * @return 第二个查询值
     */
    public Object getQueryValue2() {
        return queryValue2;
    }
}
