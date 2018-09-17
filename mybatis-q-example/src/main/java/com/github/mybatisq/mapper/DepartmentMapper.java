package com.github.mybatisq.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.DeleteQuery;
import com.github.mybatisq.Query;
import com.github.mybatisq.Insert;
import com.github.mybatisq.Update;
import com.github.mybatisq.entity.Department;

/**
 * @author richterplus
 */
@Mapper
public interface DepartmentMapper {

    int count(Query<DepartmentTable> query);

    List<Department> select(Query<DepartmentTable> query);

    int insert(Department department);

    int batchInsert(@Param("list") Collection<Department> department);

    int insertBySelect(Insert<DepartmentTable> insert);

    int update(Department department);

    int batchUpdate(@Param("list") Collection<Department> department);

    int updateByBuilder(Update<DepartmentTable> update);

    int delete(@Param("deptId") Integer deptId);

    int batchDelete(@Param("deptIdList") Collection<Integer> deptIdList);

    int deleteByQuery(DeleteQuery<DepartmentTable> query);

}