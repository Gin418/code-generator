package com.code.web.converter;

import com.code.web.model.dto.generator.GeneratorAddRequest;
import com.code.web.model.dto.generator.GeneratorEditRequest;
import com.code.web.model.dto.generator.GeneratorQueryRequest;
import com.code.web.model.dto.generator.GeneratorUpdateRequest;
import com.code.web.model.entity.Generator;
import com.code.web.model.vo.GeneratorVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * packageName com.code.web.converter
 *
 * @author <a href="https://github.com/Gin418">Gin</a>
 * @version 1.0.0
 * @title GeneratorConvert
 * @date 2024/12/9 16:36 周一
 * @description 代码生成器转换
 */
@Mapper(componentModel = "spring")
public interface GeneratorConvert {

    //GeneratorConvert INSTANCE = Mappers.getMapper(GeneratorConvert.class);

    @Mappings({
            @Mapping(target = "tags", expression = "java(cn.hutool.json.JSONUtil.toList(generator.getTags(), String.class))"),
            @Mapping(target = "fileConfig", expression = "java(cn.hutool.json.JSONUtil.toBean(generator.getFileConfig(), com.code.maker.meta.Meta.FileConfig.class))"),
            @Mapping(target = "modelConfig", expression = "java(cn.hutool.json.JSONUtil.toBean(generator.getModelConfig(), com.code.maker.meta.Meta.ModelConfig.class))"),
        }
    )
    GeneratorVO GeneratorVOToGenerator(Generator generator);

    @Mappings({
            @Mapping(target = "tags", expression = "java(cn.hutool.json.JSONUtil.toJsonStr(generatorVO.getTags()))"),
            @Mapping(target = "fileConfig", expression = "java(cn.hutool.json.JSONUtil.toJsonStr(generatorVO.getFileConfig()))"),
            @Mapping(target = "modelConfig", expression = "java(cn.hutool.json.JSONUtil.toJsonStr(generatorVO.getModelConfig()))"),
        }
    )
    Generator GeneratorToGeneratorVO(GeneratorVO generatorVO);

    @Mappings({
            @Mapping(target = "tags", expression = "java(cn.hutool.json.JSONUtil.toJsonStr(generatorAddRequest.getTags()))"),
            @Mapping(target = "fileConfig", expression = "java(cn.hutool.json.JSONUtil.toJsonStr(generatorAddRequest.getFileConfig()))"),
            @Mapping(target = "modelConfig", expression = "java(cn.hutool.json.JSONUtil.toJsonStr(generatorAddRequest.getModelConfig()))"),
        }
    )
    Generator GeneratorAddRequestToGenerator(GeneratorAddRequest generatorAddRequest);

    @Mappings({
            @Mapping(target = "tags", expression = "java(cn.hutool.json.JSONUtil.toJsonStr(generatorEditRequest.getTags()))"),
            @Mapping(target = "fileConfig", expression = "java(cn.hutool.json.JSONUtil.toJsonStr(generatorEditRequest.getFileConfig()))"),
            @Mapping(target = "modelConfig", expression = "java(cn.hutool.json.JSONUtil.toJsonStr(generatorEditRequest.getModelConfig()))"),
        }
    )
    Generator GeneratorEditRequestToGenerator(GeneratorEditRequest generatorEditRequest);

    @Mappings({
            @Mapping(target = "tags", expression = "java(cn.hutool.json.JSONUtil.toJsonStr(generatorQueryRequest.getTags()))"),
        }
    )
    Generator GeneratorQueryRequestToGenerator(GeneratorQueryRequest generatorQueryRequest);

    @Mappings({
            @Mapping(target = "tags", expression = "java(cn.hutool.json.JSONUtil.toJsonStr(generatorUpdateRequest.getTags()))"),
            @Mapping(target = "fileConfig", expression = "java(cn.hutool.json.JSONUtil.toJsonStr(generatorUpdateRequest.getFileConfig()))"),
            @Mapping(target = "modelConfig", expression = "java(cn.hutool.json.JSONUtil.toJsonStr(generatorUpdateRequest.getModelConfig()))"),
        }
    )
    Generator GeneratorUpdateRequestToGenerator(GeneratorUpdateRequest generatorUpdateRequest);
}
