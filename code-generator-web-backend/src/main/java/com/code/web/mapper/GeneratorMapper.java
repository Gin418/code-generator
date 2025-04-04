package com.code.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.code.web.model.entity.Generator;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * packageName com.code.web.mapper
 *
 * @author <a href="https://github.com/Gin418">Gin</a>
 * @version 1.0.0
 * @title GeneratorMapper
 * @date 2024/12/8 16:07 周日
 * @description 代码生成器数据库操作
 */
public interface GeneratorMapper extends BaseMapper<Generator> {

    @Select("select id, distPath from generator where isDelete = 1")
    List<Generator> listDeleteGenerator();
}
