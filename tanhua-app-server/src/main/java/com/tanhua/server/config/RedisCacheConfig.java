package com.tanhua.server.config;

import com.google.common.collect.ImmutableMap;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: leah_ana
 * @Date: 2022/4/15 14:43
 */

//@Configuration
//public class RedisCacheConfig {
// //设置失效时间
//
//    @Bean
//    public CacheManager cacheManager(RedisTemplate<String, Object> template) {
//
//        // 基本配置
//        RedisCacheConfiguration defaultCacheConfiguration =
//                RedisCacheConfiguration
//                        .defaultCacheConfig()
//                        // 设置key为String
//                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getStringSerializer()))
//                        // 设置value 为自动转Json的Object
//                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getValueSerializer()))
//                        // 不缓存null
//                        .disableCachingNullValues()
//                        // 缓存数据保存1小时
//                        .entryTtl(Duration.ofHours(1));
//
//        // 够着一个redis缓存管理器
//
//        return RedisCacheManager.RedisCacheManagerBuilder
//                // Redis 连接工厂
//                .fromConnectionFactory(Objects.requireNonNull(template.getConnectionFactory()))
//                // 缓存配置
//                .cacheDefaults(defaultCacheConfiguration)
//                // 配置同步修改或删除 put/evict
//                .transactionAware()
//                .build();
//    }
//}
