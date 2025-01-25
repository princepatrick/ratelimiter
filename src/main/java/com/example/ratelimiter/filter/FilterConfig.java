package com.example.ratelimiter.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<SlidingWindowLogFilter> leakyBucketMeterFilterRegistration(SlidingWindowLogFilter filter) {
        FilterRegistrationBean<SlidingWindowLogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("slidingWindowLog");
        return registration;
    }
}
