package com.github.mybatisq.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 员工职位
 * @author chenjie
 */
@Data
@Table(name = "emp_post")
public class EmpPost {

    /**
     * id
     */
    @Id
    @GeneratedValue
    @Column(name = "ep_id")
    private Integer epId;

    /**
     * 员工id
     */
    @Column(name = "emp_id")
    private Integer empId;

    /**
     * 职位id
     */
    @Column(name = "post_id")
    private Integer postId;

}