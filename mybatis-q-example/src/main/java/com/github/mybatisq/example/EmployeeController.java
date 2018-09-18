package com.github.mybatisq.example;

import com.github.mybatisq.NumberOps;
import com.github.mybatisq.entity.Employee;
import com.github.mybatisq.mapper.*;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author richterplus
 */
@RestController
@RequestMapping("emp")
public class EmployeeController {

    @Autowired
    private EmployeeMapper employeeMapper;

    private static final Date BIRTHDAY = new Date(0);

    private static final Float HEIGHT = 160f;

    private static final Double WEIGHT = 50d;

    private static final BigDecimal SALARY = new BigDecimal(5000);

    private void fillEmployeeData(Employee employee, int no) {
        employee.setEmpNo("No." + (no + 1));
        employee.setEmpName("员工" + (no + 1));
        employee.setIsFulltime(no % 2 == 0);
        employee.setSerialNo(no + 10000L);
        employee.setGender(no % 2 + 1);
        employee.setBirthday(DateUtils.addDays(BIRTHDAY, no));
        employee.setHeight(HEIGHT + no);
        employee.setWeight(WEIGHT + no);
        employee.setSalary(SALARY.add(new BigDecimal(no)));
        employee.setCreateDate(new Date());
    }

    @GetMapping("test")
    public String test() throws ParseException {
        EmployeeTable emp = EmployeeTable.employee;

        /*
        List<Employee> employees = new ArrayList<>(10);

        for (int i = 0; i < 10; i++) {
            Employee employee = new Employee();
            fillEmployeeData(employee, i);
            employees.add(employee);
        }
        employeeMapper.batchInsert(employees);

        for (int i = 0; i < employees.size(); i++) {
            fillEmployeeData(employees.get(i), i + 1000);
        }
        employeeMapper.batchUpdate(employees);

        employeeMapper.deleteByQuery(emp.deleteQuery().where(emp.emp_id.gt(0)));
        employeeMapper.batchDelete(employees.stream().map(Employee::getEmpId).collect(Collectors.toList()));
        */

        /*
        employeeMapper.updateByBuilder(
                emp.update()
                        .set(emp.create_date, new Date())
                        .set(emp.serial_no, NumberOps.plus(10L))
                        .where(emp.emp_id.gt(10))
                        .where(emp.serial_no.gt(0L)));
        */

        List<Employee> employees = employeeMapper.select(emp.query().where(emp.emp_id.eq(1)));

        employees = employeeMapper.select(
                emp.query()
                        .columns(emp.emp_id, emp.emp_name, emp.is_fulltime)
                        .where(emp.height.ge(150f))
                        .where(emp.weight.le(80d))
                        .orderBy(emp.emp_no.asc())
                        .orderBy(emp.create_date.desc())
                        .limit(10).skip(20));

        EmpDeptTable ed = EmpDeptTable.emp_dept;
        DepartmentTable dept = DepartmentTable.department;

        employees = employeeMapper.select(
                emp.query()
                        .join(emp.inner(ed)
                                .on(emp.emp_id.eq(ed.emp_id))
                                .and(ed.dept_id.gt(0)))
                        .join(ed.inner(dept).on(ed.dept_id.eq(dept.dept_id)))
                        .where(emp.emp_id.gt(0)));

        employeeMapper.insertBySelect(
                emp.insert()
                        .columns(emp.emp_no, emp.emp_name, emp.is_fulltime, emp.gender, emp.create_date)
                        .select(emp.query()
                                .columns(emp.emp_no, emp.emp_name, emp.is_fulltime, emp.gender, emp.create_date)
                                .where(emp.emp_id.gt(0))
                                .limit(10)));

        employeeMapper.deleteByQuery(emp.deleteQuery().where(emp.gender.eq(1)).where(emp.emp_no.startWith("A")));

        return "ok";
    }

    @GetMapping("{empId}")
    public Employee get(@PathVariable("empId") int empId) {
        EmployeeTable emp = EmployeeTable.employee;
        List<Employee> employees = employeeMapper.select(emp.query().where(emp.emp_id.eq(empId)));
        return employees.size() == 0 ? new Employee() : employees.get(0);
    }

    @GetMapping("list")
    public List<Employee> list() {
        EmployeeTable e = EmployeeTable.employee;
        DepartmentTable d = DepartmentTable.department;
        EmpDeptTable ed = EmpDeptTable.emp_dept;
        EmpPostTable ep = EmpPostTable.emp_post;

        return employeeMapper.select(e.query()
                .join(e.inner(ed).on(e.emp_id.eq(ed.emp_id)))
                .join(e.inner(ep).on(e.emp_id.eq(ep.emp_id)))
                .join(ed.inner(d).on(ed.dept_id.eq(d.dept_id)).and(d.dept_id.ge(0)))
                .where(e.birthday.gt(new Date())));
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
