package com.github.mybatisq.mapper;

import com.github.mybatisq.Column;
import com.github.mybatisq.Join;
import com.github.mybatisq.Query;
import com.github.mybatisq.Table;

public class PersonTable extends Table {

    PersonTable() {
        super("person", "p");
    }

    public static final PersonTable Person = new PersonTable();

    public Query<PersonTable> query() {
        return new Query<>(Person);
    }

    public <T extends Table> Join<PersonTable, T> inner(T p) {
        return new Join<>("inner", this, p);
    }

    public Column<PersonTable, Integer> pid = new Column<>("pid");

    public Column<PersonTable, String> personName = new Column<>("personName");

    public Column<PersonTable, java.util.Date> createDate = new Column<>("createDate");
}