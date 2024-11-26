package ${basePackage}.cli.command;

import cn.hutool.core.util.ReflectUtil;
import ${basePackage}.model.DataModel;
import picocli.CommandLine.Command;

import java.lang.reflect.Field;

/**
 * packageName ${basePackage}.cli.command
 *
 * @author ${author}
 * @version ${version}
 * @title ConfigCommand
 * @date ${createTime}
 * @desreciption 查看参数信息
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

        Field[] fields = ReflectUtil.getFields(DataModel.class);

        // 遍历所有字段，打印字段名和字段值
        for (Field field : fields) {
            // 打印字段类型
            System.out.println("字段类型：" + field.getType());
            // 打印字段名
            System.out.println("字段名：" + field.getName());
        }
    }
}
