package com.github.mybatisq.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.DeleteQuery;
import com.github.mybatisq.Query;
import com.github.mybatisq.entity.Employee;

/**
 * @author richterplus
 */
@Mapper
public interface EmployeeMapper {

    int count(Query<EmployeeTable> query);

    List<Employee> select(Query<EmployeeTable> query);

    int insert(Employee employee);

    int batchInsert(@Param("list") Collection<Employee> employee);

    int update(Employee employee);

    int batchUpdate(@Param("list") Collection<Employee> employee);

    int delete(@Param("empId") Integer empId);

    int batchDelete(@Param("empIdList") Collection<Integer> empIdList);

    int deleteByQuery(DeleteQuery<EmployeeTable> query);

}