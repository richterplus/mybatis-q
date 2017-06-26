package com.github.mybatisq;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 连接
 * @param <T1> 表1
 * @param <T2> 表2
 */
public class Join<T1 extends Table, T2 extends Table> {

    /**
     * 新建连接
     * @param type 连接类型
     * @param table1 表1
     * @param table2 表2
     */
    public Join(String type, T1 table1, T2 table2) {
        this.type = type;
        this.tableAlias1 = table1.getAlias();
        this.tableName2 = table2.getName();
        this.tableAlias2 = table2.getAlias();
        this.ons = new LinkedList<>();
        this.wheres = new LinkedList<>();
    }

    /**
     * 连接两列
     * @param on 连接对象
     * @return 连接
     */
    public Join<T1, T2> on(On<T1, T2> on) {
        if (on != null)
            ons.add(on);
        return this;
    }

    /**
     * 继续连接两列
     * @param on 连接对象
     * @return 连接
     */
    public Join<T1, T2> and(On<T1, T2> on) {
        if (on != null)
            ons.add(on);
        return this;
    }

    /**
     * 继续添加where
     * @param where where条件
     * @return 连接
     */
    public Join<T1, T2> and(Where<T2> where) {
        if (where != null)
            wheres.add(where);
        return this;
    }

    /**
     * 连接类型
     */
    private final String type;

    /**
     * 表别名1
     */
    private final String tableAlias1;

    /**
     * 表名2
     */
    private final String tableName2;

    /**
     * 表别名2
     */
    private final String tableAlias2;

    /**
     * 连接对象集合
     */
    private final Collection<On<T1, T2>> ons;

    /**
     * where集合
     */
    private final Collection<Where<T2>> wheres;

    /**
     * 获取连接类型
     *
     * @return 连接类型
     */
    public String getType() {
        return type;
    }

    /**
     * 获取表别名1
     * @return 表别名1
     */
    public String getTableAlias1() {
        return tableAlias1;
    }

    /**
     * 获取表名2
     * @return 表名2
     */
    public String getTableName2() {
        return tableName2;
    }

    /**
     * 获取表别名2
     * @return 表别名2
     */
    public String getTableAlias2() {
        return tableAlias2;
    }

    /**
     * 获取连接对象集合
     * @return 连接对象集合
     */
    public Collection<On<T1, T2>> getOns() {
        return ons;
    }

    /**
     * 获取where集合
     * @return where集合
     */
    public Collection<Where<T2>> getWheres() {
        return wheres;
    }
}
