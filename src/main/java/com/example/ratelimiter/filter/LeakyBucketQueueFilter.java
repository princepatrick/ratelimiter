package com.example.ratelimiter.filter;

import com.example.ratelimiter.service.LeakyBucketQueueService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

@Component
@ConditionalOnProperty(name = "filter.type", havingValue = "leakybucketqueue")
public class LeakyBucketQueueFilter implements Filter {

    @Autowired
    public LeakyBucketQueueService leakyBucketQueueService;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("LeakyBucketMeterFilter is initialized!!");
    }

    @Value("${filter.type}")
    private String filterType;

    @PostConstruct
    public void checkConfig(){
        System.out.println( "The filter type is" + filterType );
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try{
            String ipAddress = servletRequest.getRemoteHost();
            System.out.println( "The request ip address is " + ipAddress );

            leakyBucketQueueService.checkValidity( ipAddress );

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
