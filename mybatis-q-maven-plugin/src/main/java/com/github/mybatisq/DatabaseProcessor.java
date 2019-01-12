package com.github.mybatisq;

import java.util.Collection;
import java.util.List;

/**
 * 数据库处理器
 * @author chenjie
 */
public interface DatabaseProcessor {

    /**
     * 将数据库类型映射到java类型
     * @param dbType 数据库类型
     * @return java类型
     */
    String mapDbTypeToJavaType(String dbType);

    /**
     * 获取映射的表
     * @param includeEntityNames 包含的实体名称
     * @param excludeEntityNames 不包含的实体名称
     * @return 表集合
     */
    List<MappedTable> getMappedTables(Collection<String> includeEntityNames, Collection<String> excludeEntityNames);
}
