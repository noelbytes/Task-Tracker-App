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
        // Smaller sizes and no recordStats to reduce CPU/memory on tiny hosts
        CaffeineCache tasksByUser = new CaffeineCache("tasksByUser",
                Caffeine.newBuilder()
                        .initialCapacity(20)
                        .maximumSize(60)
                        .expireAfterWrite(45, TimeUnit.SECONDS)
                        .build());
        CaffeineCache taskById = new CaffeineCache("taskById",
                Caffeine.newBuilder()
                        .initialCapacity(50)
                        .maximumSize(150)
                        .expireAfterWrite(60, TimeUnit.SECONDS)
                        .build());
        CaffeineCache taskStats = new CaffeineCache("taskStats",
                Caffeine.newBuilder()
                        .initialCapacity(10)
                        .maximumSize(30)
                        .expireAfterWrite(20, TimeUnit.SECONDS)
                        .build());
        manager.setCaches(Arrays.asList(tasksByUser, taskById, taskStats));
        return manager;
    }
}
