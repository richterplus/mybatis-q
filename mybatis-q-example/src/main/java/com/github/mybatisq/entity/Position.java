package com.github.mybatisq.entity;


/**
 * 岗位
 * @author richterplus
 */
public class Position {

    /**
     * 岗位id
     */
    private Integer postId;

    /**
     * 岗位编号
     */
    private java.lang.String postNo;

    /**
     * 岗位名称
     */
    private java.lang.String postName;

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
    public java.lang.String getPostNo() {
        return postNo;
    }

    /**
     * 设置岗位编号
     * @param postNo 岗位编号
     */
    public void setPostNo(java.lang.String postNo) {
        this.postNo = postNo;
    }

    /**
     * 获取岗位名称
     * @return 岗位名称
     */
    public java.lang.String getPostName() {
        return postName;
    }

    /**
     * 设置岗位名称
     * @param postName 岗位名称
     */
    public void setPostName(java.lang.String postName) {
        this.postName = postName;
    }

}