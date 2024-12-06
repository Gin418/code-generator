package com.code.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.code.maker.meta.Meta;
import com.code.maker.meta.enums.FileGenerateTypeEnum;
import com.code.maker.meta.enums.FileTypeEnum;
import com.code.maker.template.model.TemplateMakerConfig;
import com.code.maker.template.model.TemplateMakerFileConfig;
import com.code.maker.template.model.TemplateMakerModelConfig;
import com.code.maker.template.model.TemplateMakerOutputConfig;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName com.code.maker.template
 *
 * @author Gin
 * @version 1.0.0
 * @title TemplateMaker
 * @date 2024/11/27 11:08 周三
 * @desreciption 模板制作工具
 */
public class TemplateMaker {

    /**
     * 工作空间的目录
     */
    public static final String WORKSPACE_DIRECTORY = ".temp";
    /**
     * 模板文件的后缀
     */
    public static final String TEMPLATE_FILE_SUFFIX = ".ftl";
    /**
     * 元信息名称
     */
    public static final String META_INFORMATION_NAME = "meta.json";

    /*
     * @title makeTemplate
     * @date 2024/12/3
     * @param TemplateMakerConfig templateMakerConfig
     * @return java.lang.Long
     * @throws
     * @description 制作模板
     */
    public static Long makeTemplate(TemplateMakerConfig templateMakerConfig) {
        Long id = templateMakerConfig.getId();
        Meta meta = templateMakerConfig.getMeta();
        String originProjectPath = templateMakerConfig.getOriginProjectPath();
        TemplateMakerFileConfig fileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig modelConfig = templateMakerConfig.getModelConfig();
        TemplateMakerOutputConfig outputConfig = templateMakerConfig.getOutputConfig();

        return makeTemplate(meta, originProjectPath, fileConfig, modelConfig, outputConfig, id);
    }

    /*
     * @title makeTemplate
     * @date 2024/11/28
     * @param Meta newMeta
     * @param String originProjectPath
     * @param TemplateMakerFileConfig templateMakerFileConfig
     * @param TemplateMakerModelConfig templateMakerModelConfig
     * @param Long id
     * @return java.lang.Long
     * @throws
     * @description 制作模板
     */
    public static Long makeTemplate(Meta newMeta,
                                    String originProjectPath,
                                    TemplateMakerFileConfig templateMakerFileConfig,
                                    TemplateMakerModelConfig templateMakerModelConfig,
                                    TemplateMakerOutputConfig templateMakerOutputConfig,
                                    Long id) {
        // 如果 id 没有则生成
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }

