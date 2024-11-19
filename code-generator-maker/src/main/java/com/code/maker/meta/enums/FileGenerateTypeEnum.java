package com.code.maker.meta.enums;

import lombok.Getter;

/**
 * packageName com.code.maker.meta.enums
 *
 * @author Gin
 * @version 1.0.0
 * @title FileGenerateTypeEnum
 * @date 2024/11/19 20:23 周二
 * @desreciption TODO
 */
@Getter
public enum FileGenerateTypeEnum {

    DYNAMIC("动态", "dynamic"),
    STATIC("静态", "static"),
    ;

    private final String text;

    private final String value;

    FileGenerateTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

}
