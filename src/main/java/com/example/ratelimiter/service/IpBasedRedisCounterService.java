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
    private RedisTemplate<String, Map<String, Integer>> redisTemplate;

    public boolean checkKeyExistsInCounterMap( String ipAddress ){
        System.out.println("We are running checkKeyExistsInCounterMap() for the address ipAddress");
        return redisTemplate.hasKey( ipAddress );
    }

    public void saveIpBasedSlidingWindowCounterMap( String ipAddress, Map<String, Integer> windowCounterObject ){
        System.out.println("The ipAddress is " + ipAddress );
        System.out.println("We are running saveIpBasedSlidingWindowCounterMap() for the address ipAddress");
        redisTemplate.opsForValue().set( ipAddress, windowCounterObject );
        Map<String, Integer> slidingWindowCounterMap = getIpBasedSlidingWindowCounterMap( ipAddress );
        System.out.println("The updated count in saveIpBasedSlidingWindowCounterMap() is " + slidingWindowCounterMap.size());
        for( Map.Entry<String, Integer> entry : slidingWindowCounterMap.entrySet() ){
            System.out.println("The key value pairs in the saveIpBasedSlidingWindowCounterMap() are :");
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println( key + " " + value );
        }
    }

    public Map<String, Integer> getIpBasedSlidingWindowCounterMap( String ipAddress ){
        System.out.println("We are running getIpBasedSlidingWindowCounterMap() for the address ipAddress");
        Map<String, Integer> slidingWindowCounterMap = redisTemplate.opsForValue().get( ipAddress );
        return slidingWindowCounterMap;
    }

    public void deleteIpBasedSlidingWindowCounterMap( String ipAddress ){
        System.out.println("We are running deleteIpBasedSlidingWindowCounterMap() for the address ipAddress");
        redisTemplate.delete( ipAddress );
    }

}
