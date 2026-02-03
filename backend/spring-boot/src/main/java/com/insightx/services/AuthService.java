package com.insightx.services;

// AuthService - Handles authentication and token management
//
// Responsibilities:
// - User registration
// - User login (username/email + password)
// - JWT token generation
// - JWT token validation
// - Refresh token handling
// - Password encryption and verification
//
// Key Methods:
// 
// registerUser(RegisterRequest request): AuthResponse
// - Validate username and email uniqueness
// - Hash password using BCrypt
// - Create new User entity
// - Save to database
// - Generate JWT token
// - Return token + user info
//
// login(LoginRequest request): AuthResponse
// - Find user by username or email
// - Verify password using BCrypt
// - Generate JWT token
// - Return token + user info
//
// refreshToken(String refreshToken): AuthResponse
// - Validate refresh token
// - Generate new access token
// - Return new token
//
// validateToken(String token): boolean
// - Parse and validate JWT
// - Check expiration
// - Verify signature
// - Return true if valid
//
// getUserFromToken(String token): User
// - Extract user ID from token
// - Load user from database
// - Return user entity
//
// Dependencies:
// - UserRepository (to persist users)
// - PasswordEncoder (BCrypt)
// - JwtTokenProvider (custom utility for token operations)
//
// Security Considerations:
// - Store passwords as BCrypt hashes (never plain text)
// - Use strong JWT secret (externalized in config)
// - Set appropriate token expiration (access: 1 hour, refresh: 7 days)
// - Rate limit login attempts (consider using bucket4j)
// - Log failed login attempts for security monitoring
//
// Error Handling:
// - Throw custom exceptions: UsernameAlreadyExistsException, InvalidCredentialsException
// - Return meaningful error messages without exposing security details
//
// Transaction Management:
// - Mark methods with @Transactional where needed
// - Registration should be transactional