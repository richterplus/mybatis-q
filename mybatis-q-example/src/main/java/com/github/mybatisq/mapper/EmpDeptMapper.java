package com.github.mybatisq.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.mapper.core.DeleteQuery;
import com.github.mybatisq.mapper.core.Query;
import com.github.mybatisq.mapper.core.Update;
import com.github.mybatisq.mapper.table.EmpDeptTable;
import com.github.mybatisq.entity.EmpDept;

/**
 * @author chenjie
 */
@Mapper
public interface EmpDeptMapper {

    int count(Query<EmpDeptTable> query);

    List<EmpDept> select(Query<EmpDeptTable> query);

    int insert(EmpDept empDept);

    int insertSelective(EmpDept empDept);

    int batchInsert(@Param("list") Collection<EmpDept> empDeptList);

    int update(EmpDept empDept);

    int updateSelective(EmpDept empDept);

    int batchUpdate(@Param("list") Collection<EmpDept> empDeptList);

    int updateByBuilder(Update<EmpDeptTable> update);

    int delete(@Param("edId") Integer edId);

    int batchDelete(@Param("edIdList") Collection<Integer> edIdList);

    int deleteByQuery(DeleteQuery<EmpDeptTable> query);

}