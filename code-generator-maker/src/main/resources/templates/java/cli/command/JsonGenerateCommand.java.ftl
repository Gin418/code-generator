package ${basePackage}.cli.command;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;
import lombok.Data;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.concurrent.Callable;


/**
 * packageName ${basePackage}.cli.command
 *
 * @author ${author}
 * @version ${version}
 * @title JsonGenerateCommand
 * @date ${createTime}
 * @desreciption 读取 JSON 文件生成代码
 */
@Command(name = "json-generate", mixinStandardHelpOptions = true, description = "读取 JSON 文件生成代码")
@Data
public class JsonGenerateCommand implements Callable {

    @Option(names = {"-f", "--file"}, description = "json 文件路径", interactive = true,arity = "0..1", prompt = "请输入 Json 文件路径: ", echo = true)
    private String filePath;

    @Override
    public Integer call() throws TemplateException, IOException {
        //  读取 json 文件，转换为数据模型
        String jsonStr = FileUtil.readUtf8String(filePath);
        DataModel dataModel = JSONUtil.toBean(jsonStr, DataModel.class);
        MainGenerator.doGenerate(dataModel);
        return 0;
    }
}
