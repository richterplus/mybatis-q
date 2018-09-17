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
        super("position", "p");
    }

    public static final PositionTable position = new PositionTable();

    public Query<PositionTable> query() {
        return new Query<>(position);
    }

    public Insert<PositionTable> insert() {
        return new Insert<>(position);
    }

    public Update<PositionTable> update() {
        return new Update<>(position);
    }

    public DeleteQuery<PositionTable> deleteQuery() {
        return new DeleteQuery<>(position);
    }

    public <T extends Table> Join<PositionTable, T> inner(T table) {
        return new Join<>("inner", this, table);
    }

    /**
     * 岗位id
     */
    public Column<PositionTable, Integer> post_id = new Column<>("post_id");

    /**
     * 岗位编号
     */
    public Column<PositionTable, java.lang.String> post_no = new Column<>("post_no");

    /**
     * 岗位名称
     */
    public Column<PositionTable, java.lang.String> post_name = new Column<>("post_name");
}