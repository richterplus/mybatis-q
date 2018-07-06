package com.github.mybatisq.example;

import com.github.mybatisq.entity.Employee;
import com.github.mybatisq.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/emp")
public class EmployeeController {

    @Autowired
    private EmployeeMapper employeeMapper;

    @GetMapping("{empId}")
    public Employee get(@PathVariable("empId") int empId) {
        EmployeeTable emp = EmployeeTable.employee;
        List<Employee> employees = employeeMapper.select(emp.query().where(emp.emp_id.eq(empId)));
        return employees.size() == 0 ? new Employee() : employees.get(0);
    }

    @GetMapping("/list")
    public List<Employee> list() {
        EmployeeTable e = EmployeeTable.employee;
        DepartmentTable d = DepartmentTable.department;
        EmpDeptTable ed = EmpDeptTable.emp_dept;
        EmpPostTable ep = EmpPostTable.emp_post;

        return employeeMapper.select(e.query()
                .join(e.inner(ed).on(e.emp_id.eq(ed.emp_id)))
                .join(e.inner(ep).on(e.emp_id.eq(ep.emp_id)))
                .join(ed.inner(d).on(ed.dept_id.eq(d.dept_id)).and(d.dept_id.ge(0))));
    }

    @PostMapping
    public Employee update(@RequestBody Employee employee) {
        employeeMapper.update(employee);
        return employee;
    }

    @DeleteMapping("{empId}")
    public Employee delete(@PathVariable("empId") Integer empId) {
        Employee employee = get(empId);
        if (employee != null) {
            employeeMapper.delete(empId);
        }
        return employee;
    }

    @PutMapping
    public Employee create(@RequestBody Employee employee) {
        employee.setCreateDate(new Date());
        employeeMapper.insert(employee);
        return employee;
    }
}
