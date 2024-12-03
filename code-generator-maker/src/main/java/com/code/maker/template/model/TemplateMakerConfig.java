package com.code.maker.template.model;

import com.code.maker.meta.Meta;
import lombok.Data;

/**
 * packageName com.code.maker.template.model
 *
 * @author <a href="https://github.com/Gin418">Gin</a>
 * @version 1.0.0
 * @title TemplateMakerConfig
 * @date 2024/12/3 16:07 周二
 * @description 模板制作配置
 */
@Data
public class TemplateMakerConfig {

    private Long id;

    private Meta meta = new Meta();

    private String originProjectPath;

    TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();
}
