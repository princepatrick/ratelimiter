package com.example.ratelimiter.service;

import com.example.ratelimiter.algorithm.TokenBucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import com.example.ratelimiter.util.Token;

@Component
@Slf4j
public class TokenBucketService {

    @Autowired
    TokenBucket tokenBucket;

    public Token checkValidity( String ipAddress ){

        tokenBucket.registerIp(ipAddress);

        Map<String, PriorityBlockingQueue<Token>> ipBasedTokenBucket = tokenBucket.getIpBasedTokenBucker();

        if( ipBasedTokenBucket.containsKey(ipAddress) ){

            PriorityBlockingQueue<Token> ipBasedQueue = ipBasedTokenBucket.get( ipAddress );

            if( ipBasedQueue.isEmpty() ){
//                log.error("The rate limit has reached.");
                System.out.println("The rate limit has reached.");
                throw new RuntimeException("The rate limit has reached.");
            } else {
//                log.info("You can perform the operation");
                System.out.println("You can perform the operation");
                return ipBasedQueue.poll();
            }
        } else {
//            log.error("The ip address is not registered.");
            System.out.println("The ip address is not registered.");
            throw new RuntimeException("The ip address is not registered.");
        }

    }

}
