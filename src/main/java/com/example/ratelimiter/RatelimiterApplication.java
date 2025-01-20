package com.example.ratelimiter;

import com.example.ratelimiter.algorithm.TokenBucket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.example.ratelimiter.algorithm.LeakyBucketMeter;

@SpringBootApplication
@ServletComponentScan
@EnableScheduling
public class RatelimiterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RatelimiterApplication.class, args);
	}

	@Bean
	public TokenBucket tokenBucketAlgorithm(){
		return new TokenBucket(5);
	}

	@Bean
	public LeakyBucketMeter leakyBucketMeterAlgorithm(){
		return new LeakyBucketMeter(10);
	}

}
