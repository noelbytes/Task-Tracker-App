package com.tasktracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.jcache.JCacheCacheManager;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import javax.cache.CacheManager;
import java.net.URI;
import java.net.URL;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager jCacheManager() {
        try {
            // Use the JSR-107 caching provider available on the classpath
            CachingProvider provider = Caching.getCachingProvider();
            URL resource = getClass().getResource("/ehcache.xml");
            if (resource == null) {
                throw new IllegalStateException("ehcache.xml not found on classpath");
            }
            URI configUri = resource.toURI();
            return provider.getCacheManager(configUri, getClass().getClassLoader());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create JCache CacheManager", e);
        }
    }

    @Bean
    public org.springframework.cache.CacheManager cacheManager(CacheManager jCacheManager) {
        return new JCacheCacheManager(jCacheManager);
    }
}
