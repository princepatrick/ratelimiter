package com.example.ratelimiter.util;

/*
* The ENUM is used to define the various rate limiting algorithms that are available for implementation.
* The algorithm name is used to differentiate the code flow for the registration of the ip address but
* could further be used for any other purpose
* */
public enum RateLimitingAlgorithm {

    TOKEN_BUCKET, LEAKY_BUCKET_QUEUE, LEAKY_BUCKET_METER, FIXED_WINDOW_COUNTER, SLIDING_WINDOW_LOG, SLIDING_WINDOW_COUNTER;

}
