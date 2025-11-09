package com.tasktracker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.cache.CacheManager;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class StartupInfo implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger log = LoggerFactory.getLogger(StartupInfo.class);

    @Autowired
    private Environment env;

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String cors = env.getProperty("cors.allowed.origins", "(not set)");
        log.info("Configured CORS allowed origins: {}", cors);

        if (cacheManager != null) {
            String caches = cacheManager.getCacheNames().stream().collect(Collectors.joining(", "));
            log.info("Available caches: {}", caches);
        } else {
            log.info("No CacheManager available at startup");
        }
    }
}

