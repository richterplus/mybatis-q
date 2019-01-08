package com.github.mybatisq.entity;


import com.github.mybatisq.AutoIncrement;
import com.github.mybatisq.Key;

/**
 * 岗位
 * @author richterplus
 */
public class Position {

    /**
     * 岗位id
     */
    @Key
    @AutoIncrement
    private Integer postId;

    /**
     * 岗位编号
     */
    private String postNo;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 获取岗位id
     * @return 岗位id
     */
    public Integer getPostId() {
        return postId;
    }

    /**
     * 设置岗位id
     * @param postId 岗位id
     */
    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    /**
     * 获取岗位编号
     * @return 岗位编号
     */
    public String getPostNo() {
        return postNo;
    }

    /**
     * 设置岗位编号
     * @param postNo 岗位编号
     */
    public void setPostNo(String postNo) {
        this.postNo = postNo;
    }

    /**
     * 获取岗位名称
     * @return 岗位名称
     */
    public String getPostName() {
        return postName;
    }

    /**
     * 设置岗位名称
     * @param postName 岗位名称
     */
    public void setPostName(String postName) {
        this.postName = postName;
    }

}