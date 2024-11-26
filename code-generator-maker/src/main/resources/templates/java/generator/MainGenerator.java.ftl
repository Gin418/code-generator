package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(new File(outputRootPath, "${name}"),"${fileInfo.outputPath}").getAbsolutePath();
<#if fileInfo.generateType == "static">
${indent}StaticGenerator.copyFilesByHutool(inputPath, outputPath);
<#else>
${indent}DynamicGenerator.doGenerator(inputPath, outputPath, model);
</#if>
</#macro>

/**
 * packageName ${basePackage}.generator
 * @version ${version}
 *
 * @author ${author}
 * @title MainGenerator
 * @date ${createTime}
 * @desreciption 核心代码生成器
 */
public class MainGenerator {
    /*
     * @title doGenerate
     * @date ${createTime}
     * @param Object model 数据模型
     * @return void
     * @throws
     * @description 生成文件
     */
    public static void doGenerate(DataModel model) throws TemplateException, IOException {

        // 输入模板文件根路径
        String inputRootPath = "${fileConfig.inputRootPath}";
        // 输出文件根路径
        String outputRootPath = "${fileConfig.outputRootPath}";

        // 输入文件路径
        String inputPath;
        // 输出文件路径
        String outputPath;
    <#-- 获取模型变量 -->
    <#list modelConfig.models as modelInfo>
        <#-- 组参数 -->
        <#if modelInfo.groupKey??>
            <#list modelInfo.models as groupModelInfo>
        ${groupModelInfo.type} ${groupModelInfo.fieldName} = model.${modelInfo.groupKey}.${groupModelInfo.fieldName};
            </#list>
        <#else>
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
        </#if>
    </#list>

    <#list fileConfig.files as fileInfo>
        <#if fileInfo.type == "group">
        // groupKey = ${fileInfo.groupKey}
            <#if fileInfo.condition??>
        if (${fileInfo.condition}) {
                <#list fileInfo.files as groupFileInfo>
                    <@generateFile indent="            " fileInfo=groupFileInfo/>
                </#list>
        }
            <#else>
                <#list fileInfo.files as groupFileInfo>
                    <@generateFile indent="        " fileInfo=groupFileInfo/>
                </#list>
            </#if>
        <#else>
            <#if fileInfo.condition??>
        if (${fileInfo.condition}) {
                <@generateFile indent="            " fileInfo=fileInfo/>
        }
            <#else>
                <@generateFile indent="        " fileInfo=fileInfo/>
            </#if>
        </#if>
    </#list>
    }
}