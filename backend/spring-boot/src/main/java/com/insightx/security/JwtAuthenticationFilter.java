package com.insightx.security;

// JwtAuthenticationFilter - Intercepts requests to validate JWT tokens
// Extends OncePerRequestFilter to execute once per request
//
// Responsibilities:
// - Extract JWT token from Authorization header
// - Validate token signature and expiration
// - Extract user information from token
// - Set authentication in SecurityContext
// - Allow request to proceed if valid
// - Return 401 if invalid/missing token
//
// Execution Flow:
// 1. Extract Authorization header from request
// 2. Check if header starts with "Bearer "
// 3. Extract token (remove "Bearer " prefix)
// 4. Validate token using JwtTokenProvider
// 5. If valid, extract userId from token
// 6. Load user from database
// 7. Create Authentication object
// 8. Set in SecurityContext
// 9. Continue filter chain
//
// Token Format:
// - Header: Authorization: Bearer <token>
// - Token is JWT format (header.payload.signature)
//
// Skip Filter For:
// - Public endpoints: /api/auth/login, /api/auth/register, /api/auth/refresh
// - Health check endpoints: /actuator/health
// - Swagger/OpenAPI docs: /v3/api-docs/**, /swagger-ui/**
//
// Error Handling:
// - Missing token: Allow through (SecurityConfig will handle 401)
// - Invalid token: Set 401 response, don't continue filter chain
// - Expired token: Set 401 with specific error message
// - Malformed token: Set 401 with error message
// - Don't throw exceptions, handle gracefully
//
// Token Claims (what's inside the JWT):
// - userId (UUID)
// - username (String)
// - email (String)
// - roles (List<String>) - future feature
// - exp (expiration timestamp)
// - iat (issued at timestamp)
//
// Security Considerations:
// - Always validate token signature
// - Check expiration time
// - Use secure JWT secret (long, random, externalized)
// - Rotate secrets periodically (future enhancement)
// - Log authentication failures for monitoring
// - Rate limit failed authentication attempts
//
// Dependencies:
// - JwtTokenProvider (for token validation and parsing)
// - UserService (to load user details)
//
// Performance:
// - Cache user details to avoid DB lookup on every request
// - Use in-memory cache or Redis
// - Cache key: token hash or userId
// - TTL: same as token expiration
//
// Testing:
// - Test with valid token
// - Test with expired token
// - Test with invalid signature
// - Test with missing token
// - Test with malformed token
// - Mock JwtTokenProvider and UserService