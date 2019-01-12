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
public class EmployeeTable extends Table {

    private EmployeeTable() {
        super("employee", "e0");
    }

    public static final EmployeeTable employee = new EmployeeTable();

    public Query<EmployeeTable> query() {
        return new Query<>(employee);
    }

    public Update<EmployeeTable> update() {
        return new Update<>(employee);
    }

    public DeleteQuery<EmployeeTable> deleteQuery() {
        return new DeleteQuery<>(employee);
    }

    public <T extends Table> Join<EmployeeTable, T> join(T table) {
        return new Join<>("inner", this, table);
    }

    public Column<EmployeeTable, Integer> emp_id = new Column<>("emp_id");

    public Column<EmployeeTable, String> emp_no = new Column<>("emp_no");

    public Column<EmployeeTable, String> emp_name = new Column<>("emp_name");

    public Column<EmployeeTable, Boolean> is_fulltime = new Column<>("is_fulltime");

    public Column<EmployeeTable, Long> serial_no = new Column<>("serial_no");

    public Column<EmployeeTable, Integer> gender = new Column<>("gender");

    public Column<EmployeeTable, java.util.Date> birthday = new Column<>("birthday");

    public Column<EmployeeTable, Float> height = new Column<>("height");

    public Column<EmployeeTable, Double> weight = new Column<>("weight");

    public Column<EmployeeTable, java.math.BigDecimal> salary = new Column<>("salary");

    public Column<EmployeeTable, java.util.Date> create_date = new Column<>("create_date");
}