package com.github.mybatisq.entity;

/**
 * 员工职位
 */
public class EmpPost {

    /**
     * id
     */
    private Integer epid;

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
    public Integer getEpid() {
        return epid;
    }

    /**
     * 设置id
     * @param epid id
     */
    public void setEpid(Integer epid) {
        this.epid = epid;
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