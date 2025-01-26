package com.example.ratelimiter.service;

import com.example.ratelimiter.util.RateLimitingAlgorithm;
import com.example.ratelimiter.util.Token;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class BucketRegistrationService {


    public <T> boolean registerIp( String ip, Map<String, T> ipBasedDataStruct, int capacity, RateLimitingAlgorithm algorithm ){

        System.out.println("Running registerIp() in BucketUtil");

        if( ipBasedDataStruct != null && ipBasedDataStruct.containsKey(ip) ){

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

                    Map<String, Map<Long, Integer>> ipBasedSlidingWindowCounter = ( Map<String, Map<Long, Integer>>) ipBasedDataStruct;
                    ipBasedSlidingWindowCounter.put( ip, Collections.synchronizedMap( new HashMap<>() ) );

                    System.out.println("Retrieving the current window(minute) from the current time");
                    Long currentMinute = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) / 60;

                    System.out.println("Inserting the local date time window (minute) into the queue");
                    ipBasedSlidingWindowCounter.get( ip ).put( currentMinute, 1 );

                    break;

                default:

                    break;

            }




            return true;
        }

    }

    public <T> void deRegisterIp( String ip, Map< String, T> ipBasedTokenBucket ){
        ipBasedTokenBucket.remove( ip );
    }

}
