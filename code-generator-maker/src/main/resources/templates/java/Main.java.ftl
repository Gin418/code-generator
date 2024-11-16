package ${basePackage};

import ${basePackage}.cli.CommandExecutor;

/**
 * packageName ${basePackage}
 * @version ${version}
 *
 * @author ${author}
 * @title Main
 * @date ${createTime}
 * @desreciption 代码生成器项目的全局调用入口
 */
public class Main {

    public static void main(String[] args) {
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}

