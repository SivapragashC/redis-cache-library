package com.maersk.telikos.model;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ConfigurationProperties(prefix = "redison-cache")
@Data
@ConditionalOnProperty(name = "enableCache", havingValue = "true")
@Getter
@Slf4j
public class RedisConfig {


    @Value("${redison-cache.url}")
    private String url;

    @Bean()
    RedissonClient redisson() {
        Config config = new Config();
        try
        {
        config.useSingleServer().setAddress(url);
        }
        catch (Exception e) {
            log.error("Exception occurred while fetching data from cache {}", e.getMessage());
            throw new CacheException(e.getMessage());
        }
        return Redisson.create(config);
    }

    @Bean("reactiveRedisConnectionFactory")
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        try
        {
        return new RedissonConnectionFactory(redisson());
        }
        catch (Exception e) {
            log.error("Exception occurred while fetching data from cache {}", e.getMessage());
            throw new CacheException(e.getMessage());
        }
    }


    @Bean(name = "reactiveRedisTemplate")
    public ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        try
        {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        RedisSerializationContext.RedisSerializationContextBuilder<Object, Object> builder = RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<Object, Object> context = builder.value(serializer)
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
        }
        catch (Exception e) {
            log.error("Exception occurred while fetching data from cache {}", e.getMessage());
            throw new CacheException(e.getMessage());
        }
    }



}
