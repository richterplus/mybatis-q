package com.github.mybatisq.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.Query;
import com.github.mybatisq.entity.Position;

@Mapper
public interface PositionMapper {

    int count(Query<PositionTable> query);

    List<Position> select(Query<PositionTable> query);

    int insert(Position position);

    int update(Position position);

    int delete(@Param("poid") Integer poid);

}