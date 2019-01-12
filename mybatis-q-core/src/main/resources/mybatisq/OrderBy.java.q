package com.github.mybatisq;

/**
 * 排序项
 * @author chenjie
 */
public class OrderBy {

    /**
     * 新建排序项
     * @param columnName 列名
     * @param direction 排序方向
     */
    public OrderBy(String columnName, String direction) {
        this.columnName = columnName;
        this.direction = direction;
    }

    /**
     * 列名
     */
    private final String columnName;

    /**
     * 排序方向
     */
    private final String direction;

    /**
     * 获取列名
     * @return 列名
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * 获取排序方向
     * @return 排序方向
     */
    public String getDirection() {
        return direction;
    }
}
