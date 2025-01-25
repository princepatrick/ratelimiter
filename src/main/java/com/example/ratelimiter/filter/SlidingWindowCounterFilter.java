package com.example.ratelimiter.filter;

import com.example.ratelimiter.service.SlidingWindowCounterService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@ConditionalOnProperty( name = "filter.type", havingValue = "slidingWindowCounter" )
public class SlidingWindowCounterFilter implements Filter {

    @Autowired
    SlidingWindowCounterService slidingWindowCounterService;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        try{
            System.out.println("Attempting to send the service call and check the validity of the request");

            String ipAddress = servletRequest.getRemoteHost();
            slidingWindowCounterService.checkRateLimits( ipAddress );

            System.out.println("The service call is within the limits and has been permitted and his response provided");

        } catch (RuntimeException ex ){
            System.out.println("There are too many requests by this user, hence we have received the error message" + ex.getMessage() );

            HttpServletResponse httpServletResponse = ( HttpServletResponse ) servletResponse;

            System.out.println("We are going to return to the service calling app (Postman) that the service call has failed or hindered due to too many requests ");

            httpServletResponse.setStatus( HttpStatus.TOO_MANY_REQUESTS.value() );
            httpServletResponse.setContentType("html/text");
            httpServletResponse.getWriter().write("Too many requests were initiated by the user resulting in the request");
        }
    }
}
