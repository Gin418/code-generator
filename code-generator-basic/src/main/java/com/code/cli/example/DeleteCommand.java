package com.code.cli.example;

import picocli.CommandLine.Command;

/**
 * packageName com.code.cli.example
 *
 * @author Gin
 * @version 1.0.0
 * @title AddCommand
 * @date 2024/11/12 20:47 周二
 * @desreciption TODO
 */
@Command(name = "delete", description = "删除", mixinStandardHelpOptions = true)
public class DeleteCommand implements Runnable{
    @Override
    public void run() {
        System.out.println("执行删除子命令");
    }
}
