package com.tasktracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import javax.cache.CacheManager;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class CacheConfig {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    private final Environment env;
    private final ResourceLoader resourceLoader;

    public CacheConfig(Environment env, ResourceLoader resourceLoader) {
        this.env = env;
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public CacheManager jCacheManager() {
        try {
            CachingProvider provider = Caching.getCachingProvider();

            // Prefer explicit Spring property if provided (e.g., from application-prod.properties)
            String location = env.getProperty("spring.cache.jcache.config");
            URL resourceUrl;
            if (location != null && !location.isBlank()) {
                Resource resource = resourceLoader.getResource(location);
                if (!resource.exists()) {
                    throw new IllegalStateException("Configured JCache config not found: " + location);
                }
                resourceUrl = resource.getURL();
            } else {
                // Fallback by profile: prod -> ehcache-prod.xml (heap-only), else default ehcache.xml
                String path = env.acceptsProfiles(Profiles.of("prod")) ? "/ehcache-prod.xml" : "/ehcache.xml";
                resourceUrl = getClass().getResource(path);
                if (resourceUrl == null) {
                    throw new IllegalStateException(path.substring(1) + " not found on classpath");
                }
            }

            URI configUri = resourceUrl.toURI();
            log.info("Initializing JCache CacheManager using config: {}", configUri);

            CacheManager manager = provider.getCacheManager(configUri, getClass().getClassLoader());

            // Collect and log cache names (INFO) for visibility in prod logs
            try {
                List<String> cacheNames = new ArrayList<>();
                manager.getCacheNames().forEach(cacheNames::add);
                log.info("JCache provider: {} | caches: {}", provider.getClass().getName(), cacheNames);
            } catch (Throwable t) {
                // Be defensive: don't fail app startup on logging issues
                log.warn("Unable to enumerate caches from CacheManager for logging", t);
            }

            return manager;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create JCache CacheManager", e);
        }
    }

    @Bean
    public org.springframework.cache.CacheManager cacheManager(CacheManager jCacheManager) {
        return new JCacheCacheManager(jCacheManager);
    }
}
