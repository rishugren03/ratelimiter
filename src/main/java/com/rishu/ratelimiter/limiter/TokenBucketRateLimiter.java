package com.rishu.ratelimiter.limiter;

import com.rishu.ratelimiter.config.RateLimiterConfig;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBucketRateLimiter implements RateLimiter {

    private final int maxTokens;
    private final double refillRate;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

//constructor
    public TokenBucketRateLimiter(RateLimiterConfig config) {
        this.maxTokens = config.getMaxTokens();
        this.refillRate = config.getRefillRate();
    }

    @Override
    public boolean allowRequest(String key) {
        Bucket bucket = buckets.computeIfAbsent(
                key,
                k -> new Bucket(maxTokens, System.currentTimeMillis())
        );

        synchronized (bucket) {
            refill(bucket);

            if (bucket.tokens >= 1) {
                bucket.tokens -= 1;
                return true;
            }

            return false;
        }
    }

    private void refill(Bucket bucket) {
        long now = System.currentTimeMillis();
        double tokensToAdd = (now - bucket.lastRefillTime) / 1000.0 * refillRate;

        if (tokensToAdd > 0) {
            bucket.tokens = Math.min(maxTokens, bucket.tokens + tokensToAdd);
            bucket.lastRefillTime = now;
        }
    }

    private static class Bucket {
        double tokens;
        long lastRefillTime;

        Bucket(double tokens, long lastRefillTime) {
            this.tokens = tokens;
            this.lastRefillTime = lastRefillTime;
        }
    }
}