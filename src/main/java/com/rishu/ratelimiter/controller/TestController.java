package com.rishu.ratelimiter.controller;

import com.rishu.ratelimiter.limiter.RateLimiter;
import com.rishu.ratelimiter.limiter.TokenBucketRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {

    private final RateLimiter rateLimiter = new TokenBucketRateLimiter(5, 5);

    @GetMapping("/test")
    public ResponseEntity<String> test(HttpServletRequest request) {
        String ip = request.getRemoteAddr();

        if (!rateLimiter.allowRequest(ip)) {
            return ResponseEntity.status(429).body("Too many requests");
        }

        return ResponseEntity.ok("Request allowed");
    }
}