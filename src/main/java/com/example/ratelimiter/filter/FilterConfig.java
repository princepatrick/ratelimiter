package com.example.ratelimiter.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
*A class that is used to implement the specific filter for the application.
* After changing the filter name in the application.properties based on the following options:
* [token, leakybucketqueue, leakybucketmeter, fixedWindowCounter, slidingWindowLog, slidingWindowCounter]
* We need to specify the apt Filter class in the FilterRegistrationBean method
* The filter registration sets up the correct bean and this is compared with all the filter
* implementation and picks the one that matches it.
* */
@Configuration
public class FilterConfig {

    /*
    * @param Filter Implementation
    * @Return FilterRegistrationBean of the filter type
    * The Filter sets the url patterns and the name of the filter patterns
    * */
    @Bean
    public FilterRegistrationBean<SlidingWindowCounterFilter> slidingWindowCounterFilterRegistration(SlidingWindowCounterFilter filter) {
        FilterRegistrationBean<SlidingWindowCounterFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("slidingWindowLog");
        return registration;
    }
}
