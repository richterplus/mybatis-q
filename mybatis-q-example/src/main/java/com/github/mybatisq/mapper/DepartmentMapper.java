package com.github.mybatisq.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.mapper.core.DeleteQuery;
import com.github.mybatisq.mapper.core.Query;
import com.github.mybatisq.mapper.core.Update;
import com.github.mybatisq.mapper.table.DepartmentTable;
import com.github.mybatisq.entity.Department;

/**
 * @author chenjie
 */
@Mapper
public interface DepartmentMapper {

    int count(Query<DepartmentTable> query);

    List<Department> select(Query<DepartmentTable> query);

    int insert(Department department);

    int insertSelective(Department department);

    int batchInsert(@Param("list") Collection<Department> departmentList);

    int update(Department department);

    int updateSelective(Department department);

    int batchUpdate(@Param("list") Collection<Department> departmentList);

    int updateByBuilder(Update<DepartmentTable> update);

    int delete(@Param("deptId") Integer deptId);

    int batchDelete(@Param("deptIdList") Collection<Integer> deptIdList);

    int deleteByQuery(DeleteQuery<DepartmentTable> query);

}