package com.github.mybatisq.mapper;

import com.github.mybatisq.Column;
import com.github.mybatisq.Join;
import com.github.mybatisq.Query;
import com.github.mybatisq.Table;

public class EmpPostTable extends Table {

    private EmpPostTable() {
        super("emp_post", "e0");
    }

    public static final EmpPostTable emp_post = new EmpPostTable();

    public Query<EmpPostTable> query() {
        return new Query<>(emp_post);
    }

    public <T extends Table> Join<EmpPostTable, T> inner(T table) {
        return new Join<>("inner", this, table);
    }

    /**
     * id
     */
    public Column<EmpPostTable, Integer> ep_id = new Column<>("ep_id");

    /**
     * 员工id
     */
    public Column<EmpPostTable, Integer> emp_id = new Column<>("emp_id");

    /**
     * 职位id
     */
    public Column<EmpPostTable, Integer> post_id = new Column<>("post_id");
}