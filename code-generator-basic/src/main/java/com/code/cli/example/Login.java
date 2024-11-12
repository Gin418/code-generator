package com.code.cli.example;

import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * packageName com.code.cli.example
 *
 * @author Gin
 * @version 1.0.0
 * @title Login
 * @date 2024/11/12 11:34 周二
 * @desreciption TODO
 */
public class Login implements Callable<Integer> {
    @Option(names = {"-u", "--user"}, description = "User name")
    String user;

    @Option(names = {"-p", "--password"}, arity = "0..1", description = "Passphrase", interactive = true)
    String password;

    @Option(names = {"-cp", "--checkPassword"}, arity = "0..1", description = "Check Password", interactive = true)
    String checkPassword;

    public Integer call() throws Exception {
        System.out.println("password = " + password);
        System.out.println("checkPassword = " + checkPassword);
        return 0;
    }

    public static void main(String[] args) {
        boolean flag = false;
        for (String arg : args) {
            if(arg.equals("-cp")) {
                flag = true;
            }
        }
        if (!flag) {
            args = Arrays.copyOf(args, args.length + 1);
            args[args.length - 1] = "-cp";
        }
        new CommandLine(new Login()).execute(args);
    }
}

