package com.example.ratelimiter.filter;

import com.example.ratelimiter.service.TokenBucketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

//import javax.servlet.*; //CODE IS WORKING
//import javax.servlet.Filter; //CODE IS NOT WORKING
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;


//import javax.servlet.Filter;
//import javax.servlet.FilterChain;

@Component
@ConditionalOnProperty(name = "filter.type", havingValue = "token")
@Slf4j
public class TokenBucketFilter implements Filter {

    @Autowired
    TokenBucketService tokenBucketService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try{
            String ipAddress = servletRequest.getRemoteHost();
//            log.info("The address is " + ipAddress );
            System.out.println( "The address is " + ipAddress  );

            //Add service implementation
            tokenBucketService.checkValidity( ipAddress );

//            log.info("The service call is within the rate api range");
            System.out.println("The service call is within the rate api range");
            filterChain.doFilter(servletRequest, servletResponse);
        } catch ( RuntimeException runtimeException ){
//            log.error( "The error message is {}", runtimeException.getMessage() );
            System.out.println("The error message is " + runtimeException.getMessage());
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

//            log.error("The call has exceeded the allowed limits");
            System.out.println("The call has exceeded the allowed limits");
            httpResponse.setContentType("text/html");
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.getWriter().write(runtimeException.getMessage());
        }
    }

    @Override
    public void destroy() {

    }
}