        // 复制目录
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + WORKSPACE_DIRECTORY;
        String templatePath = tempDirPath + File.separator + id;
        // 是否为首次制作模板
        // 目录不存在，这是首次制作
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originProjectPath, templatePath, true);
        }

        // 1. 输入信息
        // 输入文件
        String sourceRootPath = FileUtil.loopFiles(new File(templatePath), 1, null)
                .stream()
                .filter(File::isDirectory)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAbsolutePath();
        // windows 系统需要对路径进行转义
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");

        // 新增的模型配置列表
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = getModelInfoList(templateMakerModelConfig);

        // 2. 生成模板文件
        List<Meta.FileConfig.FileInfo> newFileInfoList = makeFileTemplates(templateMakerFileConfig, templateMakerModelConfig, sourceRootPath);

        // 3. 生成配置文件
        String metaOutputPath = templatePath + File.separator + META_INFORMATION_NAME;

        // 如果已有 meta 文件，则在已有文件基础上修改，否则构造配置参数
        if (FileUtil.exist(metaOutputPath)) {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            // 将新对象的值复制到老对象上（如果属性为空则不复制）
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            // 追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);

            // 配置去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));
        } else {
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileInfoList.addAll(newFileInfoList);
            fileConfig.setFiles(fileInfoList);
            newMeta.setFileConfig(fileConfig);

            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelInfoList.addAll(newModelInfoList);
            modelConfig.setModels(modelInfoList);
            newMeta.setModelConfig(modelConfig);
        }

        // 输出配置
        if (null != templateMakerOutputConfig) {
            // 从未分组文件中移除组内的同名文件
            if (templateMakerOutputConfig.isRemoveGroupFilesFromRoot()) {
                newFileInfoList = TemplateMakerUtils.removeGroupFilesFromRoot(newMeta.getFileConfig().getFiles());
                newMeta.getFileConfig().setFiles(newFileInfoList);
            }
            // 从未分组模型中移除组内的同名模型
            if (templateMakerOutputConfig.isRemoveGroupModelsFromRoot()) {
                newModelInfoList = TemplateMakerUtils.removeGroupModelsFromRoot(newMeta.getModelConfig().getModels());
                newMeta.getModelConfig().setModels(newModelInfoList);
            }
        }

        // 输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);
        return id;
    }

    /*
     * @title getModelInfoList
     * @date 2024/12/4
     * @param TemplateMakerModelConfig templateMakerModelConfig
     * @return java.util.List<com.code.maker.meta.Meta.ModelConfig.ModelInfo>
     * @throws 
     * @description 获取模型配置列表
     */
    private static List<Meta.ModelConfig.ModelInfo> getModelInfoList(TemplateMakerModelConfig templateMakerModelConfig) {
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();

        // 判断模型配置是否为空
        if (templateMakerModelConfig == null) {
            return newModelInfoList;
        }

        // 处理模型信息
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        if (CollUtil.isEmpty(models)) {
            return newModelInfoList;
        }

        // 转换为配置接受的 ModelInfo 对象
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream()
                .map(modelInfoConfig -> {
                    Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
                    BeanUtil.copyProperties(modelInfoConfig, modelInfo);
                    return modelInfo;
                }).collect(Collectors.toList());

        // 模型分组配置
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if (modelGroupConfig != null) {
            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelGroupConfig, groupModelInfo);
            groupModelInfo.setModels(inputModelInfoList);
            // 模型放到一个分组内
            newModelInfoList.add(groupModelInfo);
        } else {
            // 不分组，添加所有模型信息到列表
            newModelInfoList.addAll(inputModelInfoList);
        }
        return newModelInfoList;
    }

    /*
     * @title makeFileTemplates
     * @date 2024/12/4
     * @param TemplateMakerFileConfig templateMakerFileConfig
     * @param TemplateMakerModelConfig templateMakerModelConfig
     * @param String sourceRootPath
     * @return java.util.List<com.code.maker.meta.Meta.FileConfig.FileInfo>
     * @throws
     * @description 生成多个文件模板文件
     */
    private static List<Meta.FileConfig.FileInfo> makeFileTemplates(TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();

        // 判断文件配置是否为空
        if (templateMakerFileConfig == null) {
            return newFileInfoList;
        }

        List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList = templateMakerFileConfig.getFiles();
        if (CollUtil.isEmpty(fileConfigInfoList)) {
            return newFileInfoList;
        }

        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : templateMakerFileConfig.getFiles()) {
            String inputFilePath = fileInfoConfig.getPath();
            String inputFileAbsolutePath = sourceRootPath + File.separator + inputFilePath;

            // 传入绝对路径和过滤配置，返回文件列表
            List<File> fileList = FileFilter.doFilter(inputFileAbsolutePath, fileInfoConfig.getFilterConfigList());
            // 不处理已生成的 FTL 模板文件
            fileList = fileList.stream()
                    .filter(file -> !file.getAbsolutePath().endsWith(".ftl"))
                    .collect(Collectors.toList());
            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(templateMakerModelConfig, sourceRootPath, file, fileInfoConfig);
                newFileInfoList.add(fileInfo);
            }
        }

        // 文件分组配置
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if (fileGroupConfig != null) {
            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();
            BeanUtil.copyProperties(fileGroupConfig, groupFileInfo);
            groupFileInfo.setFiles(newFileInfoList);
            // 文件放到一个分组内
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
        }
        return newFileInfoList;
    }

    /*
     * @title makerFileTemplate
     * @date 2024/11/30
     * @param TemplateMakerModelConfig templateMakerModelConfig
     * @param String sourceRootPath
     * @param File inputFile
     * @param TemplateMakerFileConfig.FileInfoConfig fileInfoConfig
     * @return com.code.maker.meta.Meta.FileConfig.FileInfo
     * @throws
     * @description 制作单个模板文件
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(
            TemplateMakerModelConfig templateMakerModelConfig,
            String sourceRootPath,
            File inputFile,
            TemplateMakerFileConfig.FileInfoConfig fileInfoConfig) {
        // 文件输入输出绝对路径
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");
        String fileOutputAbsolutePath = fileInputAbsolutePath + TEMPLATE_FILE_SUFFIX;

        // 模板文件路径(相对路径)
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileOutputPath = fileInputPath + TEMPLATE_FILE_SUFFIX;

        String fileContent = null;
        // 如果已有模板文件，则在已有模板基础上修改，否则读取原始模板
        boolean hasTemplateFile = FileUtil.exist(fileOutputAbsolutePath);
        if (hasTemplateFile) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        // 支持多个模型：对于同一个文件的内容，遍历模型进行替换
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        String newFileContent = fileContent;
        String replacement;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            if (modelGroupConfig == null) {
                // 不是分组
                replacement = String.format("${%s}", modelInfoConfig.getFieldName());
            } else {
                // 是分组，要多一个层级
                String groupKey = modelGroupConfig.getGroupKey();
                replacement = String.format("${%s.%s}", groupKey, modelInfoConfig.getFieldName());
            }
            // 文本替换
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
        }

        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        // 文件输入路径和输出路径反转
        fileInfo.setInputPath(fileOutputPath);
        fileInfo.setOutputPath(fileInputPath);
        fileInfo.setCondition(fileInfoConfig.getCondition());
        fileInfo.setType(FileTypeEnum.FILE.getValue());

        // 对比替换前后文件的内容是否一致
        boolean contentEquals = fileContent.equals(newFileContent);
        // 只有在之前不存在模板文件并且内容没有更改时，才是静态生成，否则为动态生成
        if (!hasTemplateFile && contentEquals) {
            // 输入路径没用 .ftl 后缀
            fileInfo.setInputPath(fileInputPath);
            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
        } else {
            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
            // 输出模板文件
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }
        return fileInfo;
    }

    /*
     * @title distinctFiles
     * @date 2024/11/28
     * @param FileInfo> fileInfoList
     * @return java.util.List<com.code.maker.meta.Meta.FileConfig.FileInfo>
     * @throws
     * @description 文件去重
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 1. 将所有文件配置（fileInfo）分为有分组和无分组
        // 处理有分组的文件
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));

        // 2. 同组内配置合并去重
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfig.FileInfo> tempFileInfoList = entry.getValue();
            List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(tempFileInfoList.stream()
                    .flatMap(fileInfo -> fileInfo.getFiles().stream())
                    .collect(
                            Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (e, r) -> r)
                    ).values());

            // 使用新的 group 配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey, newFileInfo);
        }

        // 3. 创建新的文件配置列表，先将合并后的分组添加到结果列表
        List<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

        // 4. 将无分组的文件配置列表添加到结果列表
        resultList.addAll(new ArrayList<>(
                fileInfoList.stream()
                        .filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                        .collect(
                                Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (e, r) -> r)
                        ).values()));
        return resultList;
    }

    /*
     * @title distinctModels
     * @date 2024/11/28
     * @param ModelInfo> modelInfoList
     * @return java.util.List<com.code.maker.meta.Meta.ModelConfig.ModelInfo>
     * @throws
     * @description 模型去重
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        // 1. 将所有模型配置（modelInfo）分为有分组和无分组
        // 处理有分组的模型
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfoListMap = modelInfoList.stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));

        // 2. 同组内配置合并去重
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergedModelInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeyModelInfoListMap.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> tempModelInfoList = entry.getValue();
            List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(tempModelInfoList.stream()
                    .flatMap(fileInfo -> fileInfo.getModels().stream())
                    .collect(
                            Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)
                    ).values());

            // 使用新的 group 配置
            Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(tempModelInfoList);
            newModelInfo.setModels(newModelInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedModelInfoMap.put(groupKey, newModelInfo);
        }

        // 3. 创建新的模型配置列表，先将合并后的分组添加到结果列表
        List<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(groupKeyMergedModelInfoMap.values());

        // 4. 将无分组的模型配置列表添加到结果列表
        resultList.addAll(new ArrayList<>(
                modelInfoList.stream()
                        .filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                        .collect(
                                Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)
                        ).values()));
        return resultList;
    }
}
