<!DOCTYPE html>
<html>
<head>
    <title>Xx官网</title>
</head>
<body>
<h1>欢迎来到Xx官网</h1>
<ul>
    <#-- 循环渲染导航条 -->
    <#list menuItems as item>
        <li><a href="${item.url}">${item.label}</a></li>
    </#list>
    <#-- 判断 user 是否为空 -->
    <#if user??>
    <#-- 判断 user 是否为"Xx" -->
        <#if user == "Xx">
        我是Xx
        <#else>
        我是${user}
        </#if>
    <#else>
        我是空
    </#if>
    <#macro int long>
        int = ${long}
    </#macro>
    <@int long = 100/>
    <@int long = 200/>
    表达式：${100 + money}
    ${user!"用户为空"}

</ul>
<#-- 底部版权信息（注释部分，不会被输出）-->
<footer>
    ${currentYear} Xx官网. All rights reserved.
</footer>
</body>
</html>
