package com.example.ratelimiter;

import com.example.ratelimiter.algorithm.*;
import com.example.ratelimiter.util.RateLimitingAlgorithm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan({"com.example.ratelimiter"})
@EnableScheduling
public class RatelimiterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RatelimiterApplication.class, args);
	}

	@Bean
	public TokenBucket tokenBucketAlgorithm(){
		return new TokenBucket( 5, RateLimitingAlgorithm.TOKEN_BUCKET );
	}

	@Bean
	public LeakyBucketQueue leakyBucketQueueAlgorithm(){
		return new LeakyBucketQueue( 10, RateLimitingAlgorithm.LEAKY_BUCKET_QUEUE );
	}

	@Bean
	public LeakyBucketMeter leakyBucketMeterAlgorithm(){
		return new LeakyBucketMeter( 5, RateLimitingAlgorithm.LEAKY_BUCKET_METER );
	}

	@Bean
	public FixedWindowCounter fixedWindowCounterAlgorithm(){
		return new FixedWindowCounter( 3, RateLimitingAlgorithm.FIXED_WINDOW_COUNTER );
	}

	@Bean
	public SlidingWindowLog slidingWindowLogAlgorithm(){
		return new SlidingWindowLog( 5, 60, RateLimitingAlgorithm.SLIDING_WINDOW_LOG );
	}

	@Bean
	public SlidingWindowCounter slidingWindowCounterAlgorithm(){
		return new SlidingWindowCounter( 2, RateLimitingAlgorithm.SLIDING_WINDOW_COUNTER );
	}

}
