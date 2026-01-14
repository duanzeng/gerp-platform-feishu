package com.gerp.platform.feishu.server.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/19 18:04
 */

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, String> tokenCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(23, TimeUnit.HOURS)  // 写入24小时后过期
                .maximumSize(10000)  // 最大缓存1000个条目
                .build();
    }
}
