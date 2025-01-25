package com.example.ratelimiter.service;

import com.example.ratelimiter.algorithm.SlidingWindowLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.Map;
import java.time.Duration;

@Component
public class SlidingWindowLogService {

    @Autowired
    SlidingWindowLog slidingWindowLog;

    public void checkRateLimits( String ipAddress ){

        boolean justRegistered = this.slidingWindowLog.registerIp( ipAddress );

        Map< String, PriorityBlockingQueue<LocalDateTime>> ipBasedSlidingWindowLog = this.slidingWindowLog.getIpBasedSlidingWindowLog();

        if( !ipBasedSlidingWindowLog.containsKey( ipAddress ) ){

            System.out.println("The Ip address is not found in the sliding window log!!");

        } else {
            PriorityBlockingQueue<LocalDateTime> queue = ipBasedSlidingWindowLog.get( ipAddress );

            int capacity = this.slidingWindowLog.getCapacity();

            int thresholdSeconds = this.slidingWindowLog.getDurationOfWindow();

            if( justRegistered && capacity >= 1 ) return;
            else if( justRegistered ) throw new RuntimeException("Too many requests!!");

            LocalDateTime oldestRequest;

            LocalDateTime currentRequest = LocalDateTime.now();

            Long secondsBetween;

            System.out.println("Removing the older requests from the sliding window log!!");

            do{
                if( queue.isEmpty() ) break;

                queue.poll();
                oldestRequest = queue.peek();
                secondsBetween = Duration.between( oldestRequest, currentRequest ).toSeconds();

            } while ( secondsBetween > thresholdSeconds );

            System.out.println("All the older requests from the sliding window log are removed!!");

            if( queue.size() + 1 <= capacity ){
                System.out.println("The new request is eligible and will be added into the sliding window log!!");
                queue.add( currentRequest );
            } else {
                System.out.println("The number of eligible requests have exceeded in the sliding window log!!");
                throw new RuntimeException("Too many requests!!");
            }

        }

    }

}
