package com.code.maker.template.model;

import com.code.maker.meta.Meta;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * packageName com.code.maker.template.model
 *
 * @author Gin
 * @version 1.0.0
 * @title TemplateMakeFileConfig
 * @date 2024/12/1 15:43 周日
 * @desreciption TODO
 */
@Data
public class TemplateMakeFileConfig {

    private List<FileInfoConfig> files;

    private FileGroupConfig fileGroupConfig;

    @Data
    @NoArgsConstructor
    public static class FileInfoConfig {
        private String path;

        private List<FileFilterConfig> filterConfigList;
    }

    @Data
    @NoArgsConstructor
    public static class FileGroupConfig {
        private String condition;

        private String groupKey;

        private String groupName;
    }
}
