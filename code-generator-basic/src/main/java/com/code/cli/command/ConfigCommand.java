package com.code.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.code.model.MainTemplateConfig;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.lang.reflect.Field;

/**
 * packageName com.code.cli.command
 *
 * @author Gin
 * @version 1.0.0
 * @title ConfigCommand
 * @date 2024/11/12 22:19 周二
 * @desreciption TODO
 */
@Command(name = "config", description = "查看参数信息", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("查看参数信息");

        // 获取要打印的类
        /*Class<MainTemplateConfig> myClass = MainTemplateConfig.class;
        //获取类的所有字段
        Field[] fields = myClass.getDeclaredFields();*/

        Field[] fields = ReflectUtil.getFields(MainTemplateConfig.class);

        // 遍历所有字段，打印字段名和字段值
        for (Field field : fields) {
            // 打印字段类型
            System.out.println("字段类型：" + field.getType());
            // 打印字段名
            System.out.println("字段名：" + field.getName());
        }
    }
}
