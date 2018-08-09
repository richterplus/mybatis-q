package com.github.mybatisq;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 删除查询
 * @param <T> 表类型
 */
public class DeleteQuery<T extends Table> {

    /**
     * 新建查询
     * @param table 表
     */
    public DeleteQuery(T table) {
        this.tableName = table.getName();
        this.tableAlias = table.getAlias();
        this.wheres = new LinkedList<>();
        this.orderBys = new LinkedList<>();
    }

    /**
     * where
     * @param where where条件
     * @return 查询
     */
    public DeleteQuery<T> where(Where where) {
        if (where != null)
            wheres.add(where);
        return this;
    }

    /**
     * 排序
     * @param orderBy 排序
     * @return 查询
     */
    public DeleteQuery<T> orderBy(OrderBy orderBy) {
        if (orderBy != null)
            orderBys.add(orderBy);
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
}
