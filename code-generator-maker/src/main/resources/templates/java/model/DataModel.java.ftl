package ${basePackage}.maker.model;

import lombok.Data;

/**
 * packageName ${basePackage}.model
 *
 * @author ${author}
 * @version 1.0.0
 * @title DataModel
 * @date ${createTime}
 * @desreciption 数据模型
 */
@Data
public class DataModel {
<#list modelConfig.models as modelInfo>

    <#if modelInfo.description??>
    /*
     * ${modelInfo.description}
     */
    </#if>
    private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;

</#list>
}
