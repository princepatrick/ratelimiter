package com.example.ratelimiter.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class BucketUtil {


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
