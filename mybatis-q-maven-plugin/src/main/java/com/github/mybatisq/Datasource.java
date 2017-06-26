package com.github.mybatisq;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * 数据源
 */
public class Datasource {

    /**
     * 数据源驱动类
     */
    @Parameter(required = true)
    private String driverClassName;

    /**
     * 连接url
     */
    @Parameter(required = true)
    private String url;

    /**
     * 用户名
     */
    @Parameter(required = true)
    private String username;

    /**
     * 密码
     */
    @Parameter(required = true)
    private String password;

    /**
     * 获取数据源驱动类
     * @return 数据源驱动类
     */
    public String getDriverClassName() {
        return driverClassName;
    }

    /**
     * 设置数据源驱动类
     * @param driverClassName 数据源驱动类
     */
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    /**
     * 获取连接url
     * @return 连接url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置连接url
     * @param url 连接url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取用户名
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
