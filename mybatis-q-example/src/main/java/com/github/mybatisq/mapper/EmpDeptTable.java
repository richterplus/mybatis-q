package com.github.mybatisq.mapper;

import com.github.mybatisq.Column;
import com.github.mybatisq.Join;
import com.github.mybatisq.Query;
import com.github.mybatisq.Table;

public class EmpDeptTable extends Table {

    EmpDeptTable() {
        super("empDept", "e");
    }

    public static final EmpDeptTable empDept = new EmpDeptTable();

    public Query<EmpDeptTable> query() {
        return new Query<>(empDept);
    }

    public <T extends Table> Join<EmpDeptTable, T> inner(T table) {
        return new Join<>("inner", this, table);
    }

    /**
     * id
     */
    public Column<EmpDeptTable, Integer> edid = new Column<>("edid");

    /**
     * 员工id
     */
    public Column<EmpDeptTable, Integer> empId = new Column<>("empId");

    /**
     * 部门id
     */
    public Column<EmpDeptTable, Integer> deptId = new Column<>("deptId");
}