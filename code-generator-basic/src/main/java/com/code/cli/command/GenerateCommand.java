package com.code.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.code.generator.MainGenerator;
import com.code.model.MainTemplateConfig;
import freemarker.template.TemplateException;
import lombok.Data;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * packageName com.code.cli.command
 *
 * @author Gin
 * @version 1.0.0
 * @title GeneratorCommand
 * @date 2024/11/12 22:20 周二
 * @desreciption TODO
 */
@Command(name = "generate", mixinStandardHelpOptions = true, description = "生成代码")
@Data
public class GenerateCommand implements Callable {

    /*
     * 作者信息
     */
    @Option(names = {"-a", "--author"}, description = "作者信息", interactive = true,arity = "0..1", prompt = "请输入作者信息: ", echo = true)
    private String author = "Gin";

    /*
     * 输出信息
     */
    @Option(names = {"-o", "--output"}, description = "输出信息", interactive = true, arity = "0..1", prompt = "请输入输出信息: ", echo = true)
    private String outputText = "sum";

    /*
     * 是否循环
     */
    @Option(names = {"-l", "--loop"}, description = "是否循环", interactive = true, arity = "0..1", prompt = "是否循环: ", echo = true)
    private boolean loop;

    @Override
    public Integer call() {
        MainTemplateConfig templateConfig = new MainTemplateConfig();
        BeanUtil.copyProperties(this, templateConfig);
        System.out.println("配置信息：" + templateConfig);
        try {
            MainGenerator.doGenerate(templateConfig);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
