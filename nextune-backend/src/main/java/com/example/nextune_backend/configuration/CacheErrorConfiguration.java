package com.example.nextune_backend.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CacheErrorConfiguration  {

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception,
                                            Cache cache, Object key) {
                log.error("Cache GET error for key {} in cache {}",
                        key, cache.getName(), exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception,
                                            Cache cache, Object key, Object value) {
                log.error("Cache PUT error", exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception,
                                              Cache cache, Object key) {
                log.error("Cache EVICT error", exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception,
                                              Cache cache) {
                log.error("Cache CLEAR error", exception);
            }
        };
    }
}

