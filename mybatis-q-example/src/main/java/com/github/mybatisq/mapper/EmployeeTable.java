package com.github.mybatisq.mapper;

import com.github.mybatisq.Column;
import com.github.mybatisq.Join;
import com.github.mybatisq.DeleteQuery;
import com.github.mybatisq.Query;
import com.github.mybatisq.Table;

/**
 * @author richterplus
 */
public class EmployeeTable extends Table {

    private EmployeeTable() {
        super("employee", "e1");
    }

    public static final EmployeeTable employee = new EmployeeTable();

    public Query<EmployeeTable> query() {
        return new Query<>(employee);
    }

    public DeleteQuery<EmployeeTable> deleteQuery() {
        return new DeleteQuery<>(employee);
    }

    public <T extends Table> Join<EmployeeTable, T> inner(T table) {
        return new Join<>("inner", this, table);
    }

    /**
     * 员工id
     */
    public Column<EmployeeTable, Integer> emp_id = new Column<>("emp_id");

    /**
     * 工号
     */
    public Column<EmployeeTable, java.lang.String> emp_no = new Column<>("emp_no");

    /**
     * 员工姓名
     */
    public Column<EmployeeTable, java.lang.String> emp_name = new Column<>("emp_name");

    /**
     * 是否全职
     */
    public Column<EmployeeTable, Boolean> is_fulltime = new Column<>("is_fulltime");

    /**
     * 序列号
     */
    public Column<EmployeeTable, Long> serial_no = new Column<>("serial_no");

    /**
     * 性别（1:男，2:女）
     */
    public Column<EmployeeTable, Integer> gender = new Column<>("gender");

    /**
     * 出生年月
     */
    public Column<EmployeeTable, java.util.Date> birthday = new Column<>("birthday");

    /**
     * 身高
     */
    public Column<EmployeeTable, Float> height = new Column<>("height");

    /**
     * 体重
     */
    public Column<EmployeeTable, Double> weight = new Column<>("weight");

    /**
     * 薪资
     */
    public Column<EmployeeTable, java.math.BigDecimal> salary = new Column<>("salary");

    /**
     * 创建日期
     */
    public Column<EmployeeTable, java.util.Date> create_date = new Column<>("create_date");
}