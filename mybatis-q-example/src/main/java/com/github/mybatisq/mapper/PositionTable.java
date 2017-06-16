package com.github.mybatisq.mapper;

import com.github.mybatisq.Column;
import com.github.mybatisq.Join;
import com.github.mybatisq.Query;
import com.github.mybatisq.Table;

public class PositionTable extends Table {

    PositionTable() {
        super("position", "p");
    }

    public static final PositionTable position = new PositionTable();

    public Query<PositionTable> query() {
        return new Query<>(position);
    }

    public <T extends Table> Join<PositionTable, T> inner(T p) {
        return new Join<>("inner", this, p);
    }

    /*
     * 岗位id
     */
    public Column<PositionTable, Integer> postId = new Column<>("postId");

    /*
     * 岗位编号
     */
    public Column<PositionTable, String> postNo = new Column<>("postNo");

    /*
     * 岗位名称
     */
    public Column<PositionTable, String> postName = new Column<>("postName");
}