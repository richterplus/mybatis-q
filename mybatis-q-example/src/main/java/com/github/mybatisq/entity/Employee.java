package com.github.mybatisq.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 人员
 * @author chenjie
 */
@Data
@Table(name = "employee")
public class Employee {

    /**
     * 员工id
     */
    @Id
    @GeneratedValue
    @Column(name = "emp_id")
    private Integer empId;

    /**
     * 工号
     */
    @Column(name = "emp_no")
    private String empNo;

    /**
     * 员工姓名
     */
    @Column(name = "emp_name")
    private String empName;

    /**
     * 是否全职
     */
    @Column(name = "is_fulltime")
    private Boolean isFulltime;

    /**
     * 序列号
     */
    @Column(name = "serial_no")
    private Long serialNo;

    /**
     * 性别（1:男，2:女）
     */
    private Integer gender;

    /**
     * 出生年月
     */
    private java.util.Date birthday;

    /**
     * 身高
     */
    private Float height;

    /**
     * 体重
     */
    private Double weight;

    /**
     * 薪资
     */
    private BigDecimal salary;

    /**
     * 创建日期
     */
    @Column(name = "create_date")
    private Date createDate;

}