package com.github.mybatisq;

/**
 * 数据类型映射器
 * @author richterplus
 */
public interface DbTypeMapper {

    /**
     * 将数据库类型映射到java类型
     * @param dbType 数据库类型
     * @return java类型
     */
    String mapDbTypeToJavaType(String dbType);
}
