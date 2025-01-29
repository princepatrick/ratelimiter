package com.example.ratelimiter.service;

import com.example.ratelimiter.util.RateLimitingAlgorithm;
import com.example.ratelimiter.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/*
* A common implementation of the ip address registration flow for each Rate limiting algorithm
* */
@Component
public class BucketRegistrationService {

    @Autowired
    IpBasedRedisCounterService ipBasedRedisCounterService;

    /*
      @param ip - The ip address of the current user request
      @param ipBasedDataStruct - A generic based implementation of the Map data structure that stores the information
            based on the ip address
      @param capacity - The capacity permitted with the data structure associated with the ip address
      @param algorithm - The algorithm used by the Ratelimiter
      @return true if the registration is successful, false if unsuccessful
    * The method contains the central functionality of the Registration service depending on the implementation
    * All the implementations use a local Map based flow except for the Sliding Window Counter implementation that
    * uses a Redis based implementation
    * */

    public <T> boolean registerIp( String ip, Map<String, T> ipBasedDataStruct, int capacity, RateLimitingAlgorithm algorithm ){

        System.out.println("Running registerIp() in BucketUtil");

        if( (algorithm == RateLimitingAlgorithm.SLIDING_WINDOW_COUNTER && ipBasedRedisCounterService.checkKeyExistsInCounterMap(ip) )
        || ( algorithm == RateLimitingAlgorithm.SLIDING_WINDOW_COUNTER && ipBasedDataStruct != null && ipBasedDataStruct.containsKey(ip)) ){

            System.out.println("The IP Address is already present in the TokenBucket");
            return false;

        } else {

            System.out.println("The IP Address is being registered");

            PriorityBlockingQueue<Token> tokenQueue = new PriorityBlockingQueue<>( capacity,
                    new Comparator<Token>() {
                        @Override
                        public int compare(Token o1, Token o2) {
                            return o1.getUUID().compareTo(o2.getUUID());
                        }
                    }
            );

            PriorityBlockingQueue<LocalDateTime> localDateTimeQueue = new PriorityBlockingQueue<>( capacity,
                    new Comparator<LocalDateTime>(){
                        @Override
                        public int compare( LocalDateTime time1, LocalDateTime time2 ){

                            return time1.compareTo(time2);
                        }
                    }
            );

            //The flow diverges depending on the Rate limiting algorithm in this switch statement
            switch( algorithm ){
                case TOKEN_BUCKET :

                    Map<String, PriorityBlockingQueue<Token>> ipBasedTokenBucket = (Map<String, PriorityBlockingQueue<Token>>) ipBasedDataStruct;
                    System.out.println("The tokens are added to the queue for the Token Bucket algorithm");
                    for( int i=0 ; i< capacity ; i++ ){
                        tokenQueue.add( new Token( UUID.randomUUID().toString(), LocalDateTime.now() ));
                    }
                    System.out.println("The queue is added to the bucket");
                    ipBasedTokenBucket.put( ip, tokenQueue );

                    break;

                case LEAKY_BUCKET_QUEUE:

                    Map<String, PriorityBlockingQueue<Token>> ipBasedLeakyBucketQueue = ( Map<String, PriorityBlockingQueue<Token>> ) ipBasedDataStruct;
                    System.out.println("Add a token that indicates the process that needs to be processed");
                    tokenQueue.add( new Token( UUID.randomUUID().toString(), LocalDateTime.now() ));
                    System.out.println("The queue with a single process is added to the bucket");
                    ipBasedLeakyBucketQueue.put( ip, tokenQueue );

                    break;

                case LEAKY_BUCKET_METER:

                    Map<String, Integer> ipBasedLeakyBucketMeter = ( Map<String, Integer> ) ipBasedDataStruct;
                    System.out.println("Increment or define the map with the first process");
                    ipBasedLeakyBucketMeter.put( ip, 1 );

                    break;

                case FIXED_WINDOW_COUNTER:

                    Map< String, Map<String, Integer>> ipBasedFixedWindowCounter = ( Map<String, Map<String, Integer>>) ipBasedDataStruct;

                    ipBasedFixedWindowCounter.put( ip, Collections.synchronizedMap( new HashMap<>()));

                    System.out.println("Retrieving the current time and finding the fixed window (current minute)");
                    LocalTime currentTime = LocalTime.now();
                    int hour = currentTime.getHour();
                    int minute = currentTime.getMinute();
                    String time = String.valueOf( hour ) + ":" + String.valueOf( minute );

                    System.out.println("Inserting into the new ipaddress with the new timestamp (current minute)");
                    ipBasedFixedWindowCounter.get( ip ).put( time, 1 );

                    break;

                case SLIDING_WINDOW_LOG:

                    Map< String, PriorityBlockingQueue<LocalDateTime>> ipBasedSlidingWindowLog = ( Map<String, PriorityBlockingQueue<LocalDateTime>> ) ipBasedDataStruct ;
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    System.out.println("Inserting into the local date time queue with the current LocalDateTime value");
                    localDateTimeQueue.add(currentDateTime);

                    break;

                case SLIDING_WINDOW_COUNTER:

                    System.out.println("Retrieving the current window(minute) from the current time");
                    Long currentMinute = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) / 60;
                    String currMinuteStr = String.valueOf(currentMinute);

                    System.out.println("Inserting the local date time window (minute) into the queue");
                    Map<String, Integer> slidingWindowCounterMap = new HashMap<>();
                    slidingWindowCounterMap.put( currMinuteStr, 1 );

                    ipBasedRedisCounterService.saveIpBasedSlidingWindowCounterMap( ip, slidingWindowCounterMap );

                    break;

                default:

                    break;

            }

            return true;
        }

    }

    /*
      @param ip - The ip address of the current user request
      @param ipBasedTokenBucket - A generic based implementation of the Map data structure that stores the information
            based on the ip address
    * A common implementation of the expulsion of the ipaddress from the data storage
    * */
    public <T> void deRegisterIp( String ip, Map< String, T> ipBasedTokenBucket ){
        ipBasedTokenBucket.remove( ip );
    }

}
