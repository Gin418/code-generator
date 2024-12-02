package com.code.maker.template.model;

import lombok.Builder;
import lombok.Data;

/**
 * packageName com.code.maker.template.model
 *
 * @author Gin
 * @version 1.0.0
 * @title FileFilterConfig
 * @date 2024/12/1 15:42 周日
 * @desreciption 文件过滤配置
 */
@Data
@Builder
public class FileFilterConfig {

    /*
     * 过滤范围
     */
    private String range;

    /*
     * 过滤规则
     */
    private String rule;

    /*
     * 过滤值
     */
    private String value;
}
