package com.insightx.security;

// JwtTokenProvider - Utility class for JWT token operations
// Handles token generation, validation, and claims extraction
//
// Responsibilities:
// - Generate JWT access tokens
// - Generate JWT refresh tokens
// - Validate token signature and expiration
// - Extract claims from token (userId, username, email)
// - Check if token is expired
//
// Key Methods:
//
// generateAccessToken(User user): String
// - Create JWT access token for authenticated user
// - Include claims: userId, username, email
// - Set expiration: 1 hour from now
// - Sign with secret key
// - Return token string
//
// generateRefreshToken(User user): String
// - Create JWT refresh token
// - Include minimal claims: userId
// - Set expiration: 7 days from now
// - Sign with same or different secret
// - Return token string
//
// validateToken(String token): boolean
// - Parse token
// - Verify signature using secret key
// - Check expiration
// - Return true if valid, false otherwise
// - Catch and handle all JWT exceptions
//
// getUserIdFromToken(String token): UUID
// - Extract userId claim from token
// - Return as UUID
// - Throw exception if claim missing or invalid
//
// getUsernameFromToken(String token): String
// - Extract username claim
// - Return as String
//
// getEmailFromToken(String token): String
// - Extract email claim
// - Return as String
//
// isTokenExpired(String token): boolean
// - Check if token expiration time has passed
// - Return true if expired
//
// getExpirationDateFromToken(String token): Date
// - Extract expiration claim
// - Return as Date
//
// Configuration:
// - JWT secret: Loaded from environment variable or config
// - Secret should be at least 256 bits (32 chars)
// - Access token expiration: 3600000 ms (1 hour)
// - Refresh token expiration: 604800000 ms (7 days)
//
// Token Structure (JWT):
// Header:
//   { "alg": "HS256", "typ": "JWT" }
// Payload:
//   {
//     "sub": "userId",
//     "username": "john_doe",
//     "email": "john@example.com",
//     "iat": 1234567890,
//     "exp": 1234571490
//   }
// Signature:
//   HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
//
// Dependencies:
// - io.jsonwebtoken (jjwt library)
// - Add to pom.xml: jjwt-api, jjwt-impl, jjwt-jackson
//
// Security Best Practices:
// - Use HS256 (HMAC SHA-256) algorithm
// - Store secret in environment variable (never in code)
// - Use different secrets for dev/staging/production
// - Secret should be complex and random
// - Consider using RS256 (RSA) for production (asymmetric)
//
// Error Handling:
// - Catch ExpiredJwtException (token expired)
// - Catch MalformedJwtException (invalid token format)
// - Catch SignatureException (invalid signature)
// - Catch IllegalArgumentException (empty token)
// - Log errors but don't expose details to client
//
// Testing:
// - Test token generation with user object
// - Test validation with valid/invalid tokens
// - Test validation with expired tokens
// - Test claim extraction
// - Mock secret key for tests
//
// Future Enhancements:
// - Support for token blacklisting (logout)
// - Support for token refresh rotation
// - Support for multiple algorithms
// - Token revocation checking
// - Asymmetric keys (public/private key pair)