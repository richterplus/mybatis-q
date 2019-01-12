package com.github.mybatisq.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.mapper.core.DeleteQuery;
import com.github.mybatisq.mapper.core.Query;
import com.github.mybatisq.mapper.core.Update;
import com.github.mybatisq.mapper.table.PositionTable;
import com.github.mybatisq.entity.Position;

/**
 * @author chenjie
 */
@Mapper
public interface PositionMapper {

    int count(Query<PositionTable> query);

    List<Position> select(Query<PositionTable> query);

    int insert(Position position);

    int insertSelective(Position position);

    int batchInsert(@Param("list") Collection<Position> positionList);

    int update(Position position);

    int updateSelective(Position position);

    int batchUpdate(@Param("list") Collection<Position> positionList);

    int updateByBuilder(Update<PositionTable> update);

    int delete(@Param("postId") Integer postId);

    int batchDelete(@Param("postIdList") Collection<Integer> postIdList);

    int deleteByQuery(DeleteQuery<PositionTable> query);

}