package com.code.maker.cli;

import com.code.maker.cli.command.ConfigCommand;
import com.code.maker.cli.command.GenerateCommand;
import com.code.maker.cli.command.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * packageName com.code.cli
 *
 * @author Gin
 * @version 1.0.0
 * @title CommandExecutor
 * @date 2024/11/12 22:20 周二
 * @desreciption TODO
 */
@Command(name = "code", mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable {

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ListCommand());

    }

    @Override
    public void run() {
        System.out.println("请输入具体命令，或者使用 --help 参数查看帮助");
    }

    /**
     * 执行命令
     *
     * @param args 命令参数
     * @return Integer 命令执行结果
     */
    public Integer doExecute(String[] args) {
        return commandLine.execute(args);
    }

}
