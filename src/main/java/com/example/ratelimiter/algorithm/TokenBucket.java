package com.example.ratelimiter.algorithm;

import com.example.ratelimiter.service.BucketRegistrationService;
import com.example.ratelimiter.util.RateLimitingAlgorithm;
import com.example.ratelimiter.util.Token;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/*
 * The implementation algorithm for the Token Bucket Queue. The registration flows are handled by the
 * BucketRegistrationService service. They define the necessary data structures - a bucket that holds the tokens to
 * be used to process the requests.
 * A cron job is scheduled at every 30 seconds to refill the tokens into the bucket to facilitate future request
 * calls.
 * */
@Slf4j
public class TokenBucket {

    int capacity;
    @Getter
    Map<String, PriorityBlockingQueue<Token>> ipBasedTokenBucket;
    RateLimitingAlgorithm algorithm;

    @Autowired
    public BucketRegistrationService bucketRegistrationService;

    public TokenBucket(int threshold, RateLimitingAlgorithm algorithm ){
        this.capacity = threshold;
        this.ipBasedTokenBucket = Collections.synchronizedMap(new HashMap<>());
        this.algorithm = algorithm;
    }

    public boolean registerIp( String ip ){
        return bucketRegistrationService.registerIp( ip, ipBasedTokenBucket, this.capacity, algorithm );
    }

    public void deregisterIp( String ip ){
        bucketRegistrationService.deRegisterIp( ip, this.ipBasedTokenBucket );
    }

//    @Scheduled( cron = "*/30 * * * * ?" )
//    public void addTokenIntoBucket(){
//
//        //Check if ipBasedTokenBucket is empty - Then no IP addresses have been registered, hence, we cannot insert a token into the bucket
//        if( ipBasedTokenBucket.isEmpty() ){
////            log.info("The Token Bucket is empty of ip addresses. Hence we cannot insert tokens.");
//            System.out.println("The Token Bucket is empty of ip addresses. Hence we cannot insert tokens.");
//        } else {
//            //If the ipBasedTokenBucket is present - then iterate through each of the ipaddress's Token Bucket
////            log.info("The token bucket is available, and we can iterate through the ipaddress ");
//            System.out.println("The token bucket is available, and we can iterate through the ipaddress ");
//
//            for( Map.Entry<String, PriorityBlockingQueue<Token>> ipBasedQueue : ipBasedTokenBucket.entrySet()){
//                PriorityBlockingQueue<Token> queue = ipBasedQueue.getValue();
//
//                if( queue.size() == this.capacity ){
//                    //For each ipaddress - check the corresponding Token bucket's capacity - if full ignore
////                    log.info("The bucket has reached it's capacity. Please ignore.");
//                    System.out.println("The bucket has reached it's capacity. Please ignore.");
//                } else {
//                    //If the bucket is having vacancy, then insert a single token
////                    log.info("The bucket has a vacant space. Please insert a single token");
//                    System.out.println("The bucket has a vacant space. Please insert a single token");
//
//                    queue.add( new Token( UUID.randomUUID().toString(), LocalDateTime.now() ) );
//                }
//            }
//        }
//
//    }

    public int getCapacity(){
        return capacity;
    }

    public Map<String, PriorityBlockingQueue<Token>> getIpBasedTokenBucket(){
        return ipBasedTokenBucket;
    }

}
