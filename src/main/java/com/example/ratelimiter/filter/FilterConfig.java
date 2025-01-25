package com.example.ratelimiter.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<SlidingWindowCounterFilter> leakyBucketMeterFilterRegistration(SlidingWindowCounterFilter filter) {
        FilterRegistrationBean<SlidingWindowCounterFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("slidingWindowLog");
        return registration;
    }
}
