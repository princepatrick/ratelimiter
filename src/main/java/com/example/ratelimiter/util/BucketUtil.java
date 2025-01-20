package com.example.ratelimiter.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class BucketUtil {


    public boolean registerIp( String ip, Map<String, PriorityBlockingQueue<Token>> ipBasedTokenBucket, int capacity, boolean isTokenBucket ){

        System.out.println("Running registerIp() in BucketUtil");

        if( ipBasedTokenBucket.containsKey(ip) ){
//            log.info("The IP Address is already present");
            System.out.println("The IP Address is already present in the TokenBucket");
            return false;
        } else {
//            log.info("The IP Address is being registered");
            System.out.println("The IP Address is being registered");
            PriorityBlockingQueue<Token> tokenQueue = new PriorityBlockingQueue<>( capacity,
                    new Comparator<Token>() {
                        @Override
                        public int compare(Token o1, Token o2) {
                            return o1.getUUID().compareTo(o2.getUUID());
                        }
                    }
            );

            if( isTokenBucket){
                System.out.println("The tokens are added to the queue for the Token Bucket algorithm");
                for( int i=0 ; i< capacity ; i++ ){
                    tokenQueue.add( new Token( UUID.randomUUID().toString(), LocalDateTime.now() ));
                }
            } else {
                System.out.println("Add a token that indicates the process that needs to be processed");
                tokenQueue.add( new Token( UUID.randomUUID().toString(), LocalDateTime.now() ));
            }

            System.out.println("The queue is added to the bucket");
            ipBasedTokenBucket.put( ip, tokenQueue );

            return true;
        }

    }

    public void deRegisterIp( String ip, Map< String, PriorityBlockingQueue<Token>> ipBasedTokenBucket ){
        ipBasedTokenBucket.remove( ip );
    }

}
