package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

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

    <#list modelConfig.models as modelInfo>
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
    </#list>

    <#list fileConfig.files as fileInfo>
        <#if fileInfo.condition??>
        if (${fileInfo.condition}) {
            inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
            outputPath = new File(new File(outputRootPath, "${name}"),"${fileInfo.outputPath}").getAbsolutePath();
            <#if fileInfo.generateType == "static">
            //生成静态文件
            StaticGenerator.copyFilesByHutool(inputPath, outputPath);
            <#else>
            //生成动态文件
            DynamicGenerator.doGenerator(inputPath, outputPath, model);
            </#if>
        }

        <#else>
        inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
        outputPath = new File(new File(outputRootPath, "${name}"),"${fileInfo.outputPath}").getAbsolutePath();
        <#if fileInfo.generateType == "static">
        //生成静态文件
        StaticGenerator.copyFilesByHutool(inputPath, outputPath);

        <#else>
        //生成动态文件
        DynamicGenerator.doGenerator(inputPath, outputPath, model);

        </#if>
        </#if>
    </#list>
    }
}