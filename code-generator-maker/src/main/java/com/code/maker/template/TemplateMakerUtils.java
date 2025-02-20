package com.code.maker.template;

import cn.hutool.core.util.StrUtil;
import com.code.maker.meta.Meta;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * packageName com.code.maker.template
 *
 * @author <a href="https://github.com/Gin418">Gin</a>
 * @version 1.0.0
 * @title TemplateMakerUtils
 * @date 2024/12/5 19:13 周四
 * @description 模板制作工具类
 */
public class TemplateMakerUtils {

    /*
     * @title removeGroupFilesFromRoot
     * @date 2024/12/5
     * @param FileInfo> fileInfoList
     * @return java.util.List<com.code.maker.meta.Meta.FileConfig.FileInfo>
     * @throws
     * @description 从未分组文件中移除组内的同名文件
     */
    public static List<Meta.FileConfig.FileInfo> removeGroupFilesFromRoot(List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 获取所有的分组
        List<Meta.FileConfig.FileInfo> groupFileInfoList = fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());

        // 获取所有分组内的文件列表
        List<Meta.FileConfig.FileInfo> groupInnerFileInfoList = groupFileInfoList.stream()
                .flatMap(fileInfo -> fileInfo.getFiles().stream())
                .collect(Collectors.toList());

        // 获取所有分组内文件输入路径集合
        Set<String> fileInputPathSet = groupInnerFileInfoList.stream()
                .map(Meta.FileConfig.FileInfo::getInputPath)
                .collect(Collectors.toSet());

        // 移除所有输入路径在 set 中的外层文件
        return fileInfoList.stream()
                .filter(fileInfo -> !fileInputPathSet.contains(fileInfo.getInputPath()))
                .collect(Collectors.toList());
    }

    /*
     * @title removeGroupModelsFromRoot
     * @date 2024/12/6
     * @param ModelInfo> modelInfoList
     * @return java.util.List<com.code.maker.meta.Meta.ModelConfig.ModelInfo>
     * @throws
     * @description 从未分组模型中移除组内的同名模型
     */
    public static List<Meta.ModelConfig.ModelInfo> removeGroupModelsFromRoot(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        // 获取所有的分组
        List<Meta.ModelConfig.ModelInfo> groupModelInfoList = modelInfoList.stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toList());

        // 获取所有分组内的模型列表
        List<Meta.ModelConfig.ModelInfo> groupInnerModelInfoList = groupModelInfoList.stream()
                .flatMap(modelInfo -> modelInfo.getModels().stream())
                .collect(Collectors.toList());

        // 获取所有分组内模型输入路径集合
        Set<String> modelInputPathSet = groupInnerModelInfoList.stream()
                .map(Meta.ModelConfig.ModelInfo::getFieldName)
                .collect(Collectors.toSet());

        // 移除所有输入路径在 set 中的外层模型
        return modelInfoList.stream()
                .filter(modelInfo -> !modelInputPathSet.contains(modelInfo.getFieldName()))
                .collect(Collectors.toList());
    }
}
