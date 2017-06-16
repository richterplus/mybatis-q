package com.github.mybatisq.mapper;

import com.github.mybatisq.Column;
import com.github.mybatisq.Join;
import com.github.mybatisq.Query;
import com.github.mybatisq.Table;

public class EmployeeTable extends Table {

    EmployeeTable() {
        super("employee", "e1");
    }

    public static final EmployeeTable employee = new EmployeeTable();

    public Query<EmployeeTable> query() {
        return new Query<>(employee);
    }

    public <T extends Table> Join<EmployeeTable, T> inner(T table) {
        return new Join<>("inner", this, table);
    }

    /**
     * 员工id
     */
    public Column<EmployeeTable, Integer> empId = new Column<>("empId");

    /**
     * 工号
     */
    public Column<EmployeeTable, String> empNo = new Column<>("empNo");

    /**
     * 员工姓名
     */
    public Column<EmployeeTable, String> empName = new Column<>("empName");

    /**
     * 是否全职
     */
    public Column<EmployeeTable, Boolean> isFulltime = new Column<>("isFulltime");

    /**
     * 序列号
     */
    public Column<EmployeeTable, Long> serialNo = new Column<>("serialNo");

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
    public Column<EmployeeTable, java.util.Date> createDate = new Column<>("createDate");
}