package com.code.maker.meta.enums;

import lombok.Getter;

/**
 * packageName com.code.maker.meta.enums
 *
 * @author Gin
 * @version 1.0.0
 * @title ModelTypeEnum
 * @date 2024/11/19 20:26 周二
 * @desreciption TODO
 */
@Getter
public enum ModelTypeEnum {

    STRING("字符串", "String"),
    BOOLEAN("布尔值", "Boolean"),
    ;

    private final String text;

    private final String value;

    ModelTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
}
