package com.code.maker.generator;

import java.io.File;
import java.io.IOException;

/**
 * packageName com.code.maker.generator
 *
 * @author Gin
 * @version 1.0.0
 * @title GitGenerator
 * @date 2024/11/17 19:08 周日
 * @desreciption TODO
 */
public class GitGenerator {
    public static void doGenerate(String projectPath) throws IOException {
        // 执行 git init 命令
        String command = "git init";
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.directory(new File(projectPath));
        processBuilder.start();
    }
}
