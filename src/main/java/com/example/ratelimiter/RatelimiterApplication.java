package com.example.ratelimiter;

import com.example.ratelimiter.algorithm.LeakyBucketMeter;
import com.example.ratelimiter.algorithm.TokenBucket;
import com.example.ratelimiter.util.RateLimitingAlgorithm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.example.ratelimiter.algorithm.LeakyBucketQueue;
import com.example.ratelimiter.algorithm.FixedWindowCounter;

@SpringBootApplication
@ComponentScan({"com.example.ratelimiter"})
@EnableScheduling
public class RatelimiterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RatelimiterApplication.class, args);
	}

	@Bean
	public TokenBucket tokenBucketAlgorithm(){
		return new TokenBucket(5, RateLimitingAlgorithm.TOKEN_BUCKET );
	}

	@Bean
	public LeakyBucketQueue leakyBucketQueueAlgorithm(){
		return new LeakyBucketQueue(10, RateLimitingAlgorithm.LEAKY_BUCKET_QUEUE );
	}

	@Bean
	public LeakyBucketMeter leakyBucketMeterAlgorithm(){
		return new LeakyBucketMeter( 5, RateLimitingAlgorithm.LEAKY_BUCKET_METER );
	}

	@Bean
	public FixedWindowCounter fixedWindowCounterAlgorithm(){
		return new FixedWindowCounter( 3, RateLimitingAlgorithm.FIXED_WINDOW_COUNTER );
	}

}
