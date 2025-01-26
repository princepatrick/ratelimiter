package com.example.ratelimiter.service;

import com.example.ratelimiter.algorithm.FixedWindowCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * The service class for the Fixed Window Counter algorithm
 * The service class checks the ip address registration, attempts to add the new request
 * and blocks if the rate limit is reached
 */
@Component
public class FixedWindowCounterService {

    @Autowired
    public FixedWindowCounter fixedWindowCounter;

    /**
     * @param ipAddress - the ipaddress of the user's device derived from the request
     * The algorithm uses a Map<String, Map<Long, Integer>> based data structure with the data storage as follows
     * Map<IP_ADDRESS, MAP<LOCAL_DATE_TIME_IN_MINUTE, COUNTER_OF_REQUESTS>> The window is marked through the
     * minute converted from the LocalDateTime class.
     */
    public void checkRateLimits(String ipAddress) {

        boolean justRegistered = fixedWindowCounter.registerIp( ipAddress );

        Map<String, Map<Long, Integer>> ipBasedFixedWindowCounter = fixedWindowCounter.getIpBasedFixedWindowCounter();
        int windowCapacity = fixedWindowCounter.getWindowCapacity();

        if (!ipBasedFixedWindowCounter.containsKey(ipAddress)) {

            System.out.println("The IP based Fixed Window Counter does not contain the ip address! " +
                    "Please register the ip address first");

        } else {

            System.out.println("The IP based Fixed Window Counter contains the ip address");

            Map<Long, Integer> timeBasedBucket = ipBasedFixedWindowCounter.get(ipAddress);

            //The Key for the Map is based out of the current minute's window calculated through the LocalDateTime
            LocalDateTime currentTime = LocalDateTime.now();
            Long currentMinute = currentTime.toEpochSecond(ZoneOffset.UTC) / 60;

            if (timeBasedBucket.containsKey(currentMinute)) {

                System.out.println("The fixed window counter exists for the ip address "
                        + ipAddress + " with for the time window " + currentTime );

                int currentLimit = timeBasedBucket.get(currentMinute);

                System.out.println("The current limit in this window is " + currentLimit );

                if (currentLimit < windowCapacity || ( justRegistered && currentLimit == windowCapacity )) {
                    timeBasedBucket.put( currentMinute, currentLimit + (justRegistered ? 0 : 1) );
                    System.out.println("The rate limiter has the capacity to allow the service call!!");
                } else {
                    System.out.println("You have exceeded the api calls. Please try it in the next window(minute).");
                    throw new RuntimeException("The rate limit has been reached!!");
                }

            } else {

                if (windowCapacity < 1) {
                    System.out.println("You have exceeded the api calls. Please try it in the next window(minute).");
                    throw new RuntimeException("The rate limit has been reached!!");
                }

                System.out.println("Entering the element into the ip address "
                        + ipAddress + " with for the time window " + currentTime );
                timeBasedBucket.put(currentMinute, 1);

            }


        }
    }

}
