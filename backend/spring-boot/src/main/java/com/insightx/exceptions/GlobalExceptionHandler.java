package com.insightx.exceptions;

import com.insightx.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Global Exception Handler
 * Centralizes exception handling across all controllers
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ========================================
    // Authentication & Authorization (401)
    // ========================================

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex, WebRequest request) {
        log.warn("Invalid credentials attempt: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid credentials",
                "The username/email or password is incorrect",
                request
        );
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpired(
            TokenExpiredException ex, WebRequest request) {
        log.warn("Token expired: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Token expired",
                "Your session has expired. Please login again",
                request
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex, WebRequest request) {
        log.warn("Unauthorized access: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotVerified(
            EmailNotVerifiedException ex, WebRequest request) {
        log.warn("Email not verified: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Email not verified",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidVerificationCode(
            InvalidVerificationCodeException ex, WebRequest request) {
        log.warn("Invalid verification code: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid verification code",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(VerificationCodeExpiredException.class)
    public ResponseEntity<ErrorResponse> handleVerificationCodeExpired(
            VerificationCodeExpiredException ex, WebRequest request) {
        log.warn("Verification code expired: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Verification code expired",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(TooManyVerificationAttemptsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyVerificationAttempts(
            TooManyVerificationAttemptsException ex, WebRequest request) {
        log.warn("Too many verification attempts: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS,
                "Too many attempts",
                ex.getMessage(),
                request
        );
    }

    // ========================================
    // Resource Not Found (404)
    // ========================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                ex.getMessage(),
                request
        );
    }

    // ========================================
    // Conflict (409)
    // ========================================

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException ex, WebRequest request) {
        log.warn("Conflict: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Resource conflict",
                ex.getMessage(),
                request
        );
    }

    // ========================================
    // Validation Errors (400)
    // ========================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {
        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.add(ErrorResponse.ValidationError.builder()
                    .field(fieldName)
                    .message(errorMessage)
                    .build());
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation failed")
                .message("Request validation failed")
                .details(validationErrors)
                .path(extractPath(request))
                .build();

        log.warn("Validation errors: {}", validationErrors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex, WebRequest request) {
        log.warn("Validation error: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation error",
                ex.getMessage(),
                request
        );
    }

    // ========================================
    // Rate Limiting (429)
    // ========================================

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(
            RateLimitExceededException ex, WebRequest request) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS,
                "Rate limit exceeded",
                ex.getMessage(),
                request
        );
    }

    // ========================================
    // Service Unavailable (503)
    // ========================================

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(
            ServiceException ex, WebRequest request) {
        log.error("Service error: {}", ex.getMessage(), ex);
        return buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service temporarily unavailable",
                "An external service is currently unavailable. Please try again later",
                request
        );
    }

    // ========================================
    // Generic Exception (500)
    // ========================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                "An unexpected error occurred. Please try again later",
                request
        );
    }

    // ========================================
    // Helper Methods
    // ========================================

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status, String error, String message, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
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