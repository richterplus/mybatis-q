package com.github.mybatisq.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.DeleteQuery;
import com.github.mybatisq.Query;
import com.github.mybatisq.entity.Position;

/**
 * @author richterplus
 */
@Mapper
public interface PositionMapper {

    int count(Query<PositionTable> query);

    List<Position> select(Query<PositionTable> query);

    int insert(Position position);

    int batchInsert(@Param("list") Collection<Position> position);

    int update(Position position);

    int batchUpdate(@Param("list") Collection<Position> position);

    int delete(@Param("postId") Integer postId);

    int batchDelete(@Param("postIdList") Collection<Integer> postIdList);

    int deleteByQuery(DeleteQuery<PositionTable> query);

}