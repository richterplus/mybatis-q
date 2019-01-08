package com.github.mybatisq.entity;

import com.github.mybatisq.AutoIncrement;
import com.github.mybatisq.Key;

/**
 * 部门
 * @author richterplus
 */
public class Department {

    /**
     * 部门id
     */
    @Key
    @AutoIncrement
    private Integer deptId;

    /**
     * 部门编号
     */
    private String deptNo;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 创建日期
     */
    private java.util.Date createDate;

    /**
     * 获取部门id
     * @return 部门id
     */
    public Integer getDeptId() {
        return deptId;
    }

    /**
     * 设置部门id
     * @param deptId 部门id
     */
    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    /**
     * 获取部门编号
     * @return 部门编号
     */
    public String getDeptNo() {
        return deptNo;
    }

    /**
     * 设置部门编号
     * @param deptNo 部门编号
     */
    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    /**
     * 获取部门名称
     * @return 部门名称
     */
    public String getDeptName() {
        return deptName;
    }

    /**
     * 设置部门名称
     * @param deptName 部门名称
     */
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    /**
     * 获取创建日期
     * @return 创建日期
     */
    public java.util.Date getCreateDate() {
        return createDate;
    }

    /**
     * 设置创建日期
     * @param createDate 创建日期
     */
    public void setCreateDate(java.util.Date createDate) {
        this.createDate = createDate;
    }

}