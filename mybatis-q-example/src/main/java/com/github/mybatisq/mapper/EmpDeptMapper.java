package com.github.mybatisq.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.Query;
import com.github.mybatisq.entity.EmpDept;

@Mapper
public interface EmpDeptMapper {

    int count(Query<EmpDeptTable> query);

    List<EmpDept> select(Query<EmpDeptTable> query);

    int insert(EmpDept empDept);

    int batchInsert(@Param("entityList") List<EmpDept> empDept);

    int update(EmpDept empDept);

    int batchUpdate(@Param("entityList") List<EmpDept> empDept);

    int delete(@Param("edId") Integer edId);

}