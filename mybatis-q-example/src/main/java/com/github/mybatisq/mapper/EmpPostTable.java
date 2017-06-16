package com.github.mybatisq.mapper;

import com.github.mybatisq.Column;
import com.github.mybatisq.Join;
import com.github.mybatisq.Query;
import com.github.mybatisq.Table;

public class EmpPostTable extends Table {

    EmpPostTable() {
        super("empPost", "e0");
    }

    public static final EmpPostTable empPost = new EmpPostTable();

    public Query<EmpPostTable> query() {
        return new Query<>(empPost);
    }

    public <T extends Table> Join<EmpPostTable, T> inner(T table) {
        return new Join<>("inner", this, table);
    }

    /**
     * id
     */
    public Column<EmpPostTable, Integer> epid = new Column<>("epid");

    /**
     * 员工id
     */
    public Column<EmpPostTable, Integer> empId = new Column<>("empId");

    /**
     * 职位id
     */
    public Column<EmpPostTable, Integer> postId = new Column<>("postId");
}