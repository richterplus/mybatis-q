package com.github.mybatisq.mapper;

import com.github.mybatisq.Column;
import com.github.mybatisq.Join;
import com.github.mybatisq.Query;
import com.github.mybatisq.Table;

public class PositionTable extends Table {

    private PositionTable() {
        super("position", "p");
    }

    public static final PositionTable position = new PositionTable();

    public Query<PositionTable> query() {
        return new Query<>(position);
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
    public Column<PositionTable, String> post_no = new Column<>("post_no");

    /**
     * 岗位名称
     */
    public Column<PositionTable, String> post_name = new Column<>("post_name");
}