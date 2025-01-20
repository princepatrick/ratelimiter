package com.example.ratelimiter.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.ratelimiter.filter.LeakyBucketMeterFilter;

import javax.servlet.FilterRegistration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<LeakyBucketMeterFilter> leakyBucketMeterFilterRegistration(LeakyBucketMeterFilter filter) {
        FilterRegistrationBean<LeakyBucketMeterFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("leakyBucketMeterFilter");
//        registration.setOrder(1);
        return registration;
    }
}
