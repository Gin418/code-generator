package com.code.maker.template;

import com.code.maker.meta.Meta;
import com.code.maker.template.enums.FileFilterRangeEnum;
import com.code.maker.template.enums.FileFilterRuleEnum;
import com.code.maker.template.model.FileFilterConfig;
import com.code.maker.template.model.TemplateMakeFileConfig;
import com.code.maker.template.model.TemplateMakeModelConfig;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

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
        TemplateMakeFileConfig templateMakeFileConfig = new TemplateMakeFileConfig();
        TemplateMakeFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakeFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);

        templateMakeFileConfig.setFiles(Arrays.asList(fileInfoConfig1));

        // 模型参数配置
        TemplateMakeModelConfig templateMakeModelConfig = new TemplateMakeModelConfig();

        // 模型配置
        TemplateMakeModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakeModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDescription("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        templateMakeModelConfig.setModels(Arrays.asList(modelInfoConfig1));

        Long id = TemplateMaker.makeTemplate(meta, originProjectPath, templateMakeFileConfig, templateMakeModelConfig, 1863584226524897280L);
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
        TemplateMakeFileConfig templateMakeFileConfig = new TemplateMakeFileConfig();
        TemplateMakeFileConfig.FileInfoConfig fileInfoConfig = new TemplateMakeFileConfig.FileInfoConfig();
        fileInfoConfig.setPath(inputFilePath);

        templateMakeFileConfig.setFiles(Arrays.asList(fileInfoConfig));

        // 模型参数配置
        TemplateMakeModelConfig templateMakeModelConfig = new TemplateMakeModelConfig();

        // 模型配置
        TemplateMakeModelConfig.ModelInfoConfig modelInfoConfig = new TemplateMakeModelConfig.ModelInfoConfig();
        modelInfoConfig.setFieldName("className");
        modelInfoConfig.setType("String");
        modelInfoConfig.setReplaceText("BaseResponse");

        templateMakeModelConfig.setModels(Arrays.asList(modelInfoConfig));

        // 第一次执行
        Long id = TemplateMaker.makeTemplate(meta, originProjectPath, templateMakeFileConfig, templateMakeModelConfig, null);
        // 第二次执行
        TemplateMaker.makeTemplate(meta, originProjectPath, templateMakeFileConfig, templateMakeModelConfig, id);
        System.out.println(id);
    }
}