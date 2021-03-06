package com.github.mybatisq.entity;


import com.github.mybatisq.AutoIncrement;
import com.github.mybatisq.Key;
import com.github.mybatisq.MapTo;

/**
 * 员工所在部门
 * @author richterplus
 */
@MapTo("emp_dept")
public class EmpDept {

    /**
     * id
     */
    @Key
    @AutoIncrement
    @MapTo("ed_id")
    private Integer edId;

    /**
     * 员工id
     */
    @MapTo("emp_id")
    private Integer empId;

    /**
     * 部门id
     */
    @MapTo("dept_id")
    private Integer deptId;

    /**
     * 获取id
     * @return id
     */
    public Integer getEdId() {
        return edId;
    }

    /**
     * 设置id
     * @param edId id
     */
    public void setEdId(Integer edId) {
        this.edId = edId;
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