package com.github.mybatisq;

/**
 * 日期操作
 * @author chenjie
 */
public class DateOps {

    /**
     * 方法
     */
    private String method;

    /**
     * 参数
     */
    private Object[] args;

    /**
     * 新建日期操作
     * @param method 方法
     * @param args 参数
     */
    private DateOps(String method, Object ... args) {
        this.method = method;
        this.args = args;
    }
}
