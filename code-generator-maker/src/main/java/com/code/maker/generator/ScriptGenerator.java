package com.code.maker.generator;

import cn.hutool.core.io.FileUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * packageName com.code.maker.generator
 *
 * @author Gin
 * @version 1.0.0
 * @title ScriptGenerator
 * @date 2024/11/17 0:54 周日
 * @desreciption TODO
 */
public class ScriptGenerator {

    public static void doGenerate(String outputPath, String jarPath) {
        StringBuilder sb = new StringBuilder();
        // Linux 版本
        // #!/bin/bash
        // java -jar target/code-generator-basic-1.0-SNAPSHOT-jar-with-dependencies.jar "$@"
        sb.append("#!/bin/bash\n");
        sb.append("java -jar ").append(jarPath).append(" \"$@\"");
        FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8), outputPath);
        // 添加可执行权限
        Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
        // windows 系统不支持 PosixFilePermissions所以需要忽略异常
        try {
            Files.setPosixFilePermissions(Paths.get(outputPath), permissions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Windows 版本
        // @echo off
        // java -jar target\code-generator-basic-1.0-SNAPSHOT-jar-with-dependencies.jar %*
        sb = new StringBuilder();
        sb.append("@echo off\n");
        sb.append("java -jar ").append(jarPath).append(" %*");
        FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8), outputPath + ".bat");
    }
}
