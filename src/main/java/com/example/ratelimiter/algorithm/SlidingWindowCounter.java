package com.example.ratelimiter.algorithm;

import com.example.ratelimiter.util.BucketUtil;
import com.example.ratelimiter.util.RateLimitingAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SlidingWindowCounter {

    @Autowired
    BucketUtil bucketUtil;

    int windowCapacity;
    Map< String, Map<Long, Integer>> ipBasedSlidingWindowCounter;
    RateLimitingAlgorithm algorithm;

    public SlidingWindowCounter( int windowCapacity, RateLimitingAlgorithm algorithm ){
        this.windowCapacity = windowCapacity;
        this.algorithm = algorithm;
        this.ipBasedSlidingWindowCounter = Collections.synchronizedMap(new HashMap<>());
    }

    public boolean registerIp( String ipAddress ){
        return bucketUtil.registerIp( ipAddress, ipBasedSlidingWindowCounter, windowCapacity, RateLimitingAlgorithm.SLIDING_WINDOW_COUNTER );
    }

    public void deRegisterIp( String ipAddress ){
        ipBasedSlidingWindowCounter.remove( ipAddress );
    }

    public Map<String, Map<Long, Integer>> getIpBasedSlidingWindowCounter(){
        return ipBasedSlidingWindowCounter;
    }

    public int getWindowCapacity(){
        return windowCapacity;
    }

}