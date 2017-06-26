package com.github.mybatisq;

import java.util.Collection;

/**
 * 列
 * @param <T> 表类型
 * @param <D> 数据类型
 */
public class Column<T extends Table, D> {

    /**
     * 新增列
     * @param name 名称
     */
    public Column(String name) {
        this.name = name;
    }

    /**
     * 升序排列
     * @return 排序项
     */
    public OrderBy<T> asc() {
        return new OrderBy<>(name, "asc");
    }

    /**
     * 降序排列
     * @return 排序项
     */
    public OrderBy<T> desc() {
        return new OrderBy<>(name, "desc");
    }

    /**
     * = value
     * @param value 查询值
     * @return 查询项
     */
    public Where<T> eq(D value) {
        return new Where<>(name, "eq", value);
    }

    /**
     * &gt; value
     * @param value 查询值
     * @return 查询项
     */
    public Where<T> gt(D value) {
        return new Where<>(name, "gt", value);
    }

    /**
     * &gt;= value
     * @param value 查询值
     * @return 查询项
     */
    public Where<T> ge(D value) {
        return new Where<>(name, "ge", value);
    }

    /**
     * &lt; value
     * @param value 查询值
     * @return 查询项
     */
    public Where<T> lt(D value) {
        return new Where<>(name, "lt", value);
    }

    /**
     * &lt;= value
     * @param value 查询值
     * @return 查询项
     */
    public Where<T> le(D value) {
        return new Where<>(name, "le", value);
    }

    /**
     * between start and end
     * @param start 起始值
     * @param end 结束值
     * @return 查询项
     */
    public Where<T> between(D start, D end) {
        return new Where<>(name, "between", start, end);
    }

    /**
     * in (values...)
     * @param values 查询值
     * @return 查询项
     */
    public Where<T> in(Collection<D> values) {
        return new Where<>(name, "in", values);
    }

    /**
     * in (select ...)
     * @param select 子查询
     * @return 查询项
     */
    Where<T> in(Select<? extends Table, D> select) {
        return new Where<>(name, "inSelect", select);
    }

    /**
     * like 'value%'
     * @param value 查询值
     * @return 查询项
     */
    public Where<T> startWith(D value) {
        if (!(value instanceof String)) {
            throw new RuntimeException("LIKE is not supported on non-string values.");
        }
        return new Where<>(name, "startWith", value);
    }

    /**
     * 连接到另一个列
     * @param column 列
     * @param <T2> 连接表
     * @return 连接对象
     */
    public <T2 extends Table> On<T, T2> eq(Column<T2, D> column) {
        return new On<>(this, "eq", column);
    }

    /**
     * 名称
     */
    private final String name;

    /**
     * 获取名称
     * @return 名称
     */
    public String getName() {
        return name;
    }
}
