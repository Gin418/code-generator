package com.code.web.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * packageName com.code.web.manager
 *
 * @author <a href="https://github.com/Gin418">Gin</a>
 * @version 1.0.0
 * @title CacheManager
 * @date 2024/12/24 9:52 周二
 * @description 多级缓存
 */
@Component
public class CacheManager {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    Cache<String, Object> cache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    /**
     * @param key
     * @return java.lang.String
     * @throws
     * @title get
     * @date 2024/12/24
     * @description 读缓存
     */
    public Object get(String key) {
        // 从本地缓存中获取
        Object value = cache.getIfPresent(key);
        if (value != null) {
            return value;
        }

        // 从 Redis 中获取
        value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            // 将 Redis 中获取的值放入本地缓存
            cache.put(key, value);
        }
        return value;
    }

    /**
     * @param key
     * @param value
     * @throws
     * @title put
     * @date 2024/12/24
     * @description 写缓存
     */
    public void put(String key, Object value) {
        // 添加或者更新一个缓存元素
        cache.put(key, value);
        redisTemplate.opsForValue().set(key, value, 1, TimeUnit.MINUTES);
    }

    /**
     * @param key
     * @throws
     * @title delete
     * @date 2024/12/24
     * @description 移除缓存
     */
    public void delete(String key) {
        // 移除一个缓存元素
        cache.invalidate(key);
        redisTemplate.delete(key);
    }
}
