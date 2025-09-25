package com.example.nextune_backend.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class JacksonTypeRedisSerializer<T> implements RedisSerializer<T> {
    private final ObjectMapper om;
    private final JavaType type;

    public JacksonTypeRedisSerializer(ObjectMapper om, JavaType type) {
        this.om = om;
        this.type = type;
    }

    @Override
    public byte[] serialize(T value) throws SerializationException {
        if (value == null) return new byte[0];
        try {
            return om.writerFor(type).writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Could not write JSON", e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) return null;
        try {
            return om.readerFor(type).readValue(bytes);
        } catch (Exception e) {
            throw new SerializationException("Could not read JSON", e);
        }
    }
}
