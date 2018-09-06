package com.github.mybatisq;

/**
 * 列
 * @author richterplus
 */
public class MappedColumn {
    
    /**
     * 原始列名
     */
    private String originalName;
    
    /**
     * 映射的名称
     */
    private String mappedName;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 注释
     */
    private String comment;

    /**
     * 是否主键
     */
    private boolean isPrimaryKey;

    /**
     * 是否自增长
     */
    private boolean isAutoIncrement;

    /**
     * 获取原始列名
     * @return 原始列名
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * 设置原始列名
     * @param originalName 原始列名
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
     * 获取数据类型
     * @return 数据类型
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * 设置数据类型
     * @param dataType 数据类型
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
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
     * 获取是否主键
     * @return 是否主键
     */
    public boolean getIsPrimaryKey() {
        return isPrimaryKey;
    }

    /**
     * 设置是否主键
     * @param isPrimaryKey 是否主键
     */
    public void setIsPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    /**
     * 获取是否自增长
     * @return 是否自增长
     */
    public boolean getIsAutoIncrement() {
        return isAutoIncrement;
    }

    /**
     * 设置是否自增长
     * @param isAutoIncrement 是否自增长
     */
    public void setIsAutoIncrement(boolean isAutoIncrement) {
        this.isAutoIncrement = isAutoIncrement;
    }
}
