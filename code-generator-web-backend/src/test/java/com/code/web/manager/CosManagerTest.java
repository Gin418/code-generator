package com.code.web.manager;

import com.code.web.common.ErrorCode;
import com.code.web.exception.BusinessException;
import com.qcloud.cos.exception.CosServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=local")
class CosManagerTest {

    @Resource
    private CosManager cosManager;

    @Test
    void deletedObject() {
        cosManager.deletedObject("/generator_make_template/1/THW6nbwb-acm-template-pro.zip");
    }

    @Test
    void deletedObjects() {
        cosManager.deletedObjects(Arrays.asList(
                "generator_make_template/1/qjcbm17T-acm-template-pro.zip",
                "generator_make_template/1/uXiPPyWf-springboot-template.zip"
        ));
    }

    @Test
    void deletedDir() {
        cosManager.deletedDir("/generator_dist/1/");
    }
}