package com.github.mybatisq.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.DeleteQuery;
import com.github.mybatisq.Query;
import com.github.mybatisq.Update;
import com.github.mybatisq.entity.EmpPost;

/**
 * @author richterplus
 */
@Mapper
public interface EmpPostMapper {

    int count(Query<EmpPostTable> query);

    List<EmpPost> select(Query<EmpPostTable> query);

    int insert(EmpPost empPost);

    int batchInsert(@Param("list") Collection<EmpPost> empPost);

    int update(EmpPost empPost);

    int batchUpdate(@Param("list") Collection<EmpPost> empPost);

    int updateByBuilder(Update<EmpPostTable> update);

    int delete(@Param("epId") Integer epId);

    int batchDelete(@Param("epIdList") Collection<Integer> epIdList);

    int deleteByQuery(DeleteQuery<EmpPostTable> query);

}