package com.example.ratelimiter.service;

import com.example.ratelimiter.algorithm.LeakyBucketMeter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/*
* The service class for the Leaky Bucket Meter algorithm
* The service class checks the ip address registration, attempts to add the new request
* and blocks if the rate limit is reached
* */
@Component
public class LeakyBucketMeterService {

    @Autowired
    LeakyBucketMeter leakyBucketMeter;

    /**
     * @param ipAddress - the ipaddress of the user's device derived from the request
     * The algorithm uses a Map<String, Integer> based data structure with the data storage as follows
     * Map<IP_ADDRESS, COUNTER_OF_REQUESTS_IN_PROCESS> The rate limiting implementation merely uses a counter
     * instead of storing the process or information of the process to be stored. This saves a lot on the
     * processing time and data storage space.
     */
    public void checkValidity( String ipAddress ){

        boolean justRegistered = leakyBucketMeter.registerIp( ipAddress );

        Map<String, Integer> ipBasedLeakyBucketMeter = leakyBucketMeter.getIpBasedLeakyBucketMeter();

        //The Leaky Bucket Meter uses a variable "currentFilledRequests" to maintain the counter of the
        // completed requests instead of using a data structure.
        int currentFilledRequests = ipBasedLeakyBucketMeter.get(ipAddress);

        if( currentFilledRequests < leakyBucketMeter.getCapacity()
                || (justRegistered && currentFilledRequests == leakyBucketMeter.getCapacity()  ) ){
            System.out.println("The number of requests have not reached the limit. The service call is permitted!!");
            ipBasedLeakyBucketMeter.put( ipAddress, currentFilledRequests + 1 );
            System.out.println("The current requests after the recent update is " + currentFilledRequests + 1 );
        } else {
            System.out.println("The bucket is filled with older requests!! The rate limiter API has reached the capacity");
            throw new RuntimeException("The rate limit has been reached!!");
        }

    }
}
