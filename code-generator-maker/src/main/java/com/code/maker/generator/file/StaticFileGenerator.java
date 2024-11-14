package com.code.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");// 获取当前项目根路径
        System.out.println(projectPath);
        File parentFile = new File(projectPath).getParentFile();
        // 输入文件路径
        String inputPath =new File(parentFile, "code-generator-demo-projects/acm-template").getAbsolutePath();
        System.out.println(inputPath);
        // 输出文件路径
        String outputPath = projectPath;

        // 拷贝文件
        copyFilesByRecursive(inputPath, outputPath);

    }

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

    /**
     * 递归拷贝文件（递归实现，会将输入目录完整拷贝到输出目录下）
     * @param inputPath
     * @param outputPath
     */
    public static void copyFilesByRecursive(String inputPath, String outputPath) {
        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);
        try {
            copyFileByRecursive(inputFile, outputFile);
        } catch (Exception e) {
            System.err.println("文件复制失败");
            e.printStackTrace();
        }
    }

    /**
     * 文件 A => 目录 B，则文件 A 放在目录 B 下
     * 文件 A => 文件 B，则文件 A 覆盖文件 B
     * 目录 A => 目录 B，则目录 A 放在目录 B 下
     *
     * 核心思路：先创建目录，然后遍历目录内的文件，依次复制
     * @param inputFile
     * @param outputFile
     * @throws IOException
     */
    private static void copyFileByRecursive(File inputFile, File outputFile) throws IOException {
        // 区分是文件还是目录
        if (inputFile.isDirectory()) {
            System.out.println(inputFile.getName());
            File destOutputFile = new File(outputFile, inputFile.getName());
            // 如果是目录，首先创建目标目录
            if (!destOutputFile.exists()) {
                destOutputFile.mkdirs();
            }
            // 获取目录下的所有文件和子目录
            File[] files = inputFile.listFiles();
            // 无子文件，直接结束
            if (ArrayUtil.isEmpty(files)) {
                return;
            }
            for (File file : files) {
                // 递归拷贝下一层文件
                copyFileByRecursive(file, destOutputFile);
            }
        } else {
            // 是文件，直接复制到目标目录下
            Path destPath = outputFile.toPath().resolve(inputFile.getName());
            Files.copy(inputFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }


}
