package ${basePackage}.model;

import lombok.Data;

<#macro generateModel indent modelInfo>
${indent}/*
${indent} * ${modelInfo.description}
${indent} */
${indent}public ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#macro>

/**
 * packageName ${basePackage}.model
 *
 * @author ${author}
 * @version ${version}
 * @title DataModel
 * @date ${createTime}
 * @desreciption 数据模型
 */
@Data
public class DataModel {
<#list modelConfig.models as modelInfo>

    <#-- 有分组 -->
    <#if modelInfo.groupKey??>
    /*
     * ${modelInfo.groupName}
     */
    public ${modelInfo.type} ${modelInfo.groupKey} = new ${modelInfo.type}();

    /*
     * ${modelInfo.description}
     */
    @Data
    public static class ${modelInfo.type} {
        <#list modelInfo.models as groupModelInfo>
            <@generateModel indent="        " modelInfo=groupModelInfo/>
        </#list>
    }
    <#else>
        <#-- 无分组 -->
        <@generateModel indent="    " modelInfo=modelInfo/>
    </#if>
</#list>
}
