package com.code.cli.example;

import picocli.CommandLine;
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
@Command(name = "add", description = "增加", mixinStandardHelpOptions = true)
public class AddCommand implements Runnable{
    @Override
    public void run() {
        System.out.println("执行增加子命令");
    }
}
