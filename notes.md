# Token Bucket Rate Limiter — Complete Notes

## 1️⃣ What Is Token Bucket?

Token Bucket is a rate limiting algorithm that:

- Allows burst traffic up to a maximum capacity
- Refills tokens gradually over time
- Rejects requests when tokens are exhausted

It smooths traffic while allowing short bursts.

---

## 2️⃣ Core Parameters

maxTokens  → Maximum capacity of bucket  
refillRate → Tokens added per second

Example:

maxTokens = 5  
refillRate = 5 tokens/sec

This means:
- You can send 5 requests instantly
- Then average 5 requests per second

---

## 3️⃣ Data Structure Design

Each user (or IP/API key) has its own bucket.

We store:

class Bucket {
double tokens;
long lastRefillTime;
}

Map<String, Bucket> buckets = new ConcurrentHashMap<>();

Key → IP / UserID  
Value → Bucket state

---

## 4️⃣ Core Algorithm Flow

On each request:

1. Get or create bucket
2. Refill tokens based on time passed
3. If tokens >= 1:
   allow request
   decrease tokens by 1
   else:
   reject request

---

## 5️⃣ Refill Logic

tokensToAdd = (currentTime - lastRefillTime) / 1000.0 * refillRate

Example:

refillRate = 5 tokens/sec  
timePassed = 500 ms = 0.5 sec

tokensToAdd = 0.5 × 5 = 2.5 tokens

Important:
bucket.tokens = min(maxTokens, bucket.tokens + tokensToAdd)

We cap at maxTokens to prevent unlimited accumulation.

---

## 6️⃣ Complete Implementation (Single Node)

public class TokenBucketRateLimiter implements RateLimiter {

    private final int maxTokens;
    private final double refillRate;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(int maxTokens, double refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
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

---

## 7️⃣ Example Scenarios

### Scenario 1

maxTokens = 5  
refillRate = 5/sec

User sends 5 instantly → Allowed  
tokens = 0

Wait 0.5 sec  
tokens = 2.5

Send 1 request → Allowed  
tokens = 1.5

---

### Scenario 2

maxTokens = 5  
refillRate = 5/sec

Send 5 instantly → tokens = 0

Wait 3 seconds  
tokensToAdd = 3 × 5 = 15  
tokens = min(5, 15) = 5

Send 10 instantly →  
First 5 allowed  
Next 5 rejected

---

### Scenario 3

maxTokens = 5  
refillRate = 1/sec

Send 5 instantly → tokens = 0

Wait 3 seconds → tokens = 3

Send 4 instantly →  
First 3 allowed  
Last 1 rejected

---

## 8️⃣ Why Use double for tokens?

Because refill may produce fractional tokens.

Example:
0.3 sec × 5/sec = 1.5 tokens

Using double gives smoother control.

---

## 9️⃣ Why synchronized(bucket)?

Without synchronization:

Two threads could both see tokens = 1  
Both allow → tokens become negative

That breaks correctness.

We lock per bucket (fine-grained locking).

---

## 🔟 Advantages

✔ Allows burst traffic  
✔ Smooth rate control  
✔ Simple implementation  
✔ Suitable for distributed systems (with Redis)

---

## 11️⃣ Limitations

✖ In-memory version does not scale across servers  
✖ Requires shared storage (Redis) for distributed systems

---

Next Step:
Replace in-memory Map with Redis + Lua script for atomic operations.