package com.code.maker.template;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.code.maker.meta.Meta;
import com.code.maker.template.model.TemplateMakerConfig;
import com.code.maker.template.model.TemplateMakerFileConfig;
import com.code.maker.template.model.TemplateMakerModelConfig;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class TemplateMakerTest {

    @Test
    public void makeTemplate() {
    }

    /*
     * 测试同配置多次生成时，强制变为静态生成
     */
    @Test
    public void testMakeTemplateBug1() {
        // 基本信息
        String name = "springboot-init";
        String description = "Spring Boot 初始化模板";
        String author = "Gin";
        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);
        meta.setAuthor(author);

        // 指定原始项目目录
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "code-generator-demo-projects/springboot-template";
        String inputFilePath1 = "src/main/resources/application.yml";

        // 文件配置
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);

        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1));

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();

        // 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDescription("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        templateMakerModelConfig.setModels(Arrays.asList(modelInfoConfig1));

        Long id = TemplateMaker.makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig, 1863584226524897280L);
        System.out.println(id);
    }

    

    /**
     * 同文件目录多次生成时，会扫描新的 FTL 文件
     */
    @Test
    public void testMakeTemplateBug2() {
        // 基本信息
        String name = "springboot-init";
        String description = "Spring Boot 初始化模板";
        String author = "Gin";
        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);
        meta.setAuthor(author);

        // 指定原始项目目录
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "code-generator-demo-projects/springboot-template";

        // 文件配置,扫描路径
        String inputFilePath = "./";
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig.setPath(inputFilePath);

        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig));

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();

        // 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig.setFieldName("className");
        modelInfoConfig.setType("String");
        modelInfoConfig.setReplaceText("BaseResponse");

        templateMakerModelConfig.setModels(Arrays.asList(modelInfoConfig));

        // 第一次执行
        Long id = TemplateMaker.makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig, null);
        // 第二次执行
        TemplateMaker.makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig, id);
        System.out.println(id);
    }

    /**
     * 使用 JSON 制作模板
     */
    @Test
    public void testMakeTemplateWithJSON() {
        String configStr = ResourceUtil.readUtf8Str("templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

}