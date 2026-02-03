package com.insightx.config;

// Redis Configuration for Caching Layer
// Configures Redis as a non-critical performance enhancement layer
//
// Key Responsibilities:
// - Configure Redis connection factory
// - Set up Redis template with serialization
// - Configure cache manager
// - Define TTL strategies for different cache types
// - Implement graceful degradation (app works without Redis)
//
// Cached Data Types with TTLs:
// - External API responses: 15 minutes
// - Watch provider lookups: 1 hour
// - Recommendation results: 30 minutes
// - AI explanations: 1 hour
// - Media metadata: 2 hours
//
// Cache Key Patterns:
// - media:movie:{id}
// - media:book:{id}
// - media:game:{id}
// - recommendations:user:{userId}
// - providers:{region}:{mediaId}
// - ai:explanation:{mediaId}
//
// Fault Tolerance:
// - Catch all Redis exceptions
// - Log errors but don't throw
// - Fall back to database/API on cache miss
// - Application must work even if Redis is down