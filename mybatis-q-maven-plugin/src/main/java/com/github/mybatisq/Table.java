package com.github.mybatisq;

import java.util.List;

/**
 * 表
 */
public class Table {

    /**
     * 名称
     */
    private String name;

    /**
     * 注释
     */
    private String comment;

    /**
     * 列
     */
    private List<Column> columns;

    /**
     * 获取名称
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取注释
     * @return 注释
     */
    public String getComment() {
        return comment;
    }

    /**
     * 设置注释
     * @param comment 注释
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * 获取列
     * @return 列
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * 设置列
     * @param columns 列
     */
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}
