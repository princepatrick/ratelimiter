package com.example.ratelimiter.algorithm;

import com.example.ratelimiter.service.BucketRegistrationService;
import com.example.ratelimiter.util.RateLimitingAlgorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import java.time.LocalDateTime;
import java.util.concurrent.PriorityBlockingQueue;

@Getter
public class SlidingWindowLog {

    @Autowired
    BucketRegistrationService bucketRegistrationService;

    Map<String, PriorityBlockingQueue<LocalDateTime>> ipBasedSlidingWindowLog;
    int capacity;
    RateLimitingAlgorithm algorithm;
    int durationOfWindow;

    public SlidingWindowLog( int capacity, int durationOfWindow, RateLimitingAlgorithm algorithm ){
        this.capacity = capacity;
        this.durationOfWindow = durationOfWindow;
        this.algorithm = algorithm;
        this.ipBasedSlidingWindowLog = new HashMap<>();
    }

    public boolean registerIp( String ipAddress ){
        return bucketRegistrationService.registerIp( ipAddress, ipBasedSlidingWindowLog, capacity, algorithm );
    }

    public void deRegisterIp( String ipAddress ){
        ipBasedSlidingWindowLog.remove( ipAddress );
    }

    public int getCapacity(){
        return capacity;
    }

    public Map<String, PriorityBlockingQueue<LocalDateTime>> getIpBasedSlidingWindowLog() {
        return ipBasedSlidingWindowLog;
    }

    public int getDurationOfWindow(){
        return durationOfWindow;
    }
}
