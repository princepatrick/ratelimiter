package com.example.ratelimiter.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

/*
* The class is used to define the RedisTemplate with the Map<String, Map<Long, Integer>> data structure
* and serialization options.
* */
@Configuration
public class RedisTemplateGenerator {

    /**
     * @param redisConnnectionFactory - Used to set up redis connection and stores the critical authentication
     *                                and setup details
     * @return RedisTemplate - Used to return an instance with the serializers and deserializers to handle the
     * data storage
     * The method integrates the redis template instance with the redisConnectionFactory and setup the necessary
     * serializers for the Key and Value.
     */
    @Bean
    public RedisTemplate<String, Map<String, Integer>> redisTemplate( RedisConnectionFactory redisConnnectionFactory ){
        RedisTemplate<String, Map<String, Integer>> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnnectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Map.class));

        return template;
    }

}
