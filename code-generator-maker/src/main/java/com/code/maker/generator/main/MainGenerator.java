package com.code.maker.generator.main;

/**
 * packageName com.code.maker.generator
 *
 * @author Gin
 * @version 1.0.0
 * @title MainGenerator
 * @date 2024/11/15 21:04 周五
 * @desreciption TODO
 */
public class MainGenerator extends GenerateTemplate {

    @Override
    protected void buildDist(String outputPath, String sourceCopyDestPath, String jarPath, String scriptPath) {
        System.out.println("不需要生成 dist");
    }
}
