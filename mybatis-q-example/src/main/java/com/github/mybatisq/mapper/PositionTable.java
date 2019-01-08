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
public class PositionTable extends Table {

    private PositionTable() {
        super("Position", "P");
    }

    public static final PositionTable Position = new PositionTable();

    public Query<PositionTable> query() {
        return new Query<>(Position);
    }

    public Insert<PositionTable> insert() {
        return new Insert<>(Position);
    }

    public Update<PositionTable> update() {
        return new Update<>(Position);
    }

    public DeleteQuery<PositionTable> deleteQuery() {
        return new DeleteQuery<>(Position);
    }

    public <T extends Table> Join<PositionTable, T> inner(T table) {
        return new Join<>("inner", this, table);
    }

    public Column<PositionTable, Integer> postId = new Column<>("postId");

    public Column<PositionTable, String> postNo = new Column<>("postNo");

    public Column<PositionTable, String> postName = new Column<>("postName");
}