package com.github.mybatisq;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * 查询
 * @param <T> 表类型
 */
public class Query<T extends Table> {

    /**
     * 新建查询
     * @param table 表
     */
    public Query(T table) {
        this.tableName = table.getName();
        this.tableAlias = table.getAlias();
        this.wheres = new LinkedList<>();
        this.orderBys = new LinkedList<>();
        this.joins = new LinkedList<>();
        this.selectedColumns = new LinkedList<>();
    }

    /**
     * where
     * @param where where条件
     * @return 查询
     */
    public Query<T> where(Where where) {
        if (where != null)
            wheres.add(where);
        return this;
    }

    /**
     * 排序
     * @param orderBy 排序
     * @return 查询
     */
    public Query<T> orderBy(OrderBy orderBy) {
        if (orderBy != null)
            orderBys.add(orderBy);
        return this;
    }

    /**
     * 限制查询返回的数据行（limit ..., ?）
     * @param limit 限制的数据行数
     * @return 查询
     */
    public Query<T> limit(int limit) {
        this.limit = limit;
        if (this.skip == null) {
            this.skip = 0;
        }
        return this;
    }

    /**
     * 跳过查询返回的行数（limit ?, ...）
     * @param skip 跳过的数据行数
     * @return 查询
     */
    public Query<T> skip(int skip) {
        this.skip = skip;
        if (this.limit == null) {
            this.limit = Integer.MAX_VALUE;
        }
        return this;
    }

    /**
     * 连接
     * @param innerJoin 连接
     * @return 查询
     */
    public Query<T> join(Join<? extends Table, ? extends Table> innerJoin) {
        if (innerJoin != null)
            joins.add(innerJoin);
        return this;
    }

    /**
     * 选择列
     * @param columns 列集合
     * @return 查询
     */
    @SafeVarargs
    public final Query<T> columns(Column<T, ?>... columns) {
        selectedColumns.clear();
        if (columns != null) {
            selectedColumns.addAll(Arrays.asList(columns));
        }
        return this;
    }

    /**
     * 表名称
     */
    private final String tableName;

    /**
     * 表别名
     */
    private final String tableAlias;

    /**
     * where条件
     */
    private final Collection<Where> wheres;

    /**
     * 排序
     */
    private final Collection<OrderBy> orderBys;

    /**
     * 限制的数据行数
     */
    private Integer limit;

    /**
     * 跳过的数据行数
     */
    private Integer skip;

    /**
     * 连接
     */
    private final Collection<Join<? extends Table, ? extends Table>> joins;

    /**
     * 选择列集合
     */
    private final Collection<Column<T, ?>> selectedColumns;

    /**
     * 获取表名称
     * @return 表名称
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 获取表别名
     * @return 表别名
     */
    public String getTableAlias() {
        return tableAlias;
    }

    /**
     * 获取where条件
     * @return where条件
     */
    public Collection<Where> getWheres() {
        return wheres;
    }

    /**
     * 获取排序
     * @return 排序
     */
    public Collection<OrderBy> getOrderBys() {
        return orderBys;
    }

    /**
     * 获取连接
     * @return 连接
     */
    public Collection<Join<? extends Table, ? extends Table>> getJoins() {
        return joins;
    }

    /**
     * 获取选择列集合
     * @return 选择列集合
     */
    public Collection<Column<T, ?>> getSelectedColumns() {
        return selectedColumns;
    }
}
