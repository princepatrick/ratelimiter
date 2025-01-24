package com.example.ratelimiter.service;

import com.example.ratelimiter.algorithm.FixedWindowCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Map;

@Component
public class FixedWindowCounterService {

    @Autowired
    public FixedWindowCounter fixedWindowCounter;

    public void checkRateLimits(String ipAddress) {

        boolean justRegistered = fixedWindowCounter.registerIp( ipAddress );

        Map<String, Map<String, Integer>> ipBasedFixedWindowCounter = fixedWindowCounter.getIpBasedFixedWindowCounter();
        int windowCapacity = fixedWindowCounter.getWindowCapacity();

        if (!ipBasedFixedWindowCounter.containsKey(ipAddress)) {

            System.out.println("The IP based Fixed Window Counter does not contain the ip address! " +
                    "Please register the ip address first");

        } else {

            System.out.println("The IP based Fixed Window Counter contains the ip address");

            Map<String, Integer> timeBasedBucket = ipBasedFixedWindowCounter.get(ipAddress);

            LocalTime currentTime = LocalTime.now();
            int hour = currentTime.getHour();
            int minute = currentTime.getMinute();
            String time = String.valueOf(hour) + ":" + String.valueOf(minute);

            if (timeBasedBucket.containsKey(time)) {

                System.out.println("The fixed window counter exists for the ip address "
                        + ipAddress + " with for the time window " + time );

                int currentLimit = timeBasedBucket.get(time);

                System.out.println("The current limit in this window is " + currentLimit );

                if (currentLimit < windowCapacity || ( justRegistered && currentLimit == windowCapacity )) {
                    timeBasedBucket.put( time, currentLimit + (justRegistered ? 0 : 1) );
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
                        + ipAddress + " with for the time window " + time );
                timeBasedBucket.put(time, 1);

            }


        }
    }

}
