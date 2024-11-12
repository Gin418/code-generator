package com.code.cli.pattern;

/**
 * packageName com.code.cli.pattern
 *
 * @author Gin
 * @version 1.0.0
 * @title TurnOffCommand
 * @date 2024/11/12 21:29 周二
 * @desreciption TODO
 */
public class TurnOffCommand implements Command {

    private Device device;

    public TurnOffCommand(Device device) {
        this.device = device;
    }

    @Override
    public void execute() {
        device.turnOff();
    }
}
