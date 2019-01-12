package com.github.mybatisq.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.mapper.core.DeleteQuery;
import com.github.mybatisq.mapper.core.Query;
import com.github.mybatisq.mapper.core.Update;
import com.github.mybatisq.mapper.table.EmployeeTable;
import com.github.mybatisq.entity.Employee;

/**
 * @author chenjie
 */
@Mapper
public interface EmployeeMapper {

    int count(Query<EmployeeTable> query);

    List<Employee> select(Query<EmployeeTable> query);

    int insert(Employee employee);

    int insertSelective(Employee employee);

    int batchInsert(@Param("list") Collection<Employee> employeeList);

    int update(Employee employee);

    int updateSelective(Employee employee);

    int batchUpdate(@Param("list") Collection<Employee> employeeList);

    int updateByBuilder(Update<EmployeeTable> update);

    int delete(@Param("empId") Integer empId);

    int batchDelete(@Param("empIdList") Collection<Integer> empIdList);

    int deleteByQuery(DeleteQuery<EmployeeTable> query);

}