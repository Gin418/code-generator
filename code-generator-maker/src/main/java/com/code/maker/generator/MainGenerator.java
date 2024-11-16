package com.code.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import com.code.maker.generator.file.DynamicFileGenerator;
import com.code.maker.meta.Meta;
import com.code.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * packageName com.code.maker.generator
 *
 * @author Gin
 * @version 1.0.0
 * @title MainGenerator
 * @date 2024/11/15 21:04 周五
 * @desreciption TODO
 */
public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMetaObject();
        // 获取当前项目根目录
        String projectPath = System.getProperty("user.dir");
        // 生成文件的根目录
        String metaName = meta.getName();
        String outputPath = new File(new File(projectPath, "generated"), metaName).getAbsolutePath();
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }
        // 读取 resource 目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcesPath = classPathResource.getAbsolutePath();

        // java 项目的基础路径
        // com.code
        String outputBasePackage = meta.getBasePackage();
        // 将. 转换为 /
        String outputBasePackagePath = outputBasePackage.replace(".", "/");
        // generated/src/main/java/com/code
        String outputBasejavaPackagePath = new File(new File(outputPath, "src/main/java"), outputBasePackagePath).getAbsolutePath();
        // 输入/输出 文件路径
        String outputFilePath;
        String inputFilePath;

        inputFilePath =new File(inputResourcesPath, "templates/java/model/DataModel.java.ftl").getAbsolutePath();
        outputFilePath = new File(outputBasejavaPackagePath, "model/DataModel.java").getAbsolutePath();
        // 生成文件
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, meta);

        // cli.command.ConfigCommand
        inputFilePath = new File(inputResourcesPath, "templates/java/cli/command/ConfigCommand.java.ftl").getAbsolutePath();
        outputFilePath = new File(outputBasejavaPackagePath, "cli/command/ConfigCommand.java").getAbsolutePath();
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, meta);

        // cli.command.GenerateCommand
        inputFilePath = new File(inputResourcesPath, "templates/java/cli/command/GenerateCommand.java.ftl").getAbsolutePath();
        outputFilePath = new File(outputBasejavaPackagePath, "cli/command/GenerateCommand.java").getAbsolutePath();
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, meta);

        // cli.command.ListCommand
        inputFilePath = new File(inputResourcesPath, "templates/java/cli/command/ListCommand.java.ftl").getAbsolutePath();
        outputFilePath = new File(outputBasejavaPackagePath, "cli/command/ListCommand.java").getAbsolutePath();
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, meta);

        // cli.CommandExecutoe
        inputFilePath = new File(inputResourcesPath, "templates/java/cli/CommandExecutor.java.ftl").getAbsolutePath();
        outputFilePath = new File(outputBasejavaPackagePath, "cli/CommandExecutor.java").getAbsolutePath();
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, meta);

        // Main
        inputFilePath = new File(inputResourcesPath, "templates/java/Main.java.ftl").getAbsolutePath();
        outputFilePath = new File(outputBasejavaPackagePath, "Main.java").getAbsolutePath();
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, meta);

        // generator.DynamicGenerator
        inputFilePath = new File(inputResourcesPath, "templates/java/generator/DynamicGenerator.java.ftl").getAbsolutePath();
        outputFilePath = new File(outputBasejavaPackagePath, "generator/DynamicGenerator.java").getAbsolutePath();
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, meta);

        // generator.StaticGenerator
        inputFilePath = new File(inputResourcesPath, "templates/java/generator/StaticGenerator.java.ftl").getAbsolutePath();
        outputFilePath = new File(outputBasejavaPackagePath, "generator/StaticGenerator.java").getAbsolutePath();
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, meta);

        // generator.MainGenerator
        inputFilePath = new File(inputResourcesPath, "templates/java/generator/MainGenerator.java.ftl").getAbsolutePath();
        outputFilePath = new File(outputBasejavaPackagePath, "generator/MainGenerator.java").getAbsolutePath();
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, meta);

        // pom.xml
        inputFilePath = new File(inputResourcesPath, "templates/pom.xml.ftl").getAbsolutePath();
        outputFilePath = new File(outputPath, "pom.xml").getAbsolutePath();
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, meta);

        // 构建 jar 包
        JarGenerator.doGenerate(outputPath);
    }
}
