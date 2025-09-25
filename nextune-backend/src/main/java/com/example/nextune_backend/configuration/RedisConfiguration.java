package com.example.nextune_backend.configuration;

import com.example.nextune_backend.dto.response.AlbumResponse;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfiguration {

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.findAndRegisterModules();
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        om.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        // KHÔNG bật default typing -> JSON sạch, không wrapper
        return om;
    }

    @Bean
    public RedisCacheConfiguration baseRedisCacheConfiguration(ObjectMapper om) {
        // Fallback generic cho cache chưa định kiểu (vẫn OK)
        var generic = new GenericJackson2JsonRedisSerializer(om);

        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(generic))
                .disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(10));
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory cf,
                                     RedisCacheConfiguration base,
                                     ObjectMapper om) {

        Map<String, RedisCacheConfiguration> perCache = new HashMap<>();

        // ---- track: TrackResponse (đối tượng đơn) ----
        JavaType trackType = om.getTypeFactory()
                .constructType(com.example.nextune_backend.dto.response.TrackResponse.class);
        var trackSer = new JacksonTypeRedisSerializer<>(om, trackType);

        perCache.put("track", base.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(trackSer)));

        // ---- tracks_*: List<TrackResponse> ----
        JavaType trackListType = om.getTypeFactory()
                .constructCollectionType(List.class, com.example.nextune_backend.dto.response.TrackResponse.class);
        var trackListSer = new JacksonTypeRedisSerializer<>(om, trackListType);

        var listPair = RedisSerializationContext.SerializationPair.fromSerializer(trackListSer);
        perCache.put("tracks_all", base.serializeValuesWith(listPair));
        perCache.put("tracks_by_album", base.serializeValuesWith(listPair));
        perCache.put("tracks_search", base.serializeValuesWith(listPair));
        perCache.put("album_tracks", base.serializeValuesWith(listPair));
        // albums_search: List<AlbumResponse>
        JavaType albumListType = om.getTypeFactory()
                .constructCollectionType(List.class, com.example.nextune_backend.dto.response.AlbumResponse.class);
        var albumListSer = new JacksonTypeRedisSerializer<>(om, albumListType);
        perCache.put("albums_search", base.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(albumListSer)));

        return RedisCacheManager.builder(cf)
                .cacheDefaults(base)
                .withInitialCacheConfigurations(perCache)
                .transactionAware()
                .build();
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory cf) {
        return new StringRedisTemplate(cf);
    }
}
