package com.github.mybatisq.entity;


import com.github.mybatisq.AutoIncrement;
import com.github.mybatisq.Key;
import com.github.mybatisq.MapTo;

/**
 * 人员
 * @author richterplus
 */
@MapTo("employee")
public class Employee {

    /**
     * 员工id
     */
    @Key
    @AutoIncrement
    @MapTo("emp_id")
    private Integer empId;

    /**
     * 工号
     */
    @MapTo("emp_no")
    private String empNo;

    /**
     * 员工姓名
     */
    @MapTo("emp_name")
    private String empName;

    /**
     * 是否全职
     */
    @MapTo("is_fulltime")
    private Boolean isFulltime;

    /**
     * 序列号
     */
    @MapTo("serial_no")
    private Long serialNo;

    /**
     * 性别（1:男，2:女）
     */
    @MapTo("gender")
    private Integer gender;

    /**
     * 出生年月
     */
    @MapTo("birthday")
    private java.util.Date birthday;

    /**
     * 身高
     */
    @MapTo("height")
    private Float height;

    /**
     * 体重
     */
    @MapTo("weight")
    private Double weight;

    /**
     * 薪资
     */
    @MapTo("salary")
    private java.math.BigDecimal salary;

    /**
     * 创建日期
     */
    @MapTo("create_date")
    private java.util.Date createDate;

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
     * 获取工号
     * @return 工号
     */
    public String getEmpNo() {
        return empNo;
    }

    /**
     * 设置工号
     * @param empNo 工号
     */
    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    /**
     * 获取员工姓名
     * @return 员工姓名
     */
    public String getEmpName() {
        return empName;
    }

    /**
     * 设置员工姓名
     * @param empName 员工姓名
     */
    public void setEmpName(String empName) {
        this.empName = empName;
    }

    /**
     * 获取是否全职
     * @return 是否全职
     */
    public Boolean getIsFulltime() {
        return isFulltime;
    }

    /**
     * 设置是否全职
     * @param isFulltime 是否全职
     */
    public void setIsFulltime(Boolean isFulltime) {
        this.isFulltime = isFulltime;
    }

    /**
     * 获取序列号
     * @return 序列号
     */
    public Long getSerialNo() {
        return serialNo;
    }

    /**
     * 设置序列号
     * @param serialNo 序列号
     */
    public void setSerialNo(Long serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * 获取性别（1:男，2:女）
     * @return 性别（1:男，2:女）
     */
    public Integer getGender() {
        return gender;
    }

    /**
     * 设置性别（1:男，2:女）
     * @param gender 性别（1:男，2:女）
     */
    public void setGender(Integer gender) {
        this.gender = gender;
    }

    /**
     * 获取出生年月
     * @return 出生年月
     */
    public java.util.Date getBirthday() {
        return birthday;
    }

    /**
     * 设置出生年月
     * @param birthday 出生年月
     */
    public void setBirthday(java.util.Date birthday) {
        this.birthday = birthday;
    }

    /**
     * 获取身高
     * @return 身高
     */
    public Float getHeight() {
        return height;
    }

    /**
     * 设置身高
     * @param height 身高
     */
    public void setHeight(Float height) {
        this.height = height;
    }

    /**
     * 获取体重
     * @return 体重
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * 设置体重
     * @param weight 体重
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * 获取薪资
     * @return 薪资
     */
    public java.math.BigDecimal getSalary() {
        return salary;
    }

    /**
     * 设置薪资
     * @param salary 薪资
     */
    public void setSalary(java.math.BigDecimal salary) {
        this.salary = salary;
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