package com.github.mybatisq.mapper;

import com.github.mybatisq.Column;
import com.github.mybatisq.Join;
import com.github.mybatisq.Query;
import com.github.mybatisq.Table;

public class PositionTable extends Table {

    PositionTable() {
        super("position", "p0");
    }

    public static final PositionTable Position = new PositionTable();

    public Query<PositionTable> query() {
        return new Query<>(Position);
    }

    public <T extends Table> Join<PositionTable, T> inner(T p0) {
        return new Join<>("inner", this, p0);
    }

    public Column<PositionTable, Integer> poid = new Column<>("poid");

    public Column<PositionTable, String> positionTitle = new Column<>("positionTitle");

    public Column<PositionTable, Integer> pid = new Column<>("pid");
}