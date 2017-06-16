package com.github.mybatisq.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.Query;
import com.github.mybatisq.entity.Department;

@Mapper
public interface DepartmentMapper {

    int count(Query<DepartmentTable> query);

    List<Department> select(Query<DepartmentTable> query);

    int insert(Department department);

    int update(Department department);

    int delete(@Param("deptId") Integer deptId);

}