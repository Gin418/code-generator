package com.code.maker.template.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * packageName com.code.maker.template.enums
 *
 * @author <a href="https://github.com/Gin418">Gin</a>
 * @version 1.0.0
 * @title CodeMatchRuleEnum
 * @date 2024/12/7 15:35 周六
 * @description 代码匹配规则枚举
 */
@Getter
public enum CodeMatchRuleEnum {
    REGEX("正则", "regex"),
    ;

    private final String text;

    private final String value;

    CodeMatchRuleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /*
     * @param String value
     * @return com.code.maker.template.enums.CodeMatchRuleEnum
     * @throws
     * @description 根据 value 获取枚举
     */
    public static CodeMatchRuleEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (CodeMatchRuleEnum anEnum : CodeMatchRuleEnum.values()) {
            if (value.equals(anEnum.value)) {
                return anEnum;
            }
        }
        return null;
    }
}
