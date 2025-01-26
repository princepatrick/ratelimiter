package com.example.ratelimiter.algorithm;

import com.example.ratelimiter.service.BucketRegistrationService;
import com.example.ratelimiter.util.RateLimitingAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 * The implementation algorithm for the Leaky Bucket Meter. The registration flows are handled by the
 * BucketRegistrationService service. They define the necessary counter variable to hold the number of requests.
 * A cron job is scheduled at every 5 seconds to search for any pending request in the bucket and process it
 * (ie decrement the counter of the request)
 * */
public class LeakyBucketMeter {

    @Autowired
    public BucketRegistrationService bucketRegistrationService;

    int capacity;
    int currentLevel;
    Map<String, Integer> ipBasedLeakyBucketMeter;
    RateLimitingAlgorithm algorithm;

    public LeakyBucketMeter( int capacity, RateLimitingAlgorithm algorithm ){
        this.capacity = capacity;
        this.currentLevel = 0;
        this.algorithm = algorithm;
        this.ipBasedLeakyBucketMeter = Collections.synchronizedMap(new HashMap<>());
    }

    public boolean registerIp( String ipAddress ){
        return bucketRegistrationService.registerIp( ipAddress, ipBasedLeakyBucketMeter, capacity, algorithm );
    }

    public void deregisterIp( String ipAddress ){
        bucketRegistrationService.deRegisterIp(ipAddress, ipBasedLeakyBucketMeter);
    }

    @Scheduled( cron = "*/5 * * * * ?")
    public void performRequest(){

        if( ipBasedLeakyBucketMeter == null ){
            System.out.println("The Leaky Bucket Meter is Null");
            return;
        }

        //Check if the ipBasedLeakyBucketMeter has any ip address that contains any unprocessed requests
        if( ipBasedLeakyBucketMeter.isEmpty() ){
            System.out.println("We do not have any requests called to perform");
        } else {

            //Loop through each ip address
            for( Map.Entry<String, Integer> mItr : ipBasedLeakyBucketMeter.entrySet()  ) {
                int pendingRequestCount = mItr.getValue();

                if( pendingRequestCount > 0 ) {
                    //If there are any unprocessed requests, if so reduce the counter - indicating that the process is completed
                    System.out.println("The request is completed");
                    pendingRequestCount--;
                    mItr.setValue(pendingRequestCount);
                } else {
                    //If an ipaddress does not have any pending requests (ie the value is 0), then skip the process
                    System.out.println("We do not have any requests in the bucket to perform for the ip address" + mItr.getKey() );
                }
            }
        }
    }

    public int getCapacity(){
        return capacity;
    }

    public Map<String, Integer> getIpBasedLeakyBucketMeter(){
        return ipBasedLeakyBucketMeter;
    }

}
