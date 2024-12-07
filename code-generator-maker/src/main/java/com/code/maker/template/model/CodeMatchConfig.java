package com.code.maker.template.model;

import lombok.Data;

/**
 * packageName com.code.maker.template.model
 *
 * @author <a href="https://github.com/Gin418">Gin</a>
 * @version 1.0.0
 * @title CodeMatchConfig
 * @date 2024/12/7 15:43 周六
 * @description 代码匹配配置
 */
@Data
public class CodeMatchConfig {

    /**
     * 匹配类型
     */
    private String type;

    /**
     * 匹配内容
     */
    private String value;

    /**
     * 代码格式
     */
    private String format;
}
