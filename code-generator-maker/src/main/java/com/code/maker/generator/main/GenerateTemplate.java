package com.code.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.ZipUtil;
import com.code.maker.generator.GitGenerator;
import com.code.maker.generator.JarGenerator;
import com.code.maker.generator.ScriptGenerator;
import com.code.maker.generator.file.DynamicFileGenerator;
import com.code.maker.meta.Meta;
import com.code.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * packageName com.code.maker.generator.main
 *
 * @author Gin
 * @version 1.0.0
 * @title GenerateTemplate
 * @date 2024/11/19 20:56 周二
 * @desreciption 代码生成器执行模板
 */
public abstract class GenerateTemplate {

    public void doGenerate() throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMetaObject();

        // 1. 获取生成文件的根目录
        String projectPath = System.getProperty("user.dir");
        String metaName = meta.getName();
        String outputPath = new File(new File(projectPath, "generated"), metaName).getAbsolutePath();
        FileUtil.clean(outputPath);
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        // 2. 拷贝原始模板文件到生成的代码包中
        String sourceCopyDestPath = copySource(meta, outputPath);

        // 3. 代码生成
        generateCode(meta, outputPath);

        // 4. 构建 jar 包
        String jarPath = buildJar(meta, outputPath);

        // 5. 封装脚本
        String scriptPath = buildScript(outputPath, jarPath);

        // 6. 生成精简版的代码生成器
        buildDist(outputPath, sourceCopyDestPath, jarPath, scriptPath);
    }

    /**
     * @title buildZip
     * @date 2024/12/14 
     * @param outputPath 需要压缩的文件/目录 路径
     * @return java.lang.String
     * @throws 
     * @description 制作压缩包
     */
    protected String buildZip(String outputPath) {
        String zipPath = outputPath + ".zip";
        ZipUtil.zip(outputPath, zipPath);
        return zipPath;
    }

    /*
     * @title buildDist
     * @date 2024/11/19
     * @param String outputPath
     * @param String sourceCopyDestPath
     * @param String jarPath
     * @param String scriptPath
     * @return java.lang.String
     * @throws 
     * @description 生成精简版的代码生成器
     */
    protected String buildDist(String outputPath, String sourceCopyDestPath, String jarPath, String scriptPath) {
        String distOutputPath = new File(outputPath, "dist").getAbsolutePath();
        // 拷贝 jar 包
        String targetAbsolutePath = new File(distOutputPath, "target").getAbsolutePath();
        FileUtil.mkdir(targetAbsolutePath);
        String JarAbsolutePath = new File(outputPath, jarPath).getAbsolutePath();
        FileUtil.copy(JarAbsolutePath, targetAbsolutePath, false);
        // 拷贝脚本文件
        FileUtil.copy(scriptPath, distOutputPath, false);
        FileUtil.copy(scriptPath + ".bat", distOutputPath, false);
        // 拷贝原模板文件
        FileUtil.copy(sourceCopyDestPath, distOutputPath, false);
        return distOutputPath;
    }

    /*
     * @title buildScript
     * @date 2024/11/19
     * @param String outputPath
     * @param String jarPath
     * @return java.lang.String
     * @throws 
     * @description 封装脚本
     */
    protected String buildScript(String outputPath, String jarPath) {
        String scriptPath = new File(outputPath, "generator").getAbsolutePath();
        ScriptGenerator.doGenerate(scriptPath, jarPath);
        return scriptPath;
    }

    /*
     * @title buildJar
     * @date 2024/11/19
     * @param Meta meta
     * @param String outputPath
     * @return java.lang.String
     * @throws
     * @description 构建 jar 包
     */
    protected String buildJar(Meta meta, String outputPath) throws IOException, InterruptedException {
        JarGenerator.doGenerate(outputPath);
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath = "target/" + jarName;
        return jarPath;
    }

    /*
     * @title generateCode
     * @date 2024/11/19
     * @param Meta meta
     * @param String outputPath
     * @return void
     * @throws
     * @description 代码生成
     */
    protected void generateCode(Meta meta, String outputPath) throws IOException, TemplateException {
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

        // cli.command.JsonGenerateCommand
        inputFilePath = new File(inputResourcesPath, "templates/java/cli/command/JsonGenerateCommand.java.ftl").getAbsolutePath();
        outputFilePath = new File(outputBasejavaPackagePath, "cli/command/JsonGenerateCommand.java").getAbsolutePath();
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

        // README.md
        inputFilePath = new File(inputResourcesPath, "templates/README.md.ftl").getAbsolutePath();
        outputFilePath = new File(outputPath, "README.md").getAbsolutePath();
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, meta);

        // .gitignore
        inputFilePath = new File(inputResourcesPath, "templates/.gitignore.ftl").getAbsolutePath();
        outputFilePath = new File(outputPath, ".gitignore").getAbsolutePath();
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, meta);

        // 是否开启 git 管理
        if (meta.getIsGit()) {
            GitGenerator.doGenerate(outputPath);
        }
    }

    /*
     * @title copySource
     * @date 2024/11/19
     * @param Meta meta
     * @param String outputPath
     * @return java.lang.String
     * @throws
     * @description 复制原始模板文件
     */
    protected String copySource(Meta meta, String outputPath) {
        // 将原始模板文件复制到生成的代码包中
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = new File(outputPath, ".source").getAbsolutePath();
        FileUtil.copy(sourceRootPath, sourceCopyDestPath, false);
        return sourceCopyDestPath;
    }
}
