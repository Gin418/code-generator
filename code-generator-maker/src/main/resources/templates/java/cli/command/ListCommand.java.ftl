package ${basePackage}.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine.Command;

import java.io.File;
import java.util.List;

/**
 * packageName ${basePackage}.cli.command
 *
 * @author ${author}
 * @version ${version}
 * @title ListCommand
 * @date ${createTime}
 * @desreciption 查看文件列表
 */
@Command(name = "list", mixinStandardHelpOptions = true, description = "查看文件列表")
public class ListCommand implements Runnable {
    @Override
    public void run() {
        // 输入路径
        String inputPath = "${fileConfig.inputRootPath}";
        List<File> files = FileUtil.loopFiles(inputPath);
        for (File file : files) {
            System.out.println(file);
        }
    }

}
