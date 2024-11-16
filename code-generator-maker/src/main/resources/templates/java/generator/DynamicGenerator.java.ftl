package ${basePackage}.generator;

import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * packageName com.code.generator
 *
 * @author ${author}
 * @version ${version}
 * @title DynamicGenerator
 * @date ${createTime}
 * @desreciption 动态文件生成器
 */
public class DynamicGenerator {

    /*
     * @title doGenerator
     * @date ${createTime}
     * @param String inputPath 模板文件输入路径
     * @param String outputPath 输出路径
     * @param Object model 数据模型
     * @return {@link void}
     * @throws IOException
     * @throws TemplateException
     * @description 生成文件
     */
    public static void doGenerator(String inputPath, String outputPath, Object model) throws IOException, TemplateException {
        // new Configuration 对象，参数为 freemarker 版本
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);

        // 模板文件所在位置
        File parentFile = new File(inputPath).getParentFile();
        cfg.setDirectoryForTemplateLoading(parentFile);

        // 模板文件使用的字符集
        cfg.setDefaultEncoding("UTF-8");
        cfg.setNumberFormat("0.######");

        // 创建模板对象，加载指定模板
        String templateName = new File(inputPath).getName();
        Template template = cfg.getTemplate(templateName);

        // 如果文件不存在，则创建
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }

        //生成
        Writer out = new FileWriter(outputPath);
        template.process(model, out);

        // 生成文件后别忘了关闭
        out.close();
    }
}
