package com.example.ratelimiter.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<FixedWindowCounterFilter> leakyBucketMeterFilterRegistration(FixedWindowCounterFilter filter) {
        FilterRegistrationBean<FixedWindowCounterFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("fixedWindowCounter");
//        registration.setOrder(1);
        return registration;
    }
}
