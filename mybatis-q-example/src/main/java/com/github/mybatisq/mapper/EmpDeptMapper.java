package com.github.mybatisq.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.DeleteQuery;
import com.github.mybatisq.Query;
import com.github.mybatisq.Insert;
import com.github.mybatisq.Update;
import com.github.mybatisq.entity.EmpDept;

/**
 * @author richterplus
 */
@Mapper
public interface EmpDeptMapper {

    int count(Query<EmpDeptTable> query);

    List<EmpDept> select(Query<EmpDeptTable> query);

    int insert(EmpDept empDept);

    int batchInsert(@Param("list") Collection<EmpDept> empDept);

    int insertBySelect(Insert<EmpDeptTable> insert);

    int update(EmpDept empDept);

    int batchUpdate(@Param("list") Collection<EmpDept> empDept);

    int updateByBuilder(Update<EmpDeptTable> update);

    int delete(@Param("edId") Integer edId);

    int batchDelete(@Param("edIdList") Collection<Integer> edIdList);

    int deleteByQuery(DeleteQuery<EmpDeptTable> query);

}