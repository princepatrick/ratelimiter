package com.example.ratelimiter.service;

import com.example.ratelimiter.algorithm.LeakyBucketMeter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.ratelimiter.util.Token;

import java.time.LocalDateTime;
import java.util.Map;

import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class LeakyBucketMeterService {

    @Autowired
    public LeakyBucketMeter leakyBucketMeter;

    public void checkValidity( String ipAddress ) {

        leakyBucketMeter.registerIp( ipAddress );

        Map< String, PriorityBlockingQueue<Token>> leakyBucketQueue = leakyBucketMeter.getIpBasedLeakyBucket();

        PriorityBlockingQueue<Token> queue = leakyBucketQueue.get(ipAddress);

        if( queue.size() == leakyBucketMeter.getCapacity() ){
            System.out.println("The bucket is filled with older requests!! The rate limiter API is reached the capacity");
            throw new RuntimeException("The rate limit has been reached!!");
        } else {
            System.out.println("The number of requests have not reached the limit. The service call is permitted!!");
            queue.add( new Token( UUID.randomUUID().toString(), LocalDateTime.now() ) );
        }


    }

}
