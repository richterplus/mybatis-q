package com.github.mybatisq.entity;

/**
 * 员工所在部门
 */
public class EmpDept {

    /**
     * id
     */
    private Integer edid;

    /**
     * 员工id
     */
    private Integer empId;

    /**
     * 部门id
     */
    private Integer deptId;

    /**
     * 获取id
     * @return id
     */
    public Integer getEdid() {
        return edid;
    }

    /**
     * 设置id
     * @param edid id
     */
    public void setEdid(Integer edid) {
        this.edid = edid;
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

}