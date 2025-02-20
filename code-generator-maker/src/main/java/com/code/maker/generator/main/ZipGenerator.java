package com.code.maker.generator.main;

/**
 * packageName com.code.maker.generator.main
 *
 * @author <a href="https://github.com/Gin418">Gin</a>
 * @version 1.0.0
 * @title ZipGenerator
 * @date 2024/12/14 21:49 周六
 * @description 生成代码生成器压缩包
 */
public class ZipGenerator extends GenerateTemplate {
    @Override
    protected String buildDist(String outputPath, String sourceCopyDestPath, String jarPath, String scriptPath) {
        String distPath = super.buildDist(outputPath, sourceCopyDestPath, jarPath, scriptPath);
        return buildZip(distPath);
    }


}
