package com.github.mybatisq.example;

import com.github.mybatisq.entity.Person;
import com.github.mybatisq.mapper.JobTable;
import com.github.mybatisq.mapper.PersonMapper;
import com.github.mybatisq.mapper.PersonTable;
import com.github.mybatisq.mapper.PositionTable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mybatisq")
public class ExampleController {

    @Autowired
    private PersonMapper personMapper;

    @RequestMapping("/query")
    public List<Person> query() {
        PersonTable p = PersonTable.Person;
        JobTable j = JobTable.Job;
        PositionTable po = PositionTable.Position;
        List<Person> persons = personMapper.select(
                p.query()
                        .join(p.inner(j)
                                .on(p.pid.eq(j.jid))
                                .on(p.pid.eq(j.pid))
                                .and(j.jid.eq(5))
                                .and(j.jobTitle.eq("总裁")))
                        .join(p.inner(po).on(p.pid.eq(po.pid)))
                        .where(p.pid.eq(1))
                        .where(p.personName.eq("陈洁"))
                        .orderBy(p.createDate.asc())
                        .orderBy(p.personName.desc())
                        .limit(10)
                        .skip(5));

        return persons;
    }
    
    @RequestMapping("/insert")
    public Person insert(@RequestBody Person person) {
        personMapper.insert(person);
        return person;
    }
    
    @RequestMapping("/update")
    public Person update(@RequestBody Person person) {
        personMapper.update(person);
        return person;
    }
    
    @RequestMapping("/delete")
    public Person insert(Integer pid) {
        PersonTable p = PersonTable.Person;
        List<Person> persons = personMapper.select(
                p.query()
                        .where(p.pid.eq(pid)));
        Person person = persons.size() > 0 ? persons.get(0) : null;
        
        personMapper.delete(pid);
        
        return person;
    }
}
