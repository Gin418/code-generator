package com.code.web.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.code.web.manager.CosManager;
import com.code.web.mapper.GeneratorMapper;
import com.code.web.model.entity.Generator;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName com.code.web.job
 *
 * @author <a href="https://github.com/Gin418">Gin</a>
 * @version 1.0.0
 * @title ClearCosJobHandler
 * @date 2024/12/26 21:12 周四
 * @description 定时清理 cos 文件任务
 */
@Component
@Slf4j
public class ClearCosJobHandler {

    @Resource
    private CosManager cosManager;

    @Resource
    private GeneratorMapper generatorMapper;

    /**
     * @throws
     * @title clearCosJobHandler
     * @date 2024/12/26
     * @description 每天执行
     */
    @XxlJob("clearCosJobHandler")
    public void clearCosJobHandler() throws Exception {
        log.info("clearCosJobHandler start");
        // 1. 用户上传的模板制作文件（generator_make_template）
        cosManager.deletedDir("generator_make_template/");

        // 2. 已删除的代码生成器对应的产物包文件（generator_dist）
        List<Generator> generatorList = generatorMapper.listDeleteGenerator();
        List<String> keyList = generatorList.stream().map(Generator::getDistPath)
                .filter(StrUtil::isNotBlank)
                // 移除 '/' 前缀
                .map(distPath -> distPath.substring(1))
                .collect(Collectors.toList());

        cosManager.deletedObjects(keyList);
        log.info("clearCosJobHandler end");
    }
}
