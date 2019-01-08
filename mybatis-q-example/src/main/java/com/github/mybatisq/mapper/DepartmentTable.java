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
public class DepartmentTable extends Table {

    private DepartmentTable() {
        super("department", "d");
    }

    public static final DepartmentTable department = new DepartmentTable();

    public Query<DepartmentTable> query() {
        return new Query<>(department);
    }

    public Insert<DepartmentTable> insert() {
        return new Insert<>(department);
    }

    public Update<DepartmentTable> update() {
        return new Update<>(department);
    }

    public DeleteQuery<DepartmentTable> deleteQuery() {
        return new DeleteQuery<>(department);
    }

    public <T extends Table> Join<DepartmentTable, T> inner(T table) {
        return new Join<>("inner", this, table);
    }

    public Column<DepartmentTable, Integer> dept_id = new Column<>("dept_id");

    public Column<DepartmentTable, String> dept_no = new Column<>("dept_no");

    public Column<DepartmentTable, String> dept_name = new Column<>("dept_name");

    public Column<DepartmentTable, java.util.Date> create_date = new Column<>("create_date");
}