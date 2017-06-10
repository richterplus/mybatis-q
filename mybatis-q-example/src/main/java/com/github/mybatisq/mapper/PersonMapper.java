package com.github.mybatisq.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.github.mybatisq.Query;
import com.github.mybatisq.entity.Person;

@Mapper
public interface PersonMapper {

    int count(Query<PersonTable> query);

    List<Person> select(Query<PersonTable> query);

    int insert(Person person);

    int update(Person person);

    int delete(@Param("pid") Integer pid);

}