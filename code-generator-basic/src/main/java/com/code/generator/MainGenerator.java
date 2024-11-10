package com.code.generator;

import com.code.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * packageName com.code.generator
 * @version 1.0.0
 *
 * @author Gin
 * @title MainGenerator
 * @date 2024/11/10 21:44 周日
 * @desreciption 核心代码生成器
 */
public class MainGenerator {

    public static void main(String[] args) throws TemplateException, IOException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("Gin");
        mainTemplateConfig.setLoop(true);
        mainTemplateConfig.setOutputText("求和结果");
        doGenerate(mainTemplateConfig);
    }

    /*
     * @title doGenerate
     * @date 2024/11/10
     * @param Object model 数据模型
     * @return void
     * @throws
     * @description 生成文件
     */
    public static void doGenerate(Object model) throws TemplateException, IOException {
        // 整个项目的根路径
        String projectPath = System.getProperty("user.dir");
        //生成静态文件
        String inputPath = new File(projectPath, "code-generator-demo-projects/acm-template").getAbsolutePath();
        String outputPath = projectPath;
        StaticGenerator.copyFilesByRecursive(inputPath, outputPath);

        //生成动态文件
        String inputDynamicFilePath = new File(projectPath, "code-generator-basic/src/main/resources/templates/MainTemplate.java.ftl")
                .getAbsolutePath();
        String outputDynamicFilePath = new File(projectPath, "acm-template/src/com/code/acm/MainTemplate.java")
                .getAbsolutePath();
        DynamicGenerator.doGenerator(inputDynamicFilePath, outputDynamicFilePath, model);

    }

}
