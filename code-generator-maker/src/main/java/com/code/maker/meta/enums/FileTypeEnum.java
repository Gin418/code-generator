package com.code.maker.meta.enums;

import lombok.Getter;

/**
 * packageName com.code.maker.meta.enums
 *
 * @author Gin
 * @version 1.0.0
 * @title FileTypeEnum
 * @date 2024/11/19 20:10 周二
 * @desreciption TODO
 */
@Getter
public enum FileTypeEnum {
    DIR("目录", "dir"),
    FILE("文件", "file"),
    GROUP("文件组", "group"),
    ;

    private final String text;

    private final String value;

    FileTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
}
