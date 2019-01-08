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
        super("Department", "D");
    }

    public static final DepartmentTable Department = new DepartmentTable();

    public Query<DepartmentTable> query() {
        return new Query<>(Department);
    }

    public Insert<DepartmentTable> insert() {
        return new Insert<>(Department);
    }

    public Update<DepartmentTable> update() {
        return new Update<>(Department);
    }

    public DeleteQuery<DepartmentTable> deleteQuery() {
        return new DeleteQuery<>(Department);
    }

    public <T extends Table> Join<DepartmentTable, T> inner(T table) {
        return new Join<>("inner", this, table);
    }

    public Column<DepartmentTable, Integer> deptId = new Column<>("deptId");

    public Column<DepartmentTable, String> deptNo = new Column<>("deptNo");

    public Column<DepartmentTable, String> deptName = new Column<>("deptName");

    public Column<DepartmentTable, java.util.Date> createDate = new Column<>("createDate");
}