package com.example.ratelimiter.filter;

import com.example.ratelimiter.service.LeakyBucketMeterService;
import jakarta.servlet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@ConditionalOnProperty( name = "filter.type", havingValue = "leakybucketmeter")
public class LeakyBucketMeterFilter implements Filter {

    @Autowired
    LeakyBucketMeterService leakyBucketMeterService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            String ipAddress = servletRequest.getRemoteHost();
            System.out.println( "The request ip address is " + ipAddress );

            leakyBucketMeterService.checkValidity( ipAddress );

            System.out.println("The request is within limit and is executed");
        } catch (RuntimeException ex ){
            System.out.println( "TOO MANY REQUESTS TRIGGERED" + ex.getMessage() );

            HttpServletResponse httpServletResponse = ( HttpServletResponse ) servletResponse;

            System.out.println( "The number of requests crossed the expected limits" );
            httpServletResponse.setContentType("text/html");
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.getWriter().write(ex.getMessage());
        }
    }
}
