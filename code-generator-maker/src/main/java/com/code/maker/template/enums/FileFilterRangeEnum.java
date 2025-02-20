package com.code.maker.template.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * packageName com.code.maker.template.enums
 *
 * @author Gin
 * @version 1.0.0
 * @title FileFilterRangeEnum
 * @date 2024/12/1 15:44 周日
 * @desreciption 文件过滤范围枚举
 */
@Getter
public enum FileFilterRangeEnum {
    FILE_NAME("文件名称", "fileName"),
    FILE_CONTENT("文件内容", "fileContent"),
    ;

    private final String text;

    private final String value;

    FileFilterRangeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /*
     * @param String value
     * @return com.code.maker.template.enums.FileFilterRangeEnum
     * @throws
     * @description 根据 value 获取枚举
     */
    public static FileFilterRangeEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (FileFilterRangeEnum anEnum : FileFilterRangeEnum.values()) {
            if (value.equals(anEnum.value)) {
                return anEnum;
            }
        }
        return null;
    }
}
