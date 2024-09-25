package com.sparta.delivery.etc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.sparta.delivery.cart.entity.Cart;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Cart> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Cart> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 키를 문자열로 직렬화
        template.setKeySerializer(new StringRedisSerializer());

        // 값을 JSON으로 직렬화
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}
