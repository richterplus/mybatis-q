package com.github.mybatisq.mapper;

import com.github.mybatisq.Column;
import com.github.mybatisq.Join;
import com.github.mybatisq.Query;
import com.github.mybatisq.Table;

public class JobTable extends Table {

    JobTable() {
        super("job", "j");
    }

    public static final JobTable Job = new JobTable();

    public Query<JobTable> query() {
        return new Query<>(Job);
    }

    public <T extends Table> Join<JobTable, T> inner(T j) {
        return new Join<>("inner", this, j);
    }

    public Column<JobTable, Integer> jid = new Column<>("jid");

    public Column<JobTable, String> jobTitle = new Column<>("jobTitle");

    public Column<JobTable, Integer> pid = new Column<>("pid");
}