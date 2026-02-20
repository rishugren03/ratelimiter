package com.rishu.ratelimiter.controller;

import com.rishu.ratelimiter.limiter.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {

    private final RateLimiter rateLimiter;

    public TestController(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(HttpServletRequest request) {

        String ip = request.getRemoteAddr();

        if (!rateLimiter.allowRequest(ip)) {
            return ResponseEntity.status(429).body("Too many requests");
        }

        return ResponseEntity.ok("Request allowed");
    }
}