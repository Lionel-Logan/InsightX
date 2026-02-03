package com.insightx.exceptions;

// Custom Exception Classes for InsightX
// These exceptions are thrown by service layer and caught by GlobalExceptionHandler
//
// Exception Hierarchy:
//
// RuntimeException (Java base)
//   ├── InsightXException (our base exception)
//       ├── AuthenticationException
//       │   ├── InvalidCredentialsException
//       │   ├── TokenExpiredException
//       │   └── UnauthorizedException
//       ├── AuthorizationException
//       │   └── UnauthorizedAccessException
//       ├── ResourceNotFoundException
//       │   ├── UserNotFoundException
//       │   ├── MediaNotFoundException
//       │   ├── ReviewNotFoundException
//       │   ├── RatingNotFoundException
//       │   ├── BookmarkNotFoundException
//       │   └── PreferenceNotFoundException
//       ├── ConflictException
//       │   ├── UsernameAlreadyExistsException
//       │   ├── EmailAlreadyExistsException
//       │   ├── ReviewAlreadyExistsException
//       │   └── BookmarkAlreadyExistsException
//       ├── ValidationException
//       │   ├── InvalidRatingException
//       │   ├── InvalidPreferenceException
//       │   └── InvalidMediaTypeException
//       ├── BusinessException
//       │   └── InsufficientDataException
//       └── ServiceException
//           ├── FastAPIServiceException
//           └── RedisConnectionException
//
// Each exception should:
// - Extend appropriate parent exception
// - Have constructor accepting message
// - Have constructor accepting message and cause
// - Include any relevant context (e.g., userId, mediaId)
//
// Example Implementation Pattern:
//
// public class UserNotFoundException extends ResourceNotFoundException {
//     private final UUID userId;
//     
//     public UserNotFoundException(UUID userId) {
//         super("User not found with ID: " + userId);
//         this.userId = userId;
//     }
//     
//     public UUID getUserId() {
//         return userId;
//     }
// }
//
// Base Exception (InsightXException):
// - Abstract base class for all custom exceptions
// - Extends RuntimeException (unchecked)
// - Contains common fields (timestamp, errorCode)
//
// Error Codes (optional):
// - AUTH_001: Invalid credentials
// - AUTH_002: Token expired
// - AUTH_003: Unauthorized access
// - RES_001: User not found
// - RES_002: Media not found
// - VAL_001: Invalid rating
// - VAL_002: Validation failed
// - BUS_001: Insufficient data
// - SVC_001: External service error
//
// Usage in Services:
// - Throw specific exceptions, not generic ones
// - Include context (IDs, names) in exception message
// - Use meaningful error messages
// - Don't throw for normal flow (e.g., Optional.empty() is better than exception)
//
// Benefits:
// - Type-safe exception handling
// - Clear error categorization
// - Consistent error responses
// - Easy to add new exception types
// - Supports internationalization (error codes can be translated)
//
// File Organization:
// - Create separate .java files for each exception class
// - Or put related exceptions in same file
// - This file serves as documentation of all exceptions