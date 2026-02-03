package com.insightx.exceptions;

// GlobalExceptionHandler - Centralized exception handling
// Uses @ControllerAdvice to handle exceptions across all controllers
//
// Responsibilities:
// - Catch all exceptions thrown by controllers
// - Convert exceptions to appropriate HTTP responses
// - Return consistent error response format
// - Log errors for monitoring
// - Hide sensitive information from clients
//
// Exception Mappings:
//
// Authentication & Authorization:
// - InvalidCredentialsException -> 401 Unauthorized
// - UnauthorizedException -> 401 Unauthorized
// - TokenExpiredException -> 401 Unauthorized
// - UnauthorizedAccessException -> 403 Forbidden
//
// Validation:
// - MethodArgumentNotValidException -> 400 Bad Request
// - ValidationException -> 400 Bad Request
// - ConstraintViolationException -> 400 Bad Request
//
// Resource Not Found:
// - UserNotFoundException -> 404 Not Found
// - MediaNotFoundException -> 404 Not Found
// - ReviewNotFoundException -> 404 Not Found
// - RatingNotFoundException -> 404 Not Found
// - PreferenceNotFoundException -> 404 Not Found
//
// Business Logic:
// - UsernameAlreadyExistsException -> 409 Conflict
// - EmailAlreadyExistsException -> 409 Conflict
// - ReviewAlreadyExistsException -> 409 Conflict
// - BookmarkAlreadyExistsException -> 409 Conflict
// - InsufficientDataException -> 400 Bad Request
//
// Service Errors:
// - FastAPIServiceException -> 503 Service Unavailable
// - RedisConnectionException -> 503 Service Unavailable (but app continues)
//
// Generic:
// - Exception (catch-all) -> 500 Internal Server Error
//
// Error Response Format:
// {
//   "timestamp": "2024-02-03T10:30:45.123Z",
//   "status": 400,
//   "error": "Bad Request",
//   "message": "Validation failed",
//   "details": [
//     {
//       "field": "username",
//       "message": "Username must be between 3 and 50 characters"
//     },
//     {
//       "field": "email",
//       "message": "Invalid email format"
//     }
//   ],
//   "path": "/api/users/profile"
// }
//
// Methods to Implement:
//
// handleInvalidCredentials(InvalidCredentialsException ex, WebRequest request): ResponseEntity<ErrorResponse>
// handleUnauthorized(UnauthorizedException ex, WebRequest request): ResponseEntity<ErrorResponse>
// handleNotFound(NotFoundException ex, WebRequest request): ResponseEntity<ErrorResponse>
// handleConflict(ConflictException ex, WebRequest request): ResponseEntity<ErrorResponse>
// handleValidation(MethodArgumentNotValidException ex, WebRequest request): ResponseEntity<ErrorResponse>
// handleServiceUnavailable(ServiceException ex, WebRequest request): ResponseEntity<ErrorResponse>
// handleGenericException(Exception ex, WebRequest request): ResponseEntity<ErrorResponse>
//
// Logging Strategy:
// - WARN level for client errors (4xx)
// - ERROR level for server errors (5xx)
// - Include request path, method, and user ID if available
// - Don't log sensitive information (passwords, tokens)
// - Use structured logging (JSON format recommended)
//
// Security Considerations:
// - Never expose stack traces to clients in production
// - Hide database errors (don't reveal schema)
// - Sanitize error messages
// - Generic messages for authentication failures (don't say "user not found" vs "wrong password")
//
// Environment-Aware Behavior:
// - Development: Include stack traces in response
// - Production: Generic error messages, log details server-side
// - Use Spring profiles to control behavior
//
// Dependencies:
// - Custom exception classes (create in same package)
// - ErrorResponse DTO
// - Logger (SLF4J)
//
// Testing:
// - Test each exception type
// - Verify correct HTTP status codes
// - Verify response format
// - Test with different environments (dev vs prod)