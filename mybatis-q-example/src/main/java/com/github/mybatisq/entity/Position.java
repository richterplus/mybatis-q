package com.github.mybatisq.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 岗位
 * @author chenjie
 */
@Data
@Table(name = "position")
public class Position {

    /**
     * 岗位id
     */
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Integer postId;

    /**
     * 岗位编号
     */
    @Column(name = "post_no")
    private String postNo;

    /**
     * 岗位名称
     */
    @Column(name = "post_name")
    private String postName;

}