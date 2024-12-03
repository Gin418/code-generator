package com.code.maker.template.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * packageName com.code.maker.template.model
 *
 * @author Gin
 * @version 1.0.0
 * @title TemplateMakerModelConfig
 * @date 2024/12/1 15:43 周日
 * @desreciption 模板制作器模型配置
 */
@Data
public class TemplateMakerModelConfig {

    private List<ModelInfoConfig> models;

    private ModelGroupConfig modelGroupConfig;

    @Data
    @NoArgsConstructor
    public static class ModelInfoConfig {
        private String fieldName;

        private String type;

        private String description;

        private Object defaultValue;

        private String abbr;

        // 替换文本
        private String replaceText;
    }

    @Data
    @NoArgsConstructor
    public static class ModelGroupConfig {
        private String condition;

        private String groupKey;

        private String groupName;
    }
}
