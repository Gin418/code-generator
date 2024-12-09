package com.code.web.mapstruct;

import com.code.web.model.dto.generator.GeneratorAddRequest;
import com.code.web.model.dto.generator.GeneratorEditRequest;
import com.code.web.model.dto.generator.GeneratorQueryRequest;
import com.code.web.model.dto.generator.GeneratorUpdateRequest;
import com.code.web.model.entity.Generator;
import com.code.web.model.vo.GeneratorVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.Collections;

/**
 * packageName com.code.web.mapstruct
 *
 * @author <a href="https://github.com/Gin418">Gin</a>
 * @version 1.0.0
 * @title GeneratorConvert
 * @date 2024/12/9 16:36 周一
 * @description 代码生成器转换
 */
@Mapper
public interface GeneratorConvert {
    GeneratorConvert INSTANCE = Mappers.getMapper(GeneratorConvert.class);

    GeneratorVO GeneratorVOToGenerator(Generator generator);

    Generator GeneratorAddRequestToGenerator(GeneratorAddRequest generatorAddRequest);

    Generator GeneratorEditRequestToGenerator(GeneratorEditRequest generatorEditRequest);

    Generator GeneratorQueryRequestToGenerator(GeneratorQueryRequest generatorQueryRequest);

    Generator GeneratorUpdateRequestToGenerator(GeneratorUpdateRequest generatorUpdateRequest);
}
