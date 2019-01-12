package com.github.mybatisq.mapper.core;

import java.util.Collection;

/**
 * 删除查询
 * @author chenjie
 * @param <T> 表类型
 */
public class DeleteQuery<T extends Table> {

    /**
     * 查询
     */
    private Query<T> query;

    /**
     * 新建查询
     * @param table 表
     */
    public DeleteQuery(T table) {
        query = new Query<>(table);
    }

    /**
     * where
     * @param where where条件
     * @return 查询
     */
    public DeleteQuery<T> where(Where where) {
        query.where(where);
        return this;
    }

    /**
     * 获取where条件
     * @return where条件
     */
    public Collection<Where> getWheres() {
        return query.getWheres();
    }
}
