package com.code.cli.example;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * packageName com.code.cli.example
 *
 * @author Gin
 * @version 1.0.0
 * @title SubCommandExample
 * @date 2024/11/12 20:44 周二
 * @desreciption TODO
 */
@Command(name = "main", subcommands = {
        AddCommand.class,
        DeleteCommand.class
}, mixinStandardHelpOptions = true)
public class SubCommandExample implements Runnable {

    @Override
    public void run() {
        System.out.println("执行主命令");
    }
    public static void main(String[] args) {
        new CommandLine(new SubCommandExample()).execute(args);
    }
}
