package com.github.mybatisq.mapper;

import com.github.mybatisq.Column;
import com.github.mybatisq.Join;
import com.github.mybatisq.DeleteQuery;
import com.github.mybatisq.Query;
import com.github.mybatisq.Insert;
import com.github.mybatisq.Update;
import com.github.mybatisq.Table;

/**
 * @author richterplus
 */
public class EmpPostTable extends Table {

    private EmpPostTable() {
        super("emp_post", "e1");
    }

    public static final EmpPostTable emp_post = new EmpPostTable();

    public Query<EmpPostTable> query() {
        return new Query<>(emp_post);
    }

    public Insert<EmpPostTable> insert() {
        return new Insert<>(emp_post);
    }

    public Update<EmpPostTable> update() {
        return new Update<>(emp_post);
    }

    public DeleteQuery<EmpPostTable> deleteQuery() {
        return new DeleteQuery<>(emp_post);
    }

    public <T extends Table> Join<EmpPostTable, T> inner(T table) {
        return new Join<>("inner", this, table);
    }

    public Column<EmpPostTable, Integer> ep_id = new Column<>("ep_id");

    public Column<EmpPostTable, Integer> emp_id = new Column<>("emp_id");

    public Column<EmpPostTable, Integer> post_id = new Column<>("post_id");
}