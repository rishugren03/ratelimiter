# Distributed Rate Limiter (Java + Spring Boot)

A production-oriented rate limiter built using Java and Spring Boot.

This project implements the Token Bucket algorithm with a clean, extensible architecture designed for scalability and distributed environments.

## Purpose

The goal of this project is to deeply understand:

- Rate limiting algorithms (Token Bucket, later Sliding Window / Fixed Window)
- Concurrency and thread safety
- Clean interface-based design
- Scalability considerations
- Distributed coordination using Redis (planned phase)

## Current Features

- Token Bucket algorithm
- Thread-safe in-memory storage
- Per-client rate limiting (IP-based)
- REST endpoint protected with HTTP 429 handling
- Clean modular architecture

## Upcoming Enhancements

- Redis-backed distributed limiter
- Lua scripting for atomic operations
- Sliding Window implementation
- Configurable limits
- Metrics and observability support

---

This project is built as a backend systems engineering exercise focused on performance, correctness, and production-level design.
