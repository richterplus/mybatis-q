package com.github.mybatisq.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 员工所在部门
 * @author chenjie
 */
@Data
@Table(name = "emp_dept")
public class EmpDept {

    /**
     * id
     */
    @Id
    @GeneratedValue
    @Column(name = "ed_id")
    private Integer edId;

    /**
     * 员工id
     */
    @Column(name = "emp_id")
    private Integer empId;

    /**
     * 部门id
     */
    @Column(name = "dept_id")
    private Integer deptId;

}