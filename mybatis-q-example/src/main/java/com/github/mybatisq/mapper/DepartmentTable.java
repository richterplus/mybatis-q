package com.github.mybatisq.mapper;

import com.github.mybatisq.Column;
import com.github.mybatisq.Join;
import com.github.mybatisq.Query;
import com.github.mybatisq.Table;

public class DepartmentTable extends Table {

    DepartmentTable() {
        super("department", "d");
    }

    public static final DepartmentTable department = new DepartmentTable();

    public Query<DepartmentTable> query() {
        return new Query<>(department);
    }

    public <T extends Table> Join<DepartmentTable, T> inner(T table) {
        return new Join<>("inner", this, table);
    }

    /**
     * 部门id
     */
    public Column<DepartmentTable, Integer> deptId = new Column<>("deptId");

    /**
     * 部门编号
     */
    public Column<DepartmentTable, String> deptNo = new Column<>("deptNo");

    /**
     * 部门名称
     */
    public Column<DepartmentTable, String> deptName = new Column<>("deptName");

    /**
     * 创建日期
     */
    public Column<DepartmentTable, java.util.Date> createDate = new Column<>("createDate");
}