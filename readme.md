# Distributed Rate Limiter (Java + Spring Boot)

A production-oriented rate limiter built using **Java 17 + Spring Boot**.

This project implements the **Token Bucket algorithm** with a clean, extensible architecture designed for scalability and distributed environments.

---

## 🚀 Project Goals

This project is built as a backend systems engineering exercise focused on:

- Understanding rate limiting algorithms deeply
- Writing thread-safe concurrent code
- Applying clean interface-based design
- Preparing for distributed system scaling
- Practicing production-grade Spring Boot architecture

---

## 🏗 Architecture Overview

Current architecture (Single Node):

```
Client Request
      ↓
Spring REST Controller
      ↓
RateLimiter Interface
      ↓
TokenBucketRateLimiter (In-Memory)
      ↓
ConcurrentHashMap (Per-IP Buckets)
```

Key design principles:

- Interface-driven abstraction (`RateLimiter`)
- Dependency Injection (Spring-managed beans)
- Configuration externalization via `application.yml`
- Fine-grained synchronization (per bucket)
- Thread-safe concurrent storage

---

## 🧠 Implemented Algorithm: Token Bucket

### Core Parameters

- `maxTokens` → Maximum burst capacity
- `refillRate` → Tokens added per second

### Algorithm Flow

1. Each client (IP) has a dedicated bucket.
2. On each request:
    - Refill tokens based on elapsed time.
    - If tokens ≥ 1 → allow request.
    - Otherwise → return HTTP 429.

### Example

If:

```
maxTokens = 5
refillRate = 5 tokens/sec
```

Behavior:

- 5 immediate requests → allowed
- 6th request → rejected
- After 1 second → 5 tokens restored

---

## ⚙ Configuration

Rate limiting parameters are externalized in:

```
src/main/resources/application.yml
```

Example:

```yaml
rate-limiter:
  max-tokens: 5
  refill-rate: 5
```

This allows environment-specific tuning without code changes.

---

## 📦 Current Features

- ✅ Token Bucket implementation
- ✅ Thread-safe in-memory storage (`ConcurrentHashMap`)
- ✅ Per-client IP-based rate limiting
- ✅ HTTP 429 handling
- ✅ Spring Boot dependency injection
- ✅ Configuration via YAML
- ✅ Clean modular architecture

---

## 🧪 Testing Locally

Run application:

```
mvn spring-boot:run
```

Test endpoint:

```
curl http://localhost:8080/api/test
```

Stress test:

```
for i in {1..10}; do curl http://localhost:8080/api/test; done
```

---

## 🔒 Concurrency Strategy

- Uses `ConcurrentHashMap` for thread-safe storage.
- Synchronizes per bucket to prevent race conditions.
- Avoids global locks for better scalability.

---

## 📈 Roadmap (Next Phases)

### Phase 2 — Distributed Rate Limiter

- Redis-backed storage
- Atomic Lua scripting for refill + consume
- Multi-instance support
- Horizontal scalability

### Phase 3 — Advanced Algorithms

- Sliding Window
- Fixed Window
- Hybrid burst-control strategies

### Phase 4 — Production Enhancements

- Metrics (Prometheus integration)
- Observability hooks
- Admin configuration endpoint
- Load testing benchmarks

---

## 🎯 Why This Project Matters

This project is intentionally built beyond a simple demo.

It demonstrates:

- Backend system design thinking
- Concurrency control
- Scalability planning
- Clean architecture principles
- Preparation for distributed systems interviews

---

## 🛠 Tech Stack

- Java 17
- Spring Boot 4
- Maven
- Redis (upcoming phase)
- Docker (planned for distributed setup)

---

## 👨‍💻 Author

Rishu Kumar  
Backend & Systems Engineering Focused

---

> Built as part of an advanced backend engineering practice journey.