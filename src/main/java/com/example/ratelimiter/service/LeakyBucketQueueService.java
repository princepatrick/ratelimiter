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

/*
 * The service class for the Leaky Bucket Queue algorithm
 * The service class checks the ip address registration, attempts to add the new request
 * and blocks if the rate limit is reached
 * */
@Component
public class LeakyBucketQueueService {

    @PostConstruct
    public void init() {
        System.out.println("LeakyBucketMeterService initialized");
    }

    @Autowired
    public LeakyBucketQueue leakyBucketQueue;

    /**
     * @param ipAddress - the ipaddress of the user's device derived from the request
     * The algorithm uses a Map<String, PriorityBlockingQueue<Token>> based data structure with the data storage
     * as follows Map<IP_ADDRESS, QUEUE_OF_POINTER_TO_REQUESTS_IN_PROCESS>. The rate limiting implementation merely
     * uses a counter instead of storing the process or information of the process to be stored.
     * This saves a lot on the processing time and data storage space.
     * The priority blocking queue helps in handling concurrent requests.
     */
    public void checkValidity( String ipAddress ) {

        boolean justRegistered = leakyBucketQueue.registerIp( ipAddress );

        Map< String, PriorityBlockingQueue<Token>> leakyBucketQueue = this.leakyBucketQueue.getIpBasedLeakyBucket();

        //The implementation uses a queue to store the user requests.
        //If the queue is filled with requests - then we do not have space to accommodate the requests
        PriorityBlockingQueue<Token> queue = leakyBucketQueue.get(ipAddress);

        if( queue.size() > this.leakyBucketQueue.getCapacity()
                || ( !justRegistered && queue.size() == this.leakyBucketQueue.getCapacity()) ){
            System.out.println("The bucket is filled with older requests!! The rate limiter API is reached the capacity");
            throw new RuntimeException("The rate limit has been reached!!");
        } else {
            System.out.println("The number of requests have not reached the limit. The service call is permitted!!");
            queue.add( new Token( UUID.randomUUID().toString(), LocalDateTime.now() ) );
        }

    }

}
