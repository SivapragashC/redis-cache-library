package com.maersk.telikos.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CacheImpl.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RedisConfig.class)
public class CacheImplTest {

    @MockBean
    ReactiveRedisTemplate<Object,Object> reactiveRedisTemplate;

    @MockBean
    ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    @MockBean
    RedissonClient redisson;

    @MockBean
    private ReactiveValueOperations valueOperations;

    @Autowired
    private CacheImpl cacheImpl;

    @Test
    void cacheGetTest() {
        String booking="{\n  \"bookingId\": \"B38\",\n  \"logisticId\": \"L38\",\n  \"captureData\": \"Captured\",\n  \"bookingStatus\": \"Submitted\",\n  \"validationMessage\": \"Unknown\",\n  \"isFulfilment\": false\n}\n";
        when(reactiveRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(Mono.just(booking));
        StepVerifier.create(reactiveRedisTemplate.opsForValue().get("B38"))
                .expectNext("{\n  \"bookingId\": \"B38\",\n  \"logisticId\": \"L38\",\n  \"captureData\": \"Captured\",\n  \"bookingStatus\": \"Submitted\",\n  \"validationMessage\": \"Unknown\",\n  \"isFulfilment\": false\n}\n")
                .verifyComplete();
    }

    @Test
    void cachePutTest() {
        String booking="{\n  \"bookingId\": \"B38\",\n  \"logisticId\": \"L38\",\n  \"captureData\": \"Captured\",\n  \"bookingStatus\": \"Submitted\",\n  \"validationMessage\": \"Unknown\",\n  \"isFulfilment\": false\n}\n";
        when(reactiveRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.set(anyString(),any(),any())).thenReturn(Mono.just(true));
        StepVerifier.create(reactiveRedisTemplate.opsForValue().set("B38",booking,40))
                .expectComplete();
    }
}