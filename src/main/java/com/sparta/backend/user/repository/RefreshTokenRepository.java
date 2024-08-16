package com.sparta.backend.user.repository;

public interface RefreshTokenRepository {

    void save(String key, String value);

    String findByKey(String key);

    Boolean existsByKey(String key);

    void delete(String key);
}
