package com.insightx.controllers;

import com.insightx.dto.*;
import com.insightx.entities.User;
import com.insightx.exceptions.RateLimitExceededException;
import com.insightx.services.AuthService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Authentication Controller
 * REST API endpoints for user authentication and registration
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication and account management endpoints")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    // Rate limiting buckets (IP-based)
    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> verificationBuckets = new ConcurrentHashMap<>();

    /**
     * POST /api/auth/register
     * Register new user account
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", 
               description = "Create a new user account and send email verification code")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Username or email already exists"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<com.insightx.dto.ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);
        
        // Rate limiting: 3 registrations per 10 minutes per IP
        if (!checkRateLimit(registerBuckets, clientIp, 3, Duration.ofMinutes(10))) {
            throw new RateLimitExceededException("Too many registration attempts. Please try again later.");
        }

        ApiResponse<String> response = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/auth/verify-email
     * Verify user email with code
     */
    @PostMapping("/verify-email")
    @Operation(summary = "Verify email address", 
               description = "Verify user's email using the 6-digit code sent during registration")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired verification code"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<com.insightx.dto.ApiResponse<String>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);
        
        // Rate limiting: 10 attempts per 5 minutes per IP
        if (!checkRateLimit(verificationBuckets, clientIp, 10, Duration.ofMinutes(5))) {
            throw new RateLimitExceededException("Too many verification attempts. Please try again later.");
        }

        ApiResponse<String> response = authService.verifyEmail(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/resend-verification
     * Resend verification email
     */
    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification code", 
               description = "Resend the email verification code to the user's email address")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Verification email sent"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Email already verified or user not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<com.insightx.dto.ApiResponse<String>> resendVerification(
            @Valid @RequestBody ResendVerificationRequest request,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);
        
        // Rate limiting: 5 resends per hour per IP
        if (!checkRateLimit(verificationBuckets, clientIp, 5, Duration.ofHours(1))) {
            throw new RateLimitExceededException("Too many resend attempts. Please try again later.");
        }

        ApiResponse<String> response = authService.resendVerificationEmail(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/login
     * Login with username/email and password
     */
    @PostMapping("/login")
    @Operation(summary = "User login", 
               description = "Authenticate user with username/email and password, returns JWT tokens")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful, returns access and refresh tokens"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials or email not verified"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);
        
        // Rate limiting: 5 login attempts per minute per IP
        if (!checkRateLimit(loginBuckets, clientIp, 5, Duration.ofMinutes(1))) {
            throw new RateLimitExceededException("Too many login attempts. Please try again later.");
        }

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/refresh
     * Refresh access token using refresh token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", 
               description = "Generate new access and refresh tokens using a valid refresh token")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<AuthResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/logout
     * Logout user (blacklist current token)
     */
    @PostMapping("/logout")
    @Operation(summary = "User logout", 
               description = "Invalidate current access token by adding it to the blacklist")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logged out successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No valid token provided")
    })
    public ResponseEntity<com.insightx.dto.ApiResponse<String>> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        
        if (token == null) {
            return ResponseEntity.ok(ApiResponse.success("Already logged out"));
        }

        ApiResponse<String> response = authService.logout(token);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/me
     * Get current authenticated user info
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", 
               description = "Retrieve information about the currently authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated or invalid token")
    })
    public ResponseEntity<AuthResponse.UserDTO> getCurrentUser(Authentication authentication) {
        // Check if authentication is null or not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
        
        // Extract user from authentication
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
        
        User user = (User) principal;

        AuthResponse.UserDTO userDTO = AuthResponse.UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .region(user.getRegion())
                .emailVerified(user.getEmailVerified())
                .build();

        return ResponseEntity.ok(userDTO);
    }

    // ========================================
    // Helper Methods
    // ========================================

    /**
     * Check rate limit for IP address
     */
    private boolean checkRateLimit(Map<String, Bucket> buckets, String key, int capacity, Duration refillDuration) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket(capacity, refillDuration));
        return bucket.tryConsume(1);
    }

    /**
     * Create rate limiting bucket
     */
    private Bucket createBucket(int capacity, Duration refillDuration) {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(capacity, refillDuration));
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Extract client IP address from request
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
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