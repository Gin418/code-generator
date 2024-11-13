package com.code;

import com.code.cli.CommandExecutor;

/**
 * packageName com.code
 * @version 1.0.0
 *
 * @author Gin
 * @title Main
 * @date 2024/11/7 19:58 周四
 * @desreciption 代码生成器项目的全局调用入口
 */
public class Main {

    public static void main(String[] args) {
//        args = new String[]{"generate", "-l", "-a", "-o"};
//        args = new String[]{"config"};
//        args = new String[]{"list"};
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}

