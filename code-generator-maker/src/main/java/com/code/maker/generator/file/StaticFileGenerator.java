package com.code.maker.generator.file;

import cn.hutool.core.io.FileUtil;

/**
 * packageName com.code.generator
 *
 * @author Gin
 * @version 1.0.0
 * @title StaticGenerator
 * @date 2024/11/7 20:39 周四
 * @desreciption 静态文件生成器
 */
public class StaticFileGenerator {

    /*
     * @title copyFileByHutool
     * @date 2024/11/7
     * @param String inputPath 输入文件路径
     * @param String outputPath 输出文件路径
     * @return {@link void}
     * @throws
     * @description 拷贝文件
     */
    public static void copyFilesByHutool(String inputPath, String outputPath) {
        FileUtil.copy(inputPath, outputPath, false);
    }
}
