package com.code.maker.meta;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * packageName com.code.maker.meta
 *
 * @author Gin
 * @version 1.0.0
 * @title MetaValidator
 * @date 2024/11/18 22:54 周一
 * @desreciption 元信息校验
 */
public class MetaValidator {

    public static void doValidAndFill(Meta meta) {
        // 基础信息校验和默认值
        String name = meta.getName();
        if (StrUtil.isBlank(name)) {
            name = "my-generator";
            meta.setName(name);
        }

        String description = meta.getDescription();
        if (StrUtil.isEmpty(description)) {
            description = "我的模板代码生成器";
            meta.setDescription(description);
        }

        String basePackage = meta.getBasePackage();
        if (StrUtil.isBlank(basePackage)) {
            basePackage = "com.code";
            meta.setBasePackage(basePackage);
        }

        String version = meta.getVersion();
        if (StrUtil.isEmpty(version)) {
            version = "1.0";
            meta.setVersion(version);
        }

        String author = meta.getAuthor();
        if (StrUtil.isEmpty(author)) {
            author = "Gin";
            meta.setAuthor(author);
        }

        String createTime = meta.getCreateTime();
        if (StrUtil.isEmpty(createTime)) {
            createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            meta.setCreateTime(createTime);
        }

        Boolean isGit = meta.getIsGit();
        if (isGit == null) {
            isGit = true;
            meta.setIsGit(isGit);
        }

        Meta.FileConfig fileConfig = meta.getFileConfig();
        if (fileConfig != null) {
            // sourceRootPath 必填
            String sourceRootPath = fileConfig.getSourceRootPath();
            if (StrUtil.isBlank(sourceRootPath)) {
                throw new MetaException("sourceRootPath 不能为空！");
            }

            // inputRootPath 必须为 .source + sourceRootPath 的最后一个层级路径
            String inputRootPath = fileConfig.getInputRootPath();
            inputRootPath = ".source" + File.separator + FileUtil.getLastPathEle(Paths.get(sourceRootPath).getFileName()).toString();
            fileConfig.setInputRootPath(inputRootPath);

            String outputRootPath = fileConfig.getOutputRootPath();
            if (StrUtil.isBlank(outputRootPath)) {
                outputRootPath = "generated";
                fileConfig.setOutputRootPath(outputRootPath);
            }
            String fileConfigType = fileConfig.getType();
            if (StrUtil.isBlank(fileConfigType)) {
                fileConfigType = "dir";
                fileConfig.setType(fileConfigType);
            }

            List<Meta.FileConfig.FileInfo> fileInfoList = fileConfig.getFiles();
            if (CollectionUtil.isNotEmpty(fileInfoList)) {
                for (Meta.FileConfig.FileInfo fileInfo : fileInfoList) {
                    // inputPath 必填
                    String inputPath = fileInfo.getInputPath();
                    if (StrUtil.isBlank(inputPath)) {
                        throw new MetaException("inputPath 不能为空！");
                    }

                    String outputPath = fileInfo.getOutputPath();
                    if (StrUtil.isBlank(outputPath)) {
                        // 若 inputPath 后缀为.ftl，则需要删除.ftl后缀, 否则与 inputPath 相同
                        if (inputPath.endsWith(".ftl")) {
                            outputPath = inputPath.substring(0, inputPath.lastIndexOf(".ftl"));
                        }else {
                            outputPath = inputPath;
                        }
                        fileInfo.setOutputPath(outputPath);
                    }
                    // fileInfoType: 默认 inputPath 有文件后缀则为file，否则为dir
                    String fileInfoType = fileInfo.getType();
                    if (StrUtil.isBlank(fileInfoType)) {
                        if (StrUtil.isBlank(FileUtil.getSuffix(inputPath))) {
                            fileInfoType = "dir";
                        } else {
                            fileInfoType = "file";
                        }
                        fileInfo.setType(fileInfoType);
                    }

                    // generateType: 如果 inputPath 后缀为.ftl，则为dynamic，否则为static
                    String generateType = fileInfo.getGenerateType();
                    if (StrUtil.isBlank(generateType)) {
                        if (inputPath.endsWith(".ftl")) {
                            generateType = "dynamic";
                        } else {
                            generateType = "static";
                        }
                        fileInfo.setGenerateType(generateType);
                    }
                }
            }
        }

        Meta.ModelConfig modelConfig = meta.getModelConfig();
        if (modelConfig != null) {
            List<Meta.ModelConfig.ModelInfo> modelInfoList = modelConfig.getModels();
            if (CollectionUtil.isNotEmpty(modelInfoList)) {
                for (Meta.ModelConfig.ModelInfo modelInfo : modelInfoList) {
                    // fileName 必填
                    String fieldName = modelInfo.getFieldName();
                    if (StrUtil.isBlank(fieldName)) {
                        throw new MetaException("fieldName 不能为空！");
                    }

                    // modelInfoType: 默认为String
                    String modelInfoType = modelInfo.getType();
                    if (StrUtil.isBlank(modelInfoType)) {
                        modelInfoType = "String";
                        modelInfo.setType(modelInfoType);
                    }
                }
            }
        }
    }
}