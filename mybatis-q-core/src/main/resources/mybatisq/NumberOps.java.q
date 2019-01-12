package com.github.mybatisq;

/**
 * 数值操作
 * @author chenjie
 * @param <T> 数值类型
 */
public class NumberOps<T extends Number> {

    /**
     * 方法
     */
    String method;

    /**
     * 参数
     */
    Object[] args;

    /**
     * 新建数值操作
     * @param method 方法
     * @param args 参数
     */
    private NumberOps(String method, Object ... args) {
        this.method = method;
        this.args = args;
    }

    /**
     * 加number
     * @param number 数值
     * @param <D> 数值类型
     * @return 数值操作
     */
    public static <D extends Number> NumberOps<D> plus(D number) {
        return new NumberOps<>("plus", number);
    }
}
