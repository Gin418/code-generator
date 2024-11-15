package com.code.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

/**
 * packageName com.code.maker.meta
 *
 * @author Gin
 * @version 1.0.0
 * @title MetaManage
 * @date 2024/11/14 22:52 周四
 * @desreciption TODO
 */
public class MetaManager {

    private static volatile Meta meta;

    public static Meta getMetaObject() {
        // 采用双检锁进行并发控制，防止重复初始化
        if (meta == null) {
            synchronized (MetaManager.class) {
                if (meta == null) {
                    meta = initMeta();
                }
            }
        }
        return meta;
    }

    private static Meta initMeta() {
        String metaJson = ResourceUtil.readUtf8Str("meta.json");
        Meta meta = JSONUtil.toBean(metaJson, Meta.class);
        // TODO 校验配置文件，处理默认值
        return meta;
    }

}
