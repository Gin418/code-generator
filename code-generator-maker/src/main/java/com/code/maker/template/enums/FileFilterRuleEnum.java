package com.code.maker.template.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * packageName com.code.maker.template.enums
 *
 * @author Gin
 * @version 1.0.0
 * @title FileFilterRuleEnum
 * @date 2024/12/1 15:43 周日
 * @desreciption 文件过滤规则枚举
 */
@Getter
public enum FileFilterRuleEnum {
    CONTAINS("包含", "contains"),
    STARTS_WITH("前缀匹配", "startsWith"),
    ENDS_WITH("后缀匹配", "endsWith"),
    REGEX("正则", "regex"),
    EQUALS("相等", "equals"),
    ;

    private final String text;

    private final String value;

    FileFilterRuleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /*
     * @param String value
     * @return com.code.maker.template.enums.FileFilterRuleEnum
     * @throws
     * @description 根据 value 获取枚举
     */
    public static FileFilterRuleEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (FileFilterRuleEnum anEnum : FileFilterRuleEnum.values()) {
            if (value.equals(anEnum.value)) {
                return anEnum;
            }
        }
        return null;
    }
}
