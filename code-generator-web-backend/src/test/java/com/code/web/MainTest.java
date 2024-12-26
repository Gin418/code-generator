package com.code.web;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.code.web.manager.CosManager;
import com.code.web.model.entity.Generator;
import com.code.web.service.GeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * packageName com.code.web
 *
 * @author <a href="https://github.com/Gin418">Gin</a>
 * @version 1.0.0
 * @title MainTest
 * @date 2024/12/21 22:30 周六
 * @description
 */
@SpringBootTest(properties = "spring.profiles.active=local")
public class MainTest {

    @Resource
    private CosManager cosManager;

    @Resource
    private GeneratorService generatorService;

    @Test
    void contextLoads() {
        try {
            cosManager.download("/generator_dist/1/springboot-init-meta.json", "C:/Users/Q/IdeaProjects/code-generator/code-generator-web-backend/.temp/1.json");
            String metaJson = FileUtil.readUtf8String("C:/Users/Q/IdeaProjects/code-generator/code-generator-web-backend/.temp/1.json");
            Generator generator = JSONUtil.toBean(metaJson, Generator.class);
            generator.setUserId(1L);
            generatorService.save(generator);
            System.out.println(generator);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void insert() {
        Generator generator = generatorService.getById(8L);
        for (int i = 0; i < 100000; i++) {
            generator.setId(null);
            generatorService.save(generator);
        }
    }

}
