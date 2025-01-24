package com.example.ratelimiter.service;

import com.example.ratelimiter.algorithm.LeakyBucketMeter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LeakyBucketMeterService {

    @Autowired
    LeakyBucketMeter leakyBucketMeter;

    public void checkValidity( String ipAddress ){

        boolean justRegistered = leakyBucketMeter.registerIp( ipAddress );

        Map<String, Integer> ipBasedLeakyBucketMeter = leakyBucketMeter.getIpBasedLeakyBucketMeter();

        int currentFilledRequests = ipBasedLeakyBucketMeter.get(ipAddress);

        if( currentFilledRequests < leakyBucketMeter.getCapacity()
                || (justRegistered && currentFilledRequests == leakyBucketMeter.getCapacity()  ) ){
            System.out.println("The number of requests have not reached the limit. The service call is permitted!!");
            ipBasedLeakyBucketMeter.put( ipAddress, currentFilledRequests + 1 );
            System.out.println("The current requests after the recent update is " + currentFilledRequests + 1 );
        } else {
            System.out.println("The bucket is filled with older requests!! The rate limiter API has reached the capacity");
            throw new RuntimeException("The rate limit has been reached!!");
        }

    }
}
