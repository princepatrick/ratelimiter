package com.example.ratelimiter.service;

import com.example.ratelimiter.algorithm.SlidingWindowCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/*
 * The service class for the Sliding Window Counter algorithm
 * The service class checks the ip address registration, attempts to add the new request
 * and blocks if the rate limit is reached
 * */
@Component
public class SlidingWindowCounterService {

    @Autowired
    SlidingWindowCounter slidingWindowCounter;

    @Autowired
    IpBasedRedisCounterService ipBasedRedisCounterService;

    /**
     * @param ipAddress - the ipaddress of the user's device derived from the request
     * The algorithm uses a Map<String, Map<Long, Integer>> based data structure with the data storage
     * as follows Map<IP_ADDRESS, Map<WINDOW_DEFINED_IN_MINUTES, COUNTER_OF_REQUESTS_TO_BE_PROCESSED>>.
     * The data structure used is a Redis based centralized counter, that helps in handling requests in a
     * distributed server setup.
     * The rate limiting implementation merely uses a counter instead of storing the process or information of
     * the process to be stored. This saves a lot on the processing time and data storage space.
     */
    public void checkRateLimits( String ipAddress ){

        boolean justRegistered = slidingWindowCounter.registerIp( ipAddress );

        int windowCapacity = slidingWindowCounter.getWindowCapacity();

        if( justRegistered && windowCapacity >= 1 ) {
            System.out.println("The request is within the rate limiter's limits and could be processed!!");
            return;
        } else if( justRegistered ) {
            System.out.println("The request has crossed the limits of the rate limiter");
            throw new RuntimeException("Too many requests have been processed");
        }

        if( !ipBasedRedisCounterService.checkKeyExistsInCounterMap( ipAddress ) ){
            System.out.println("The user has not previously reached out to the API service.");
        } else {

            Map< String, Integer > slidingWindowCounterMap = ipBasedRedisCounterService.getIpBasedSlidingWindowCounterMap( ipAddress );

            //The algorithm tries to predict the current rate limit factor based on the number of requests in the
            //current window, and gets the ratio of the requests from the previous window
            //This helps in monitoring a rapid rise of requests during the transition between the windows
            Long currentSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            Long currentMinute = currentSeconds / 60;

            float ratio = (float) (currentSeconds % 60) / (float) 60;
            Long prevMinute = currentMinute - 1;
            int currentCount = 0, prevCount = 0;

            String currentMinuteStr = String.valueOf( currentMinute );
            String prevMinuteStr = String.valueOf(prevMinute);

            System.out.println( "The currentMinute is " + currentMinute + " and prevMinute is " + prevMinute );
            if( slidingWindowCounterMap.containsKey( currentMinuteStr ) ){
                currentCount = slidingWindowCounterMap.get( currentMinuteStr );
                System.out.println("The currentMinute is " + currentMinute + " and the currentCount is " + currentCount );
            }

            if( slidingWindowCounterMap.containsKey( prevMinuteStr ) ){
                prevCount = slidingWindowCounterMap.get( prevMinuteStr );
                System.out.println("The prevMinute is " + prevMinute + " and the currentCount is " + prevCount );
            }

            float currentRequestCount = (float) currentCount + (prevCount * ratio);

            System.out.println("The existing count of requests is : " + currentRequestCount
                    + " and the windowCapacity is " + windowCapacity );

            if( currentRequestCount + 1 <= (float) windowCapacity ){
                System.out.println("The request is valid and could be processed!!");

                slidingWindowCounterMap.put( currentMinuteStr, currentCount + 1 );
                System.out.println("The new updated value is " + slidingWindowCounterMap.get( currentMinute ));
                ipBasedRedisCounterService.saveIpBasedSlidingWindowCounterMap( ipAddress, slidingWindowCounterMap );
            } else {
                System.out.println("The request has crossed the limits of the rate limiter");
                throw new RuntimeException("Too many requests have been processed");
            }

        }

    }

}
