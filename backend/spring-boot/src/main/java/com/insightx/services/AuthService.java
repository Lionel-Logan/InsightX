package com.insightx.services;

import com.insightx.dto.*;
import com.insightx.entities.User;
import com.insightx.exceptions.*;
import com.insightx.repositories.UserRepository;
import com.insightx.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Authentication Service
 * Handles user registration, login, email verification, and token management
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final int VERIFICATION_CODE_LENGTH = 6;
    private static final int MAX_VERIFICATION_ATTEMPTS = 5;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EmailService emailService;

    @Value("${spring.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${spring.security.jwt.verification-token-expiration:86400000}") // 24 hours default
    private long verificationTokenExpiration;

    /**
     * Register new user and send verification email
     */
    @Transactional
    public ApiResponse<String> registerUser(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .region(request.getRegion() != null ? request.getRegion() : "US")
                .active(true)
                .emailVerified(false)
                .role("USER")
                .verificationAttempts(0)
                .build();

        // Generate verification code
        String verificationCode = generateVerificationCode();
        user.setVerificationToken(verificationCode);
        user.setVerificationTokenExpiry(LocalDateTime.now().plus(Duration.ofMillis(verificationTokenExpiration)));

        // Save user
        userRepository.save(user);
        log.info("User created successfully: {}", user.getId());

        // Send verification email
        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), verificationCode);
        } catch (Exception e) {
            log.error("Failed to send verification email: {}", e.getMessage());
            // Don't fail registration if email fails
        }

        return ApiResponse.success(
                "Registration successful. Please check your email for verification code.",
                user.getEmail()
        );
    }

    /**
     * Verify user email with code
     */
    @Transactional
    public ApiResponse<String> verifyEmail(VerifyEmailRequest request) {
        log.info("Verifying email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        // Check if already verified
        if (user.getEmailVerified()) {
            return ApiResponse.success("Email already verified");
        }

        // Check attempts
        if (user.getVerificationAttempts() >= MAX_VERIFICATION_ATTEMPTS) {
            throw new TooManyVerificationAttemptsException(
                    "Maximum verification attempts exceeded. Please request a new code.");
        }

        // Check if code expired
        if (user.getVerificationTokenExpiry() == null || 
            LocalDateTime.now().isAfter(user.getVerificationTokenExpiry())) {
            throw new VerificationCodeExpiredException("Verification code has expired. Please request a new one.");
        }

        // Verify code
        if (!request.getCode().equals(user.getVerificationToken())) {
            user.setVerificationAttempts(user.getVerificationAttempts() + 1);
            userRepository.save(user);
            throw new InvalidVerificationCodeException("Invalid verification code");
        }

        // Mark as verified
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        user.setVerificationAttempts(0);
        userRepository.save(user);

        log.info("Email verified successfully for user: {}", user.getId());

        // Send welcome email
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage());
        }

        return ApiResponse.success("Email verified successfully. You can now log in.");
    }

    /**
     * Resend verification email
     */
    @Transactional
    public ApiResponse<String> resendVerificationEmail(ResendVerificationRequest request) {
        log.info("Resending verification email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        // Check if already verified
        if (user.getEmailVerified()) {
            return ApiResponse.success("Email already verified");
        }

        // Generate new code
        String verificationCode = generateVerificationCode();
        user.setVerificationToken(verificationCode);
        user.setVerificationTokenExpiry(LocalDateTime.now().plus(Duration.ofMillis(verificationTokenExpiration)));
        user.setVerificationAttempts(0); // Reset attempts
        userRepository.save(user);

        // Send email
        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), verificationCode);
        } catch (Exception e) {
            throw new EmailServiceException("Failed to send verification email", e);
        }

        return ApiResponse.success("Verification email sent successfully");
    }

    /**
     * Login user and return JWT tokens
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt: {}", request.getUsernameOrEmail());

        // Find user by username or email
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username/email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid username/email or password");
        }

        // Check if user is active
        if (!user.getActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        // Check if email is verified
        if (!user.getEmailVerified()) {
            throw new EmailNotVerifiedException(
                    "Please verify your email before logging in. Check your inbox for verification code.");
        }

        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        log.info("User logged in successfully: {}", user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration / 1000) // Convert to seconds
                .user(AuthResponse.UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .region(user.getRegion())
                        .emailVerified(user.getEmailVerified())
                        .build())
                .build();
    }

    /**
     * Refresh access token using refresh token
     */
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing access token");

        // Validate refresh token
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new TokenExpiredException("Refresh token is invalid or expired");
        }

        // Get user from token
        var userId = jwtTokenProvider.getUserIdFromToken(request.getRefreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Check if user is active
        if (!user.getActive() || !user.getEmailVerified()) {
            throw new UnauthorizedException("Account is not accessible");
        }

        // Generate new tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        log.info("Tokens refreshed successfully for user: {}", user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration / 1000)
                .user(AuthResponse.UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .region(user.getRegion())
                        .emailVerified(user.getEmailVerified())
                        .build())
                .build();
    }

    /**
     * Logout user (blacklist token)
     */
    public ApiResponse<String> logout(String token) {
        log.info("Logging out user");

        try {
            jwtTokenProvider.blacklistToken(token);
            return ApiResponse.success("Logged out successfully");
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            throw new ServiceException("Failed to logout", e);
        }
    }

    /**
     * Generate 6-digit verification code
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6-digit code
        return String.valueOf(code);
    }
}
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