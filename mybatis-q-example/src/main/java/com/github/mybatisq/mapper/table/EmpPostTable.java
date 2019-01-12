package com.github.mybatisq.mapper.table;

import com.github.mybatisq.mapper.core.Column;
import com.github.mybatisq.mapper.core.Join;
import com.github.mybatisq.mapper.core.DeleteQuery;
import com.github.mybatisq.mapper.core.Query;
import com.github.mybatisq.mapper.core.Update;
import com.github.mybatisq.mapper.core.Table;

/**
 * @author chenjie
 */
public class EmpPostTable extends Table {

    private EmpPostTable() {
        super("emp_post", "e1");
    }

    public static final EmpPostTable emp_post = new EmpPostTable();

    public Query<EmpPostTable> query() {
        return new Query<>(emp_post);
    }

    public Update<EmpPostTable> update() {
        return new Update<>(emp_post);
    }

    public DeleteQuery<EmpPostTable> deleteQuery() {
        return new DeleteQuery<>(emp_post);
    }

    public <T extends Table> Join<EmpPostTable, T> join(T table) {
        return new Join<>("inner", this, table);
    }

    public Column<EmpPostTable, Integer> ep_id = new Column<>("ep_id");

    public Column<EmpPostTable, Integer> emp_id = new Column<>("emp_id");

    public Column<EmpPostTable, Integer> post_id = new Column<>("post_id");
}