package com.code.maker.generator.file;

import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * packageName com.code.generator
 *
 * @author Gin
 * @version 1.0.0
 * @title MainGenerator
 * @date 2024/11/10 21:44 周日
 * @desreciption 核心代码生成器
 */
public class FileGenerator {
    /*
     * @title doGenerate
     * @date 2024/11/10
     * @param Object model 数据模型
     * @return void
     * @throws
     * @description 生成文件
     */
    public static void doGenerate(Object model) throws TemplateException, IOException {

        // 输入模板文件根路径
        String inputRootPath = "C:\\Users\\Q\\IdeaProjects\\code-generator\\code-generator-demo-projects\\acm-template-pro";
        // 输出文件根路径
        String outputRootPath = "C:\\Users\\Q\\IdeaProjects\\code-generator";

        // 输入文件路径
        String inputPath;
        // 输出文件路径
        String outputPath;

        //生成静态文件
        inputPath = new File(inputRootPath, ".gitignore").getAbsolutePath();
        outputPath = new File(new File(outputRootPath, "acm-template-pro-generator"), ".gitignore").getAbsolutePath();
        StaticFileGenerator.copyFilesByHutool(inputPath, outputPath);

        //生成动态文件
        inputPath = new File(inputRootPath, "src/com/code/acm/MainTemplate.java.ftl").getAbsolutePath();
        outputPath = new File(new File(outputRootPath, "acm-template-pro-generator"), "src/com/code/acm/MainTemplate.java").getAbsolutePath();
        DynamicFileGenerator.doGenerator(inputPath, outputPath, model);

    }

}