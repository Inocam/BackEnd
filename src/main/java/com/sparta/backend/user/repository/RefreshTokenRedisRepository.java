package com.sparta.backend.user.repository;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Primary
@Repository
public class RefreshTokenRedisRepository implements RefreshTokenRepository {
    private static final long EXPIRE_TIME = 6 * 60 * 60; // 6시간

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOperations;

    @Override
    public void save(String key, String value) {
        valueOperations.set(key, value, EXPIRE_TIME, TimeUnit.SECONDS);
    }

    @Override
    public String findByKey(String key) {
        return valueOperations.get(key);
    }

    @Override
    public Boolean existsByKey(String key) {
        return valueOperations.getOperations().hasKey(key);
    }

    @Override
    public void delete(String key) {
        valueOperations.getOperations().delete(key);
    }
}