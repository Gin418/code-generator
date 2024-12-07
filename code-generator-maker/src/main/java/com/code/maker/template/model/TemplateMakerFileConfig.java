package com.code.maker.template.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * packageName com.code.maker.template.model
 *
 * @author Gin
 * @version 1.0.0
 * @title TemplateMakerFileConfig
 * @date 2024/12/1 15:43 周日
 * @desreciption 模板制作器文件配置
 */
@Data
public class TemplateMakerFileConfig {

    private List<FileInfoConfig> files;

    private FileGroupConfig fileGroupConfig;

    @Data
    @NoArgsConstructor
    public static class FileInfoConfig {
        private String path;

        private String condition;

        private List<FileFilterConfig> filterConfigList;

        private List<CodeMatchConfig> matchConfigList;
    }

    @Data
    @NoArgsConstructor
    public static class FileGroupConfig {
        private String condition;

        private String groupKey;

        private String groupName;

        private String type;
    }
}
