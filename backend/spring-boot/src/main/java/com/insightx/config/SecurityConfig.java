package com.insightx.config;

// Security Configuration for InsightX
// Configures Spring Security with JWT-based authentication
//
// Key Responsibilities:
// - Configure security filter chain
// - Define which endpoints are public vs protected
// - Set up JWT authentication filter
// - Configure CORS for Flutter client
// - Disable CSRF (stateless REST API)
// - Configure password encoder (BCrypt)
//
// Public Endpoints (no authentication required):
// - POST /api/auth/register
// - POST /api/auth/login
// - POST /api/auth/refresh-token
//
// Protected Endpoints (JWT required):
// - All other /api/* endpoints
//
// Security Flow:
// 1. Client sends JWT in Authorization header (Bearer token)
// 2. JwtAuthenticationFilter extracts and validates token
// 3. If valid, sets authentication in SecurityContext
// 4. Request proceeds to controller
// 5. If invalid/missing, returns 401 Unauthorized