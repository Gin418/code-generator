package ${basePackage}.generator;

import cn.hutool.core.io.FileUtil;

/**
 * packageName ${basePackage}.generator
 *
 * @author ${author}
 * @version ${version}
 * @title StaticGenerator
 * @date ${createTime}
 * @desreciption 静态文件生成器
 */
public class StaticGenerator {

    /*
     * @title copyFileByHutool
     * @date ${createTime}
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
