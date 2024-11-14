package com.code.maker.model;

import lombok.Data;

/**
 * packageName com.code.model
 *
 * @author Gin
 * @version 1.0.0
 * @title MainTemplateConfig
 * @date 2024/11/10 19:47 周日
 * @desreciption 动态模板配置
 */
@Data
public class DataModel {

    /*
     * 作者信息
     */
    private String author = "Gin";

    /*
     * 输出信息
     */
    private String outputText = "sum";

    /*
     * 是否循环
     */
    private boolean loop;

}
