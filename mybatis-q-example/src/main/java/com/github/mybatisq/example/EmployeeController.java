package com.github.mybatisq.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.mybatisq.entity.Employee;
import com.github.mybatisq.mapper.DepartmentTable;
import com.github.mybatisq.mapper.EmpDeptTable;
import com.github.mybatisq.mapper.EmpPostTable;
import com.github.mybatisq.mapper.EmployeeMapper;
import com.github.mybatisq.mapper.EmployeeTable;
import com.github.mybatisq.mapper.PositionTable;

@RestController
@RequestMapping("/emp")
public class EmployeeController {

    @Autowired
    private EmployeeMapper employeeMapper;

    @RequestMapping("/get/{empId}")
    public Employee get(@PathVariable("empId") int empId) {
        EmployeeTable emp = EmployeeTable.employee;
        List<Employee> employees = employeeMapper.select(emp.query().where(emp.empId.eq(empId)));
        return employees.size() == 0 ? null : employees.get(0);
    }

    @RequestMapping("/list")
    public List<Employee> list() {
        EmployeeTable e = EmployeeTable.employee;
        DepartmentTable d = DepartmentTable.department;
        PositionTable p = PositionTable.position;
        EmpDeptTable ed = EmpDeptTable.empDept;
        EmpPostTable ep = EmpPostTable.empPost;

        return employeeMapper.select(e.query()
                .join(e.inner(ed).on(e.empId.eq(ed.empId)))
                .join(e.inner(ep).on(e.empId.eq(ep.empId)))
                .join(ep.inner(p).on(ep.postId.eq(p.postId)).and(p.postId.eq(1)))
                .join(ed.inner(d).on(ed.deptId.eq(d.deptId)).and(d.deptId.eq(1))));
    }

    @RequestMapping("/update")
    public Employee update(@RequestBody Employee employee) {
        employeeMapper.update(employee);
        return employee;
    }

    @RequestMapping("/delete/{empId}")
    public Employee delete(@PathVariable("empId") Integer empId) {
        Employee employee = get(empId);
        if (employee != null) {
            employeeMapper.delete(empId);
        }
        return employee;
    }

    @RequestMapping("/create")
    public Employee create(@RequestBody Employee employee) {
        employeeMapper.insert(employee);
        return employee;
    }
}
