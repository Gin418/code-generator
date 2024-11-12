package com.code.cli.pattern;

/**
 * packageName com.code.cli.pattern
 *
 * @author Gin
 * @version 1.0.0
 * @title TurnOnCommand
 * @date 2024/11/12 21:35 周二
 * @desreciption TODO
 */
public class TurnOnCommand implements Command {

    private Device device;

    public TurnOnCommand(Device device) {
        this.device = device;
    }

    @Override
    public void execute() {
        device.turnOn();
    }
}
