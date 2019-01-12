package com.github.mybatisq.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 部门
 * @author chenjie
 */
@Data
@Table(name = "department")
public class Department {

    /**
     * 部门id
     */
    @Id
    @GeneratedValue
    @Column(name = "dept_id")
    private Integer deptId;

    /**
     * 部门编号
     */
    @Column(name = "dept_no")
    private String deptNo;

    /**
     * 部门名称
     */
    @Column(name = "dept_name")
    private String deptName;

    /**
     * 创建日期
     */
    @Column(name = "create_date")
    private Date createDate;

}