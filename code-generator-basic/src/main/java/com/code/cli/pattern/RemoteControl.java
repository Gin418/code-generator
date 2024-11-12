package com.code.cli.pattern;

/**
 * packageName com.code.cli.pattern
 *
 * @author Gin
 * @version 1.0.0
 * @title RemoteControl
 * @date 2024/11/12 21:40 周二
 * @desreciption TODO
 */
public class RemoteControl {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void pressButton() {
        command.execute();
    }
}
