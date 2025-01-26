package com.example.ratelimiter.service;

import com.example.ratelimiter.algorithm.TokenBucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import com.example.ratelimiter.util.Token;

/*
 * The service class for the Token Bucket algorithm
 * The service class checks the ip address registration, attempts to add the new request
 * and blocks if the rate limit is reached
 * */
@Component
@Slf4j
public class TokenBucketService {

    @Autowired
    TokenBucket tokenBucket;

    /**
     * @param ipAddress - the ipaddress of the user's device derived from the request
     * The algorithm uses a Map<String, PriorityBlockingQueue<Token>> based data structure with the
     * data storage as: Map<IP_ADDRESS, PriorityBlockingQueue<POINTER_TO_REQUESTS_TO_BE_PROCESSED>>.
     * The implementation uses a queue to store the tokens to perform the service requests. As long as there are
     * tokens in the queue, the requests can be processed.
     */
    public Token checkValidity( String ipAddress ){

        tokenBucket.registerIp(ipAddress);

        Map<String, PriorityBlockingQueue<Token>> ipBasedTokenBucket = tokenBucket.getIpBasedTokenBucket();

        //The priorityBlockingQueue is used to add tokens into the bucket with X numbers each unit time.
        //Whenever an user request is processed, we check existence of any token in the bucket
        //If the token is present, then the request is processed, else they are blocked
        if( ipBasedTokenBucket.containsKey(ipAddress) ){

            PriorityBlockingQueue<Token> ipBasedQueue = ipBasedTokenBucket.get( ipAddress );

            if( ipBasedQueue.isEmpty() ){
                System.out.println("The rate limit has reached.");
                throw new RuntimeException("The rate limit has reached.");
            } else {
                System.out.println("You can perform the operation");
                return ipBasedQueue.poll();
            }
        } else {
            System.out.println("The ip address is not registered.");
            throw new RuntimeException("The ip address is not registered.");
        }

    }

}
