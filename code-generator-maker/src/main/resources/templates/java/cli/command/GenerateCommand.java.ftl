package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
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
 * @title GeneratorCommand
 * @date ${createTime}
 * @desreciption TODO
 */
@Command(name = "generate", mixinStandardHelpOptions = true, description = "生成代码")
@Data
public class GenerateCommand implements Callable {
<#list modelConfig.models as modelInfo>

    <#if modelInfo.description??>
    /*
     * ${modelInfo.description}
     */
    </#if>
    @Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}"</#if>, <#if modelInfo.fieldName??>"--${modelInfo.fieldName}"</#if>}, description = "${modelInfo.description}", interactive = true,arity = "0..1", prompt = "请输入${modelInfo.description}: ", echo = true)
    private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#list>

    @Override
    public Integer call() throws TemplateException, IOException {
        DataModel templateConfig = new DataModel();
        BeanUtil.copyProperties(this, templateConfig);
        MainGenerator.doGenerate(templateConfig);
        return 0;
    }
}
