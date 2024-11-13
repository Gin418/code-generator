package com.code.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.util.List;

/**
 * packageName com.code.cli.command
 *
 * @author Gin
 * @version 1.0.0
 * @title ListCommand
 * @date 2024/11/12 22:20 周二
 * @desreciption TODO
 */
@Command(name = "list", mixinStandardHelpOptions = true, description = "查看文件列表")
public class ListCommand implements Runnable {
    @Override
    public void run() {
        // 获取整个项目的根路径
        String projectPath = System.getProperty("user.dir");
        System.out.println("当前项目路径：" + projectPath);
        // 输入路径
        String inputPath = new File(projectPath, "code-generator-demo-projects/acm-template").getAbsolutePath();
        List<File> files = FileUtil.loopFiles(inputPath);
        for (File file : files) {
            System.out.println(file);
        }
    }

}
