package com.github.mybatisq.entity;


import com.github.mybatisq.AutoIncrement;
import com.github.mybatisq.Key;

/**
 * 员工职位
 * @author richterplus
 */
public class EmpPost {

    /**
     * id
     */
    @Key
    @AutoIncrement
    private Integer epId;

    /**
     * 员工id
     */
    private Integer empId;

    /**
     * 职位id
     */
    private Integer postId;

    /**
     * 获取id
     * @return id
     */
    public Integer getEpId() {
        return epId;
    }

    /**
     * 设置id
     * @param epId id
     */
    public void setEpId(Integer epId) {
        this.epId = epId;
    }

    /**
     * 获取员工id
     * @return 员工id
     */
    public Integer getEmpId() {
        return empId;
    }

    /**
     * 设置员工id
     * @param empId 员工id
     */
    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    /**
     * 获取职位id
     * @return 职位id
     */
    public Integer getPostId() {
        return postId;
    }

    /**
     * 设置职位id
     * @param postId 职位id
     */
    public void setPostId(Integer postId) {
        this.postId = postId;
    }

}