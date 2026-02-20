package com.rishu.ratelimiter.limiter;

public interface RateLimiter {
    boolean allowRequest(String key);
}