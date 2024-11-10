import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName PACKAGE_NAME
 *
 * @author Gin
 * @version 1.0.0
 * @title FreeMarkerTest
 * @date 2024/11/7 22:14 周四
 * @desreciption TODO
 */
public class FreeMarkerTest {

    @Test
    public void test() throws IOException, TemplateException {
        // new Configuration 对象，参数为 freemarker 版本
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);

        // 模板文件所在位置
        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));

        // 模板文件使用的字符集
        cfg.setDefaultEncoding("UTF-8");

        // 创建模板对象，加载指定模板
        Template template = cfg.getTemplate("myweb.html.ftl");

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("currentYear", 2024);
        List<Map<String, Object>> menuItems = new ArrayList<>();
        Map<String, Object> menuItem1 = new HashMap<>();
        menuItem1.put("url", "https://github.com/Gin418");
        menuItem1.put("label", "Git仓库");
        menuItems.add(menuItem1);
        Map<String, Object> menuItem2 = new HashMap<>();
        menuItem2.put("url", "https://blog.csdn.net/weixin_51907774");
        menuItem2.put("label", "博客");
        menuItems.add(menuItem2);
        dataModel.put("menuItems", menuItems);

        String property = System.getProperty("user.dir");// 获取当前项目路径
        File parentFile = new File(property).getParentFile();// 获取父级目录
        System.out.println(property);
        String inputPath = new File(parentFile, "code-generator-basic/myweb.html").getAbsolutePath();// 父级目录和当前项目目录拼接的绝对路径(C:\Users\Q\IdeaProjects\code-generator\code-generator-basic\myweb.html)
        System.out.println(inputPath);
        //Writer out = new FileWriter(inputPath);
        Writer out = new FileWriter("myweb.html");
        template.process(dataModel, out);

        // 生成文件后别忘了关闭
        out.close();

    }

}
