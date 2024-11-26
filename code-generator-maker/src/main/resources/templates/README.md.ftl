# ${name}

> ${description}
>
> 作者：${author}
>
> 源码地址: [定制化代码生成项目](https://github.com/Gin418/code-generator)
>
> ${name} 旨在简化模板生成过程,提供快速、高效的代码生成方式，使开发人员能够更专注于业务逻辑和功能开发，用户通过命令行界面交互 通过几个参数就能生成定制化的代码。

## 使用说明

执行项目根目录下的脚本文件：

```
generator <命令> <参数>
```

示例命令：

```
generator generate <#list modelConfig.models as modelInfo><#if modelInfo.groupKey??><#else><#if modelInfo.abbr??>-${modelInfo.abbr} </#if></#if></#list>
```

## 参数说明

<#list modelConfig.models as modelInfo>
<#if modelInfo.groupKey??>
### ${modelInfo?index + 1}）${modelInfo.groupName}
<#list modelInfo.models as subModelInfo>
#### ${subModelInfo?index + 1}) ${subModelInfo.fieldName}

类型: ${subModelInfo.type}

描述: ${subModelInfo.description}

默认值: ${subModelInfo.defaultValue?c}

命令缩写: ${subModelInfo.abbr}
</#list>
<#else>
### ${modelInfo?index + 1}）${modelInfo.fieldName}

类型：${modelInfo.type}

描述：${modelInfo.description}

默认值：${modelInfo.defaultValue?c}

<#if modelInfo.abbr??>
缩写： -${modelInfo.abbr}
</#if>
</#if>

</#list>
---
查看配置命令说明:

输出generate参数类型等信息

```bash
./generator config
```

查看文件目录说明:

输出文件目录信息

```bash
./generator list
```