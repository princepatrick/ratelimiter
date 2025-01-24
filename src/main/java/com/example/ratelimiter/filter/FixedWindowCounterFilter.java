package com.example.ratelimiter.filter;

import com.example.ratelimiter.service.FixedWindowCounterService;
import jakarta.servlet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@ConditionalOnProperty( name = "filter.type", havingValue = "fixedWindowCounter")
public class FixedWindowCounterFilter implements Filter {

    @Autowired
    FixedWindowCounterService fixedWindowCounterService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try{
            System.out.println("Attempting to register the ip address");
            String ipAddress = servletRequest.getRemoteHost();

            fixedWindowCounterService.checkRateLimits( ipAddress );

            System.out.println("The service call is successful");
            filterChain.doFilter(servletRequest, servletResponse);
        } catch( RuntimeException ex ){
            System.out.println("Too many requests have been called and we are facing the exception message: " + ex.getMessage());

            if( servletResponse instanceof HttpServletResponse ){
                HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

                httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                httpServletResponse.setContentType("text/html");
                httpServletResponse.getWriter().write("We are receiving the exception message " + ex.getMessage() );
            } else {
                System.out.println("The servletResponse is not of the type HttpServletResponse ");
            }

        }
    }
}
