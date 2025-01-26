package com.example.ratelimiter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 *
 * The Redis Service class that integrates the app with Redis server. The implementation is used for the Sliding
 * Window Counter algorithm. The redis template imitates the most common functionality of the Map<> data structure
 */

@Service
public class IpBasedRedisCounterService {

    @Autowired
    private RedisTemplate<String, Map<Long, Integer>> redisTemplate;

    public boolean checkKeyExistsInCounterMap( String ipAddress ){
        return redisTemplate.hasKey( ipAddress );
    }

    public void saveIpBasedSlidingWindowCounterMap( String ipAddress, Map<Long, Integer> windowCounterObject ){
        redisTemplate.opsForValue().set( ipAddress, windowCounterObject );
    }

    public Map<Long, Integer> getIpBasedSlidingWindowCounterMap( String ipAddress ){
        return redisTemplate.opsForValue().get( ipAddress );
    }

    public void deleteIpBasedSlidingWindowCounterMap( String ipAddress ){
        redisTemplate.delete( ipAddress );
    }

}
