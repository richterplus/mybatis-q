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
public class EmpDeptTable extends Table {

    private EmpDeptTable() {
        super("emp_dept", "e");
    }

    public static final EmpDeptTable emp_dept = new EmpDeptTable();

    public Query<EmpDeptTable> query() {
        return new Query<>(emp_dept);
    }

    public Insert<EmpDeptTable> insert() {
        return new Insert<>(emp_dept);
    }

    public Update<EmpDeptTable> update() {
        return new Update<>(emp_dept);
    }

    public DeleteQuery<EmpDeptTable> deleteQuery() {
        return new DeleteQuery<>(emp_dept);
    }

    public <T extends Table> Join<EmpDeptTable, T> inner(T table) {
        return new Join<>("inner", this, table);
    }

    /**
     * id
     */
    public Column<EmpDeptTable, Integer> ed_id = new Column<>("ed_id");

    /**
     * 员工id
     */
    public Column<EmpDeptTable, Integer> emp_id = new Column<>("emp_id");

    /**
     * 部门id
     */
    public Column<EmpDeptTable, Integer> dept_id = new Column<>("dept_id");
}