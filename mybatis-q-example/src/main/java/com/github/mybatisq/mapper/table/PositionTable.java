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
public class PositionTable extends Table {

    private PositionTable() {
        super("position", "p");
    }

    public static final PositionTable position = new PositionTable();

    public Query<PositionTable> query() {
        return new Query<>(position);
    }

    public Update<PositionTable> update() {
        return new Update<>(position);
    }

    public DeleteQuery<PositionTable> deleteQuery() {
        return new DeleteQuery<>(position);
    }

    public <T extends Table> Join<PositionTable, T> join(T table) {
        return new Join<>("inner", this, table);
    }

    public Column<PositionTable, Integer> post_id = new Column<>("post_id");

    public Column<PositionTable, String> post_no = new Column<>("post_no");

    public Column<PositionTable, String> post_name = new Column<>("post_name");
}