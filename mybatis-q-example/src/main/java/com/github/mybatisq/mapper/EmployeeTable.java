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
public class EmployeeTable extends Table {

    private EmployeeTable() {
        super("Employee", "E0");
    }

    public static final EmployeeTable Employee = new EmployeeTable();

    public Query<EmployeeTable> query() {
        return new Query<>(Employee);
    }

    public Insert<EmployeeTable> insert() {
        return new Insert<>(Employee);
    }

    public Update<EmployeeTable> update() {
        return new Update<>(Employee);
    }

    public DeleteQuery<EmployeeTable> deleteQuery() {
        return new DeleteQuery<>(Employee);
    }

    public <T extends Table> Join<EmployeeTable, T> inner(T table) {
        return new Join<>("inner", this, table);
    }

    public Column<EmployeeTable, Integer> empId = new Column<>("empId");

    public Column<EmployeeTable, String> empNo = new Column<>("empNo");

    public Column<EmployeeTable, String> empName = new Column<>("empName");

    public Column<EmployeeTable, Boolean> isFulltime = new Column<>("isFulltime");

    public Column<EmployeeTable, Long> serialNo = new Column<>("serialNo");

    public Column<EmployeeTable, Integer> gender = new Column<>("gender");

    public Column<EmployeeTable, java.util.Date> birthday = new Column<>("birthday");

    public Column<EmployeeTable, Float> height = new Column<>("height");

    public Column<EmployeeTable, Double> weight = new Column<>("weight");

    public Column<EmployeeTable, java.math.BigDecimal> salary = new Column<>("salary");

    public Column<EmployeeTable, java.util.Date> createDate = new Column<>("createDate");
}