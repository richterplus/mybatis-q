package com.github.mybatisq.gen;

import java.util.Date;

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

    public <T extends Table> Join<PersonTable, T> inner(T j) {
        return new Join<>("inner", this, j);
    }

    public Column<PersonTable, Integer> pid = new Column<>("pid");

    public Column<PersonTable, String> personName = new Column<>("personName");

    public Column<PersonTable, Date> createDate = new Column<>("createDate");
}
