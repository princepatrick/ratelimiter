package com.example.ratelimiter.filter;


import com.example.ratelimiter.algorithm.SlidingWindowLog;
import com.example.ratelimiter.service.SlidingWindowLogService;
import jakarta.servlet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * Sliding Window Log Filter implementation - checks the rate level limit validity, it processes if rate limit is available
 * and throws the runtimeexception if the rate level limit exceeds
 * */
@Component
@ConditionalOnProperty( name = "filter.type", havingValue = "slidingWindowLog")
public class SlidingWindowLogFilter implements Filter {

    @Autowired
    SlidingWindowLogService slidingWindowLogService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            System.out.println("Attempting the service call for Sliding Window Log algorithm!!");

            String ipAddress = servletRequest.getRemoteHost();
            slidingWindowLogService.checkRateLimits( ipAddress );
            System.out.println("The service is available and has processed the request");

            filterChain.doFilter( servletRequest, servletResponse );
        } catch ( RuntimeException ex ){
            System.out.println("There are too many requests at the API calls" + ex.getMessage());

            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.setContentType("html/text");
            httpServletResponse.getWriter().write(ex.getMessage());
        }
    }
}
