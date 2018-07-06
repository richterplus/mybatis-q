package com.github.mybatisq.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.Query;
import com.github.mybatisq.entity.Employee;

@Mapper
public interface EmployeeMapper {

    int count(Query<EmployeeTable> query);

    List<Employee> select(Query<EmployeeTable> query);

    int insert(Employee employee);

    int batchInsert(@Param("entityList") List<Employee> employee);

    int update(Employee employee);

    int batchUpdate(@Param("entityList") List<Employee> employee);

    int delete(@Param("empId") Integer empId);

}