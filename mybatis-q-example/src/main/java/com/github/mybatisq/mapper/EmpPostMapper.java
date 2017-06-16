package com.github.mybatisq.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.Query;
import com.github.mybatisq.entity.EmpPost;

@Mapper
public interface EmpPostMapper {

    int count(Query<EmpPostTable> query);

    List<EmpPost> select(Query<EmpPostTable> query);

    int insert(EmpPost empPost);

    int update(EmpPost empPost);

    int delete(@Param("epid") Integer epid);

}