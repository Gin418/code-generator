package ${basePackage}.cli;

import ${basePackage}.cli.command.ConfigCommand;
import ${basePackage}.cli.command.GenerateCommand;
import ${basePackage}.cli.command.JsonGenerateCommand;
import ${basePackage}.cli.command.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * packageName ${basePackage}.cli
 *
 * @author ${author}
 * @version ${version}
 * @title CommandExecutor
 * @date ${createTime}
 * @desreciption TODO
 */
@Command(name = "${name}", mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable {

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ListCommand())
                .addSubcommand(new JsonGenerateCommand());
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
