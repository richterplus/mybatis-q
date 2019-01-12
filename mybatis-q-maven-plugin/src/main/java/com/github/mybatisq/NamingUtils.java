package com.github.mybatisq;

import java.util.stream.Stream;

/**
 * 命名工具类
 * @author chenjie
 */
public class NamingUtils {

    /**
     * 生成映射名称
     * @param originalName 原始名称
     * @return 映射名称
     */
    public static String generateMappedName(String originalName) {
        return Stream.of(originalName.split("[_-]")).filter(s -> s.length() > 0).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1)).reduce((a, b) -> a + b).orElse("");
    }
}
