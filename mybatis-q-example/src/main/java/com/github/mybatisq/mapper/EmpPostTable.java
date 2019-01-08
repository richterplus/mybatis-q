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
        super("EmpPost", "E1");
    }

    public static final EmpPostTable EmpPost = new EmpPostTable();

    public Query<EmpPostTable> query() {
        return new Query<>(EmpPost);
    }

    public Insert<EmpPostTable> insert() {
        return new Insert<>(EmpPost);
    }

    public Update<EmpPostTable> update() {
        return new Update<>(EmpPost);
    }

    public DeleteQuery<EmpPostTable> deleteQuery() {
        return new DeleteQuery<>(EmpPost);
    }

    public <T extends Table> Join<EmpPostTable, T> inner(T table) {
        return new Join<>("inner", this, table);
    }

    public Column<EmpPostTable, Integer> epId = new Column<>("epId");

    public Column<EmpPostTable, Integer> empId = new Column<>("empId");

    public Column<EmpPostTable, Integer> postId = new Column<>("postId");
}