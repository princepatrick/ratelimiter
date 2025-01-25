package com.example.ratelimiter.service;

import com.example.ratelimiter.algorithm.SlidingWindowCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Component
public class SlidingWindowCounterService {

    @Autowired
    SlidingWindowCounter slidingWindowCounter;

    public void checkRateLimits( String ipAddress ){

        boolean justRegistered = slidingWindowCounter.registerIp( ipAddress );

        Map< String, Map<Long, Integer > > ipBasedSlidingWindowCounter = slidingWindowCounter.getIpBasedSlidingWindowCounter();

        int windowCapacity = slidingWindowCounter.getWindowCapacity();

        if( justRegistered && windowCapacity >= 1 ) {
            System.out.println("The request is within the rate limiter's limits and could be processed!!");
            return;
        } else if( justRegistered ) {
            System.out.println("The request has crossed the limits of the rate limiter");
            throw new RuntimeException("Too many requests have been processed");
        }

        if( !ipBasedSlidingWindowCounter.containsKey( ipAddress ) ){
            System.out.println("The user has not previously reached out to the API service.");
        } else {

            Map< Long, Integer > slidingWindowCounterMap = ipBasedSlidingWindowCounter.get( ipAddress );
            Long currentSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            Long currentMinute = currentSeconds / 60;

            float ratio = (float) (currentSeconds % 60) / (float) 60;
            Long prevMinute = currentMinute - 1;
            int currentCount = 0, prevCount = 0;

            if( slidingWindowCounterMap.containsKey( currentMinute ) ){
                currentCount = slidingWindowCounterMap.get( currentMinute );
            }

            if( slidingWindowCounterMap.containsKey( prevMinute ) ){
                prevCount = slidingWindowCounterMap.get( prevMinute );
            }

            float currentRequestCount = (float) currentCount + (prevCount * ratio);

            System.out.println("The currentRequestCount is : " + currentRequestCount + " and the windowCapacity is " + windowCapacity );

            if( currentRequestCount + 1 <= (float) windowCapacity ){
                System.out.println("The request is valid and could be processed!!");
                slidingWindowCounterMap.put( currentMinute, currentCount + 1 );
            } else {
                System.out.println("The request has crossed the limits of the rate limiter");
                throw new RuntimeException("Too many requests have been processed");
            }

        }

    }

}
