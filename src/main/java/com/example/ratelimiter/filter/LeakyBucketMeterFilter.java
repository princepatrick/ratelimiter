package com.example.ratelimiter.filter;

import com.example.ratelimiter.service.LeakyBucketMeterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@ConditionalOnProperty(name = "filter.type", havingValue = "leakybucketmeter")
public class LeakyBucketMeterFilter implements Filter {

    public LeakyBucketMeterFilter() {
        System.out.println("LeakyBucketMeterFilter is created");
    }

    @Autowired
    public LeakyBucketMeterService leakyBucketMeterService;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try{
            String ipAddress = servletRequest.getRemoteHost();
            System.out.println( "The request ip address is " + ipAddress );

            leakyBucketMeterService.checkValidity( ipAddress );

            System.out.println("The request is within limit and is executed");
        } catch ( RuntimeException e ){
            System.out.println("TOO MANY REQUESTS TRIGGERED" + e.getMessage() );
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

            System.out.println("The number of requests crossed the expected limits");
            httpResponse.setContentType("text/html");
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.getWriter().write(e.getMessage());
        }
    }

    @Override
    public void destroy() {

    }
}
