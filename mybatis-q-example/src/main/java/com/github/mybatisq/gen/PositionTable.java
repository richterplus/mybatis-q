package com.github.mybatisq.gen;

import com.github.mybatisq.Column;
import com.github.mybatisq.Join;
import com.github.mybatisq.Query;
import com.github.mybatisq.Table;

public class PositionTable extends Table {

    PositionTable() {
        super("position", "po");
    }

    public static final PositionTable Position = new PositionTable();

    public Query<PositionTable> query() {
        return new Query<>(Position);
    }

    public <T extends Table> Join<PositionTable, T> inner(T j) {
        return new Join<>("inner", this, j);
    }

    public Column<PositionTable, Integer> poid = new Column<>("poid");

    public Column<PositionTable, String> positionTitle = new Column<>("positionTitle");

    public Column<PositionTable, Integer> pid = new Column<>("pid");
}
