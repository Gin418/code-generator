package com.code.generator;

import com.code.model.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName com.code.generator
 *
 * @author Gin
 * @version 1.0.0
 * @title DynamicGenerator
 * @date 2024/11/10 19:54 周日
 * @desreciption 动态文件生成器
 */
public class DynamicGenerator {
    public static void main(String[] args) throws TemplateException, IOException {

        // 模板文件所在位置
        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);
        String inputPath = new File(projectPath, "code-generator-basic/src/main/resources/templates/MainTemplate.java.ftl").getAbsolutePath();
        String outputPath = new File(projectPath, "code-generator-basic/MainTemplate.java").getAbsolutePath();

        // 创建数据模型
        MainTemplateConfig templateConfig = new MainTemplateConfig();
        templateConfig.setAuthor("Gin");
        templateConfig.setOutputText("结果");
        templateConfig.setLoop(true);

        doGenerator(inputPath, outputPath, templateConfig);

    }

    /*
     * @title doGenerator
     * @date 2024/11/10
     * @param String inputPath 模板文件输入路径
     * @param String outputPath 输出路径
     * @param Object model 数据模型
     * @return {@link void}
     * @throws IOException
     * @throws TemplateException
     * @description 生成文件
     */
    public static void doGenerator(String inputPath, String outputPath, Object model) throws IOException, TemplateException {
        // new Configuration 对象，参数为 freemarker 版本
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);

        // 模板文件所在位置
        File parentFile = new File(inputPath).getParentFile();
        cfg.setDirectoryForTemplateLoading(parentFile);

        // 模板文件使用的字符集
        cfg.setDefaultEncoding("UTF-8");
        cfg.setNumberFormat("0.######");

        // 创建模板对象，加载指定模板
        String templateName = new File(inputPath).getName();
        Template template = cfg.getTemplate(templateName);

        //生成
        Writer out = new FileWriter(outputPath);
        template.process(model, out);

        // 生成文件后别忘了关闭
        out.close();
    }
}
