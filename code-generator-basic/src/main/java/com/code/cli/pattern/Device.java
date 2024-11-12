package com.code.cli.pattern;

/**
 * packageName com.code.cli.pattern
 *
 * @author Gin
 * @version 1.0.0
 * @title Device
 * @date 2024/11/12 21:30 周二
 * @desreciption TODO
 */
public class Device {
    private String name;

    public Device(String name) {
        this.name = name;
    }

    public void turnOn() {
        System.out.println(name + " 打开设备");
    }

    public void turnOff() {
        System.out.println(name + " 关闭设备");
    }
}
