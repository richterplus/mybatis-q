package com.github.mybatisq.mapper.core;

import java.util.Arrays;
import java.util.Collection;

/**
 * 插入
 * @author chenjie
 * @param <T> 待插入表类型
 */
public class Insert<T extends Table> {

    /**
     * 待插入表
     */
    private T table;

    /**
     * 查询
     */
    private Query<?> query;

    /**
     * 插入的字段集合
     */
    private Collection<Column<T, ?>> insertColumns;

    /**
     * 新建更新
     * @param table 待插入表
     */
    public Insert(T table) {
        this.table = table;
    }

    /**
     * 设置插入的列
     * @param columns 插入的列
     * @return 插入
     */
    @SafeVarargs
    public final Insert<T> columns(Column<T, ?>... columns) {
        this.insertColumns = Arrays.asList(columns);
        return this;
    }

    /**
     * 设置查询
     * @param query 查询
     * @return 插入
     */
    public Insert<T> select(Query query) {
        this.query = query;
        return this;
    }

    /**
     * @return 插入的字段集合
     */
    public Collection<Column<T, ?>> getInsertColumns() {
        return insertColumns;
    }

    /**
     * @return 查询
     */
    public Query getQuery() {
        return query;
    }

    /**
     * @return 待插入表
     */
    public T getTable() {
        return table;
    }

    /**
     * 获取表名称
     * @return 表名称
     */
    public String getTableName() {
        return query.getTableName();
    }

    /**
     * 获取表别名
     * @return 表别名
     */
    public String getTableAlias() {
        return query.getTableAlias();
    }

    /**
     * 获取where条件
     * @return where条件
     */
    public Collection<Where> getWheres() {
        return query.getWheres();
    }

    /**
     * 获取排序
     * @return 排序
     */
    public Collection<OrderBy> getOrderBys() {
        return query.getOrderBys();
    }

    /**
     * 获取连接
     * @return 连接
     */
    public Collection<Join> getJoins() {
        return query.getJoins();
    }

    /**
     * 获取选择列集合
     * @return 选择列集合
     */
    public Collection<? extends Column<?, ?>> getSelectedColumns() {
        return query.getSelectedColumns();
    }

    /**
     * @return 跳过的数据行数
     */
    public Integer getSkip() {
        return query.getSkip();
    }

    /**
     * @return 限制数据的行数
     */
    public Integer getLimit() {
        return query.getLimit();
    }
}
