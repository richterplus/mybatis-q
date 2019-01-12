package com.github.mybatisq;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

/**
 * 更新
 * @author chenjie
 * @param <T> 表类型
 */
public class Update<T extends Table> {

    /**
     * 查询
     */
    private Query<T> query;

    /**
     * 新建更新
     * @param table 表
     */
    public Update(T table) {
        query = new Query<>(table);
        setters = new LinkedList<>();
    }

    /**
     * 设置列的值，相当于set column = data
     * @param column 列
     * @param data 列的值
     * @param <D> 值类型
     * @return 更新
     */
    public <D> Update<T> set(Column<T, D> column, D data) {
        setters.add(new Setter(column.getName(), "eq", data));
        return this;
    }

    /**
     * 设置数值列的值，相当于set column = NumberOps(ops)
     * @param column 列
     * @param ops 数值操作
     * @param <D> 值类型
     * @return 更新
     */
    public <D extends Number> Update<T> set(Column<T, D> column, NumberOps<D> ops) {
        setters.add(new Setter(column.getName(), ops.method, ops.args[0]));
        return this;
    }

    /**
     * 设置日期列的值，相当于set column = DateOps(ops)
     * @param column 列
     * @param ops 日期操作
     * @return 更新
     */
    public Update<T> set(Column<T, Date> column, DateOps ops) {
        return this;
    }

    /**
     * 查询条件
     * @param where 查询条件
     * @return 更新
     */
    public Update<T> where(Where where) {
        query.where(where);
        return this;
    }

    /**
     * 值设置
     */
    private Collection<Setter> setters;

    /**
     * 获取where条件
     * @return where条件
     */
    public Collection<Where> getWheres() {
        return query.getWheres();
    }

    /**
     * 获取值设置
     * @return 值设置
     */
    public Collection<Setter> getSetters() {
        return setters;
    }
}
