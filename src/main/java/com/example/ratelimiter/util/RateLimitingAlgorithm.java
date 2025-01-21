package com.example.ratelimiter.util;

public enum RateLimitingAlgorithm {

    TOKEN_BUCKET, LEAKY_BUCKET_QUEUE, LEAKY_BUCKET_METER, FIXED_WINDOW_COUNTER, SLIDING_WINDOW_LOG, SLIDING_WINDOW_COUNTER;

}
