package com.github.mybatisq.example;

import com.github.mybatisq.entity.Employee;
import com.github.mybatisq.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/emp")
public class EmployeeController {

    @Autowired
    private EmployeeMapper employeeMapper;

    @RequestMapping("/get/{empId}")
    public Employee get(@PathVariable("empId") int empId) {
        EmployeeTable emp = EmployeeTable.employee;
        List<Employee> employees = employeeMapper.select(emp.query().where(emp.emp_id.eq(empId)));
        return employees.size() == 0 ? null : employees.get(0);
    }

    @RequestMapping("/list")
    public List<Employee> list() {
        EmployeeTable e = EmployeeTable.employee;
        DepartmentTable d = DepartmentTable.department;
        EmpDeptTable ed = EmpDeptTable.emp_dept;
        EmpPostTable ep = EmpPostTable.emp_post;

        return employeeMapper.select(e.query()
                .join(e.inner(ed).on(e.emp_id.eq(ed.emp_id)))
                .join(e.inner(ep).on(e.emp_id.eq(ep.emp_id)))
                .join(ed.inner(d).on(ed.dept_id.eq(d.dept_id)).and(d.dept_id.eq(1))));
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
