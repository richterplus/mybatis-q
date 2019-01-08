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
        super("EmpDept", "E");
    }

    public static final EmpDeptTable EmpDept = new EmpDeptTable();

    public Query<EmpDeptTable> query() {
        return new Query<>(EmpDept);
    }

    public Insert<EmpDeptTable> insert() {
        return new Insert<>(EmpDept);
    }

    public Update<EmpDeptTable> update() {
        return new Update<>(EmpDept);
    }

    public DeleteQuery<EmpDeptTable> deleteQuery() {
        return new DeleteQuery<>(EmpDept);
    }

    public <T extends Table> Join<EmpDeptTable, T> inner(T table) {
        return new Join<>("inner", this, table);
    }

    public Column<EmpDeptTable, Integer> edId = new Column<>("edId");

    public Column<EmpDeptTable, Integer> empId = new Column<>("empId");

    public Column<EmpDeptTable, Integer> deptId = new Column<>("deptId");
}