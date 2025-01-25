package com.example.ratelimiter.algorithm;

import com.example.ratelimiter.util.BucketUtil;
import com.example.ratelimiter.util.RateLimitingAlgorithm;
import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalTime;


@Slf4j
public class FixedWindowCounter {

    @Autowired
    public BucketUtil bucketUtil;

    @Getter
    int windowCapacity;
    @Getter
    Map<String, Map<String, Integer>> ipBasedFixedWindowCounter;
    RateLimitingAlgorithm algorithm;

    public FixedWindowCounter(int windowCapacity, RateLimitingAlgorithm algorithm) {
        this.windowCapacity = windowCapacity;
        this.ipBasedFixedWindowCounter = Collections.synchronizedMap(new HashMap<>());
        this.algorithm = algorithm;
    }

    public boolean registerIp(String ipAddress) {
        return bucketUtil.registerIp( ipAddress, ipBasedFixedWindowCounter, windowCapacity, algorithm );
    }

    public void deRegisterIp(String ipAddress) {
        bucketUtil.deRegisterIp( ipAddress, ipBasedFixedWindowCounter );
    }

}
