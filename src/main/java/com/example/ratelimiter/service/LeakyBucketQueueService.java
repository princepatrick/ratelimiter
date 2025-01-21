package com.example.ratelimiter.service;

import com.example.ratelimiter.algorithm.LeakyBucketQueue;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.ratelimiter.util.Token;

import java.time.LocalDateTime;
import java.util.Map;

import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class LeakyBucketQueueService {

    @PostConstruct
    public void init() {
        System.out.println("LeakyBucketMeterService initialized");
    }


    @Autowired
    public LeakyBucketQueue leakyBucketQueue;

    public void checkValidity( String ipAddress ) {

        leakyBucketQueue.registerIp( ipAddress );

        Map< String, PriorityBlockingQueue<Token>> leakyBucketQueue = this.leakyBucketQueue.getIpBasedLeakyBucket();

        PriorityBlockingQueue<Token> queue = leakyBucketQueue.get(ipAddress);

        if( queue.size() == this.leakyBucketQueue.getCapacity() ){
            System.out.println("The bucket is filled with older requests!! The rate limiter API is reached the capacity");
            throw new RuntimeException("The rate limit has been reached!!");
        } else {
            System.out.println("The number of requests have not reached the limit. The service call is permitted!!");
            queue.add( new Token( UUID.randomUUID().toString(), LocalDateTime.now() ) );
        }

    }

}
