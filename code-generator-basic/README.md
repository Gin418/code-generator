# 基于命令行的简单代码生成器

---
[toc]

> ACM 示例模板生成器
> 
> 基于命令行的简单代码生成器，可以快速生成 ACM 代码模板。


## 快速开始
执行项目根目录下的脚本文件：

```text
generator <命令> <参数>
```
示例：

```bash
generator generate -l -a -o
```

## 参数说明

- generate：生成代码模板
  - 'l' ：是否循环输入，默认为 false
  - 'o'： 输出结果，默认为 sum =
  - ’a': 作者信息，默认为 Gin
- list： 打印目标项目列表
- help： 打印帮助信息
- config: 打印数据模型参数信息
