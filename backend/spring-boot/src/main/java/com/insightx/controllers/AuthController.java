package com.insightx.controllers;

// AuthController - REST API endpoints for authentication
// Base path: /api/auth
//
// Endpoints:
//
// POST /api/auth/register
// - Register new user account
// - Request body: RegisterRequest (username, email, password, region)
// - Response: AuthResponse (token, user info)
// - Status: 201 Created on success
// - Errors: 400 if username/email exists, 400 if validation fails
//
// POST /api/auth/login
// - Login with username/email and password
// - Request body: LoginRequest (usernameOrEmail, password)
// - Response: AuthResponse (accessToken, refreshToken, user info)
// - Status: 200 OK on success
// - Errors: 401 if credentials invalid, 400 if validation fails
//
// POST /api/auth/refresh
// - Refresh access token using refresh token
// - Request body: RefreshTokenRequest (refreshToken)
// - Response: AuthResponse (new accessToken, refreshToken)
// - Status: 200 OK on success
// - Errors: 401 if refresh token invalid/expired
//
// POST /api/auth/logout
// - Logout user (invalidate tokens if implemented)
// - Request header: Authorization Bearer token
// - Response: 200 OK with success message
// - Status: 200 OK
//
// GET /api/auth/me
// - Get current authenticated user info
// - Request header: Authorization Bearer token
// - Response: UserProfileDTO
// - Status: 200 OK
// - Errors: 401 if not authenticated
//
// Dependencies:
// - AuthService
// - UserService
//
// Security:
// - /register and /login are public (no auth required)
// - /refresh is public
// - /logout and /me require valid JWT
//
// Validation:
// - Use @Valid annotation on request bodies
// - Validate email format, password strength, username format
// - Return 400 Bad Request with error details
//
// Response Format:
// - Success: { "success": true, "data": {...}, "message": "..." }
// - Error: { "success": false, "error": "...", "details": [...] }
//
// Rate Limiting:
// - Consider adding rate limiting to login endpoint
// - Prevent brute force attacks
// - Use bucket4j or similar library
//
// CORS:
// - Configured globally in CorsConfig
// - Allow credentials for cookie-based auth (if used)
//
// Error Handling:
// - UsernameAlreadyExistsException -> 400
// - EmailAlreadyExistsException -> 400
// - InvalidCredentialsException -> 401
// - ValidationException -> 400
// - All handled by global @ControllerAdvice