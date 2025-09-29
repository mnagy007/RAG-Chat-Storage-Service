package com.ragchat.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter implements Filter {

    private static final int MAX_REQUESTS = 5; // Max requests per 10 seconds
    private static final long TIME_WINDOW_MILLIS = 10000; // 10 seconds

    // In-memory map for single-instance rate limiting
    private final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = httpRequest.getRemoteAddr();

        requestCounts.computeIfAbsent(clientIp, k -> new RequestCounter());
        RequestCounter counter = requestCounts.get(clientIp);

        synchronized (counter) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - counter.lastRequestTime > TIME_WINDOW_MILLIS) {
                counter.count.set(0);
                counter.lastRequestTime = currentTime;
            }

            if (counter.count.incrementAndGet() > MAX_REQUESTS) {
                httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                httpResponse.getWriter().write("Too many requests. Please try again later.");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private static class RequestCounter {
        AtomicInteger count = new AtomicInteger(0);
        long lastRequestTime = System.currentTimeMillis();
    }
}