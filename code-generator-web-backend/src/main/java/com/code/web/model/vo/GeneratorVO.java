package com.code.web.model.vo;

import cn.hutool.json.JSONUtil;
import com.code.web.meta.Meta;
import com.code.web.model.entity.Generator;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 代码生成器视图
 */
@Data
public class GeneratorVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 基础包名
     */
    private String basePackage;

    /**
     * 是否开启git版本控制
     */
    private Boolean isGit;

    /**
     * 版本号
     */
    private String version;

    /**
     * 作者
     */
    private String author;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 图片
     */
    private String picture;

    /**
     * 文件配置（json 字符串）
     */
    private Meta.FileConfig fileConfig;

    /**
     * 模型配置（json 字符串）
     */
    private Meta.ModelConfig modelConfig;

    /**
     * 代码生成器产物路径
     */
    private String distPath;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Data createTime;

    /**
     * 更新时间
     */
    private Data updateTime;

    /**
     * 创建人信息
     */
    private UserVO user;

    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param generatorVO
     * @return
     */
    public static Generator voToObj(GeneratorVO generatorVO) {
        if (generatorVO == null) {
            return null;
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorVO, generator);
        List<String> tagList = generatorVO.getTags();
        generator.setTags(JSONUtil.toJsonStr(tagList));
        Meta.FileConfig fileConfig = generatorVO.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorVO.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        return generator;
    }

    /**
     * 对象转包装类
     *
     * @param generator
     * @return
     */
    public static GeneratorVO objToVo(Generator generator) {
        if (generator == null) {
            return null;
        }
        GeneratorVO generatorVO = new GeneratorVO();
        BeanUtils.copyProperties(generator, generatorVO);
        generatorVO.setTags(JSONUtil.toList(generator.getTags(), String.class));
        generatorVO.setFileConfig(JSONUtil.toBean(generator.getFileConfig(), Meta.FileConfig.class));
        generatorVO.setModelConfig(JSONUtil.toBean(generator.getModelConfig(), Meta.ModelConfig.class));
        return generatorVO;
    }
}
