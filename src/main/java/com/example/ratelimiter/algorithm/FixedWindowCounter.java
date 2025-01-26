package com.example.ratelimiter.algorithm;

import com.example.ratelimiter.service.BucketRegistrationService;
import com.example.ratelimiter.util.RateLimitingAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
* The implementation algorithm for the FixedWindowCounter. The registration flows are handled by the
* BucketRegistrationService service. They define the necessary data structures for the time window based counter
* */
@Slf4j
public class FixedWindowCounter {

    @Autowired
    public BucketRegistrationService bucketRegistrationService;


    int windowCapacity;
    Map<String, Map<Long, Integer>> ipBasedFixedWindowCounter;
    RateLimitingAlgorithm algorithm;

    public FixedWindowCounter(int windowCapacity, RateLimitingAlgorithm algorithm) {
        this.windowCapacity = windowCapacity;
        this.ipBasedFixedWindowCounter = Collections.synchronizedMap(new HashMap<>());
        this.algorithm = algorithm;
    }

    public boolean registerIp(String ipAddress) {
        return bucketRegistrationService.registerIp( ipAddress, ipBasedFixedWindowCounter, windowCapacity, algorithm );
    }

    public void deRegisterIp(String ipAddress) {
        bucketRegistrationService.deRegisterIp( ipAddress, ipBasedFixedWindowCounter );
    }

    public int getWindowCapacity(){
        return windowCapacity;
    }

    public Map<String, Map<Long, Integer>> getIpBasedFixedWindowCounter(){
        return getIpBasedFixedWindowCounter();
    }

}
