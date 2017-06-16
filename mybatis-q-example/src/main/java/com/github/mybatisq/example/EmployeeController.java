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
		EmployeeTable emp = EmployeeTable.employee;
		DepartmentTable dept = DepartmentTable.department;
		PositionTable post = PositionTable.position;
		EmpDeptTable empDept = EmpDeptTable.empDept;
		EmpPostTable empPost = EmpPostTable.empPost;
		
		return employeeMapper.select(emp.query()
				.join(emp.inner(empDept)
						.on(emp.empId.eq(empDept.empId)))
				.join(emp.inner(empPost)
						.on(emp.empId.eq(empPost.empId)))
				.join(empPost.inner(post)
						.on(empPost.postId.eq(post.postId))
						.and(post.postId.eq(1)))
				.join(empDept.inner(dept)
						.on(empDept.deptId.eq(dept.deptId))
						.and(dept.deptId.eq(1))));
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
