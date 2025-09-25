package com.example.nextune_backend.configuration;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override public void handleCacheGetError(RuntimeException e, Cache cache, Object key) { /* log thôi, KHÔNG throw */ }
            @Override public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) { /* log */ }
            @Override public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) { /* log */ }
            @Override public void handleCacheClearError(RuntimeException e, Cache cache) { /* log */ }
        };
    }
}
