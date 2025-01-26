package com.example.ratelimiter.algorithm;

import com.example.ratelimiter.service.BucketRegistrationService;
import com.example.ratelimiter.util.RateLimitingAlgorithm;
import com.example.ratelimiter.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

public class LeakyBucketQueue {

    int capacity;
    Map<String, PriorityBlockingQueue<Token>> ipBasedLeakyBucket;
    RateLimitingAlgorithm algorithm;

    @Autowired
    BucketRegistrationService bucketRegistrationService;

    public LeakyBucketQueue(int capacity, RateLimitingAlgorithm algorithm ){
        this.capacity = capacity;
        this.ipBasedLeakyBucket = Collections.synchronizedMap(new HashMap<>());
        this.algorithm = algorithm;
    }

    public boolean registerIp( String ip ){
        return bucketRegistrationService.registerIp( ip, ipBasedLeakyBucket, capacity, algorithm );
    }

    public void deRegisterIp( String ip ){
        bucketRegistrationService.deRegisterIp( ip, ipBasedLeakyBucket );
    }

    @Scheduled( cron = "*/5 * * * * ?" )
    public void performRequest(){

        System.out.println("The performRequest() cron job is called at 5 second gap");

        if( ipBasedLeakyBucket.isEmpty() ){
            System.out.println("We do not have any requests called to perform");
        } else {
            for( Map.Entry<String, PriorityBlockingQueue<Token>>  itr : ipBasedLeakyBucket.entrySet() ){
                PriorityBlockingQueue<Token> queue = itr.getValue();

                if( queue.isEmpty() ){
                    System.out.println("We do not have any requests in the bucket to perform for the ip address" + itr.getKey() );
                } else {
                    System.out.println("We have requests in the bucket. We will process one of them");
                    queue.poll();
                }
            }
        }
    }

    public int getCapacity(){
        return capacity;
    }

    public Map<String, PriorityBlockingQueue<Token>> getIpBasedLeakyBucket(){
        return ipBasedLeakyBucket;
    }

}
