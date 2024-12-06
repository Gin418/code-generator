package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;
import lombok.Data;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.concurrent.Callable;

<#-- 生成选项 -->
<#macro generateOption indent modelInfo>
${indent}@Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}", </#if><#if modelInfo.fieldName??>"--${modelInfo.fieldName}"</#if>}, description = "${modelInfo.description}", interactive = true,arity = "0..1", prompt = "请输入${modelInfo.description}: ", echo = true)
${indent}private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#macro>

<#-- 生成命令调用 -->
<#macro generateCommand indent modelInfo>
${indent}System.out.println("输入${modelInfo.groupName}配置：");
${indent}CommandLine ${modelInfo.groupKey}CommandLine = new CommandLine(${modelInfo.type}Command.class);
${indent}${modelInfo.groupKey}CommandLine.execute(${modelInfo.allArgsStr});
</#macro>

/**
 * packageName ${basePackage}.cli.command
 *
 * @author ${author}
 * @version ${version}
 * @title GenerateCommand
 * @date ${createTime}
 * @desreciption 生成代码
 */
@Command(name = "generate", mixinStandardHelpOptions = true, description = "生成代码")
@Data
public class GenerateCommand implements Callable {
<#list modelConfig.models as modelInfo>

    <#-- 有分组 -->
    <#if modelInfo.groupKey??>
    /*
     * ${modelInfo.groupName}
     */
    static DataModel.${modelInfo.type} ${modelInfo.groupKey} = new DataModel.${modelInfo.type}();

    <#-- 根据分组生成命令类 -->
    @Command(name = "${modelInfo.groupKey}", description = "${modelInfo.description}")
    @Data
    public static class ${modelInfo.type}Command implements Runnable {
        <#list modelInfo.models as subModelInfo>
            <@generateOption indent="        " modelInfo=subModelInfo/>
        </#list>

        @Override
        public void run() {
            <#list modelInfo.models as subModelInfo>
            ${modelInfo.groupKey}.${subModelInfo.fieldName} = ${subModelInfo.fieldName};
            </#list>
        }
    }

    <#else>
        <@generateOption indent="    " modelInfo=modelInfo/>
    </#if>
</#list>

    <#-- 生成调用方法 -->
    @Override
    public Integer call() throws TemplateException, IOException {
        <#-- 根据条件调用子命令 -->
        <#list modelConfig.models as modelInfo>
            <#if modelInfo.groupKey??>
                <#if modelInfo.condition??>
        if (${modelInfo.condition}) {
                    <@generateCommand indent="           " modelInfo=modelInfo/>
        }
                <#else>
                    <@generateCommand indent="        " modelInfo=modelInfo/>
                </#if>
            </#if>
        </#list>
        <#-- 赋值给数据模型对象 -->
        DataModel dataModel = new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        <#list modelConfig.models as modelInfo>
            <#if modelInfo.groupKey??>
        dataModel.${modelInfo.groupKey} = ${modelInfo.groupKey};
            </#if>
        </#list>
        MainGenerator.doGenerate(dataModel);
        return 0;
    }
}
