package com.code.maker.template.model;

import lombok.Data;

/**
 * packageName com.code.maker.template.model
 *
 * @author <a href="https://github.com/Gin418">Gin</a>
 * @version 1.0.0
 * @title TemplateMakerOutputConfig
 * @date 2024/12/5 19:43 周四
 * @description 输出配置
 */
@Data
public class TemplateMakerOutputConfig {

    // 从未分组文件中移除组内的同名文件
    private boolean removeGroupFilesFromRoot = true;

    // 从未分组模型中移除组内的同名模型
    private boolean removeGroupModelsFromRoot = true;
}
