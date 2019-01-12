package com.github.mybatisq.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.mapper.core.DeleteQuery;
import com.github.mybatisq.mapper.core.Query;
import com.github.mybatisq.mapper.core.Update;
import com.github.mybatisq.mapper.table.EmpPostTable;
import com.github.mybatisq.entity.EmpPost;

/**
 * @author chenjie
 */
@Mapper
public interface EmpPostMapper {

    int count(Query<EmpPostTable> query);

    List<EmpPost> select(Query<EmpPostTable> query);

    int insert(EmpPost empPost);

    int insertSelective(EmpPost empPost);

    int batchInsert(@Param("list") Collection<EmpPost> empPostList);

    int update(EmpPost empPost);

    int updateSelective(EmpPost empPost);

    int batchUpdate(@Param("list") Collection<EmpPost> empPostList);

    int updateByBuilder(Update<EmpPostTable> update);

    int delete(@Param("epId") Integer epId);

    int batchDelete(@Param("epIdList") Collection<Integer> epIdList);

    int deleteByQuery(DeleteQuery<EmpPostTable> query);

}