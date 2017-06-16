package com.github.mybatisq;

/**
 * 列
 */
public class Column {

	/**
	 * 名称
	 */
	private String name;
	
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
