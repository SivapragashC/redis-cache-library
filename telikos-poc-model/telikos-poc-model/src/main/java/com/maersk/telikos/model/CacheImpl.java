package com.maersk.telikos.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;


@Service
@Slf4j
@ConfigurationProperties("cache")
@Getter
@Setter
public class CacheImpl<K,V> implements ICache<K,V> {

    @Autowired
    ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate;

    private int ttl = 40;

    @Override
    public Mono<V> get(K key) {
        try {
            log.info("Trying to get from cache for this key {}",key);
            return (Mono<V>) reactiveRedisTemplate.opsForValue().get(key);

        } catch (Exception e) {
            log.error("Exception occurred while fetching data from cache {}", e.getMessage());
            throw new CacheException(e.getMessage());
        }
    }

    @Override
    public Mono<Boolean> put(K key, V value) {
        try {
            log.info("Writing to cache with key ---> {}, value ---> {}, ttl ---> {}s", key, value, ttl);
            return reactiveRedisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttl));
        }
        catch (Exception e) {
            log.error("Exception occurred while fetching data from cache {}", e.getMessage());
            throw new CacheException(e.getMessage());
        }
    }

    @Override
    public Mono<Boolean> put(Object key, Object value, int entryTtl){
        try {
        log.info("Writing to cache with key ---> {}, value ---> {}, entry level ttl ---> {}s",key,value,entryTtl);
        return reactiveRedisTemplate.opsForValue().set(key, value , Duration.ofSeconds(entryTtl));
        }
        catch (Exception e) {
            log.error("Exception occurred while fetching data from cache {}", e.getMessage());
            throw new CacheException(e.getMessage());
        }
    }



}
