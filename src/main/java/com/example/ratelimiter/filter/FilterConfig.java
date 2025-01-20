package com.example.ratelimiter.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.ratelimiter.filter.LeakyBucketMeterFilter;

import javax.servlet.FilterRegistration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<LeakyBucketMeterFilter> leakyBucketMeterFilterFilterRegistration( LeakyBucketMeterFilter filter ){

    }
}
