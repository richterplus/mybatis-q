package com.github.mybatisq.example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.mybatisq.entity.Person;
import com.github.mybatisq.gen.JobTable;
import com.github.mybatisq.gen.PersonMapper;
import com.github.mybatisq.gen.PersonTable;
import com.github.mybatisq.gen.PositionTable;

@RestController
@RequestMapping("/exp")
public class ExampleController {

    @Autowired
    private PersonMapper personMapper;

    @RequestMapping("/p1")
    public List<Person> p1() {
        PersonTable p = PersonTable.Person;
        JobTable j = JobTable.Job;
        PositionTable po = PositionTable.Position;

        List<Person> persons = personMapper.select(p.query()
                .join(p.inner(j)
                        .on(p.pid.eq(j.pid))
                        .and(j.jobTitle.eq("总裁"))
                        .and(j.jid.ge(0))
                        .and(j.jid.le(10))
                        .and(j.pid.gt(150))
                        .and(j.pid.lt(200)))
                .join(p.inner(po).on(p.pid.eq(po.pid)))
                .where(p.personName.eq("陈洁"))
                .where(p.createDate.between(new Date(1483200000000L), new Date()))
                .where(p.pid.in(Arrays.asList(1, 2, 3)))
                .where(p.personName.startWith("王"))
                .orderBy(p.pid.asc())
                .orderBy(p.createDate.desc())
                .limit(5)
                .skip(10));

        persons = personMapper.select(p.query()
                .where(p.pid.eq(1))
                .where(p.personName.eq("陈洁"))
                .where(p.createDate.eq(new Date(1496246400000L))));

        personMapper.count(p.query().join(p.inner(j).on(p.pid.eq(j.pid))));

        return persons;
    }
}
