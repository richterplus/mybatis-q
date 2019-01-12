package com.github.mybatisq;

/**
 * 表
 * @author chenjie
 */
public abstract class Table {

    /**
     * 新建表
     * @param name 名称
     * @param alias 别名
     */
    protected Table(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    /**
     * 名称
     */
    private final String name;

    /**
     * 别名
     */
    private final String alias;

    /**
     * 获取名称
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取别名
     * @return 别名
     */
    public String getAlias() {
        return alias;
    }
}
