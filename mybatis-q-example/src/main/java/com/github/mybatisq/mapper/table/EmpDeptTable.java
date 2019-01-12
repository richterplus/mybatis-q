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
public class EmpDeptTable extends Table {

    private EmpDeptTable() {
        super("emp_dept", "e");
    }

    public static final EmpDeptTable emp_dept = new EmpDeptTable();

    public Query<EmpDeptTable> query() {
        return new Query<>(emp_dept);
    }

    public Update<EmpDeptTable> update() {
        return new Update<>(emp_dept);
    }

    public DeleteQuery<EmpDeptTable> deleteQuery() {
        return new DeleteQuery<>(emp_dept);
    }

    public <T extends Table> Join<EmpDeptTable, T> join(T table) {
        return new Join<>("inner", this, table);
    }

    public Column<EmpDeptTable, Integer> ed_id = new Column<>("ed_id");

    public Column<EmpDeptTable, Integer> emp_id = new Column<>("emp_id");

    public Column<EmpDeptTable, Integer> dept_id = new Column<>("dept_id");
}