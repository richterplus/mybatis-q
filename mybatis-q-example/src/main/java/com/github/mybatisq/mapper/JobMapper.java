package com.github.mybatisq.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.Query;
import com.github.mybatisq.entity.Job;

@Mapper
public interface JobMapper {

    int count(Query<JobTable> query);

    List<Job> select(Query<JobTable> query);

    int insert(Job job);

    int update(Job job);

    int delete(@Param("jid") Integer jid);

}