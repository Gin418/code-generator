package com.code.maker.generator;

import java.io.*;

/**
 * packageName com.code.maker.generator
 *
 * @author Gin
 * @version 1.0.0
 * @title JarGenerator
 * @date 2024/11/16 22:59 周六
 * @desreciption TODO
 */
public class JarGenerator {

    public static void doGenerate(String projectDir) throws IOException, InterruptedException {
        // 清理之前的构建并打包
        // 不同的操作系统，执行的命令不同
        String winMaVenCommand = "mvn.cmd clean package -DskipTests=true";
        String otherMaVenCommand = "echo $MAVEN_HOME && source ~/.bashrc && echo $PATH && mvn clean package -DskipTests=true";
        String mavenCommand = otherMaVenCommand;

        // 将命令按空格分割，并执行
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", mavenCommand);
        // 设置工作目录
        processBuilder.directory(new File(projectDir));

        System.out.println("开始打包……\n" +
                "执行命令：" + mavenCommand);
        Process process = processBuilder.start();

        // 读取命令的输出
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println("打包结束！");

        int exitCode = process.waitFor();
        System.out.println("命令执行结果，退出码: " + exitCode);
    }
}
