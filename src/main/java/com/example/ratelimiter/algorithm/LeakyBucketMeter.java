package com.example.ratelimiter.algorithm;

import com.example.ratelimiter.util.BucketUtil;
import com.example.ratelimiter.util.RateLimitingAlgorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LeakyBucketMeter {

    @Autowired
    public BucketUtil bucketUtil;

    @Getter
    int capacity;
    int currentLevel;
    @Getter
    Map<String, Integer> ipBasedLeakyBucketMeter;
    RateLimitingAlgorithm algorithm;

    public LeakyBucketMeter( int capacity, RateLimitingAlgorithm algorithm ){
        this.capacity = capacity;
        this.currentLevel = 0;
        this.algorithm = algorithm;
        this.ipBasedLeakyBucketMeter = Collections.synchronizedMap(new HashMap<>());
    }

    public boolean registerIp( String ipAddress ){
        return bucketUtil.registerIp( ipAddress, ipBasedLeakyBucketMeter, capacity, algorithm );
    }

    public void deregisterIp( String ipAddress ){
        bucketUtil.deRegisterIp(ipAddress, ipBasedLeakyBucketMeter);
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

}
