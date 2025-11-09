package com.tasktracker.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        // Align TTLs & sizes with previous Ehcache tuning
        CaffeineCache tasksByUser = new CaffeineCache("tasksByUser",
                Caffeine.newBuilder()
                        .initialCapacity(50)
                        .maximumSize(140)
                        .expireAfterWrite(60, TimeUnit.SECONDS)
                        .recordStats()
                        .build());
        CaffeineCache taskById = new CaffeineCache("taskById",
                Caffeine.newBuilder()
                        .initialCapacity(100)
                        .maximumSize(300)
                        .expireAfterWrite(120, TimeUnit.SECONDS)
                        .recordStats()
                        .build());
        CaffeineCache taskStats = new CaffeineCache("taskStats",
                Caffeine.newBuilder()
                        .initialCapacity(30)
                        .maximumSize(80)
                        .expireAfterWrite(30, TimeUnit.SECONDS)
                        .recordStats()
                        .build());
        manager.setCaches(Arrays.asList(tasksByUser, taskById, taskStats));
        return manager;
    }
}
