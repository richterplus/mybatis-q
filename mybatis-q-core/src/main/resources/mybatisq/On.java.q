package com.github.mybatisq;

/**
 * 连接对象
 * @author chenjie
 * @param <T1> 表1
 * @param <T2> 表2
 */
public class On<T1 extends Table, T2 extends Table> {

    /**
     * 新建连接对象
     * @param column1 列1
     * @param operator 操作符
     * @param column2 列2
     */
    public On(Column<T1, ?> column1, String operator, Column<T2, ?> column2) {
        this.columnName1 = column1.getName();
        this.operator = operator;
        this.columnName2 = column2.getName();
    }

    /**
     * 列名1
     */
    private final String columnName1;

    /**
     * 操作符
     */
    private final String operator;

    /**
     * 列名2
     */
    private final String columnName2;

    /**
     * 获取列名1
     * @return 列名1
     */
    public String getColumnName1() {
        return columnName1;
    }

    /**
     * 获取操作符
     * @return 操作符
     */
    public String getOperator() {
        return operator;
    }

    /**
     * 获取列名2
     * @return 列名2
     */
    public String getColumnName2() {
        return columnName2;
    }
}
