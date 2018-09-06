package com.github.mybatisq;

import java.util.List;

/**
 * 表
 * @author richterplus
 */
public class MappedTable {
    
    /**
     * 原始表名
     */
    private String originalName;
    
    /**
     * 映射的名称
     */
    private String mappedName;

    /**
     * 注释
     */
    private String comment;

    /**
     * 列
     */
    private List<MappedColumn> columns;

    /**
     * 获取原始表名
     * @return 原始表名
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * 设置原始表名
     * @param originalName 原始表名
     */
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    /**
     * 获取映射的名称
     * @return 映射的名称
     */
    public String getMappedName() {
        return mappedName;
    }

    /**
     * 设置映射的名称
     * @param mappedName 映射的名称
     */
    public void setMappedName(String mappedName) {
        this.mappedName = mappedName;
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
    public List<MappedColumn> getMappedColumns() {
        return columns;
    }

    /**
     * 设置列
     * @param columns 列
     */
    public void setMappedColumns(List<MappedColumn> columns) {
        this.columns = columns;
    }
}
