package com.code.maker;

import com.code.maker.generator.main.GenerateTemplate;
import com.code.maker.generator.main.MainGenerator;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * packageName com.code
 * @version 1.0.0
 *
 * @author Gin
 * @title Main
 * @date 2024/11/7 19:58 周四
 * @desreciption 代码生成器项目的全局调用入口
 */
public class Main {

    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        GenerateTemplate mainGenerator = new MainGenerator();
        mainGenerator.doGenerate();
    }
}

