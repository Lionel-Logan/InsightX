package com.insightx.security;

import com.insightx.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * JWT Token Provider - Handles JWT token generation, validation, and parsing
 * 
 * Features:
 * - Generate access tokens (activity-based expiration)
 * - Generate refresh tokens (30 days max)
 * - Validate tokens (signature + expiration)
 * - Extract user claims from tokens
 * - Token blacklisting (logout support)
 * - Redis caching for performance
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    private static final String LAST_ACTIVITY_PREFIX = "token:activity:";

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final RedisTemplate<String, Object> redisTemplate;

    public JwtTokenProvider(
            @Value("${spring.security.jwt.secret}") String secret,
            @Value("${spring.security.jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${spring.security.jwt.refresh-token-expiration}") long refreshTokenExpiration,
            RedisTemplate<String, Object> redisTemplate) {
        
        // Ensure secret is at least 256 bits (32 characters)
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters (256 bits)");
        }
        
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Generate JWT access token for authenticated user
     * Includes activity-based expiration tracking
     */
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(accessTokenExpiration);

        String token = Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .claim("type", "access")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        // Track last activity in Redis for activity-based expiration
        updateLastActivity(user.getId());

        log.debug("Generated access token for user: {}", user.getUsername());
        return token;
    }

    /**
     * Generate JWT refresh token
     * Max lifetime: 30 days, subject to activity-based expiration
     */
    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(refreshTokenExpiration, ChronoUnit.MILLIS);

        String token = Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("type", "refresh")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        log.debug("Generated refresh token for user: {}", user.getUsername());
        return token;
    }

    /**
     * Validate token signature and expiration
     * Also checks if token is blacklisted and if activity-based expiration applies
     */
    public boolean validateToken(String token) {
        try {
            // Check if token is blacklisted
            if (isTokenBlacklisted(token)) {
                log.warn("Token is blacklisted");
                return false;
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // For access tokens, check activity-based expiration
            String tokenType = claims.get("type", String.class);
            if ("access".equals(tokenType)) {
                UUID userId = UUID.fromString(claims.getSubject());
                if (!isUserActive(userId)) {
                    log.warn("User {} inactive beyond activity window", userId);
                    return false;
                }
                // Update activity on successful validation
                updateLastActivity(userId);
            }

            return true;
        } catch (ExpiredJwtException e) {
            log.debug("Token expired: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("Invalid token format: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn("Invalid token signature: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("Token claims string is empty: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract user ID from token
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Extract username from token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("username", String.class);
    }

    /**
     * Extract email from token
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * Extract role from token
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * Get expiration date from token
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration();
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Blacklist a token (for logout)
     * Token will be invalid until it naturally expires
     */
    public void blacklistToken(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            long ttl = expiration.getTime() - System.currentTimeMillis();
            
            if (ttl > 0) {
                String key = BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(key, "blacklisted", ttl, TimeUnit.MILLISECONDS);
                log.debug("Token blacklisted successfully");
            }
        } catch (Exception e) {
            log.error("Error blacklisting token: {}", e.getMessage());
        }
    }

    /**
     * Check if token is blacklisted
     */
    private boolean isTokenBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Error checking token blacklist: {}", e.getMessage());
            // On Redis error, allow the request (fail open for availability)
            return false;
        }
    }

    /**
     * Update user's last activity timestamp
     * Used for activity-based token expiration (30 days of inactivity)
     */
    private void updateLastActivity(UUID userId) {
        try {
            String key = LAST_ACTIVITY_PREFIX + userId;
            // Store activity with 30-day TTL (same as refresh token max)
            redisTemplate.opsForValue().set(
                key, 
                System.currentTimeMillis(), 
                refreshTokenExpiration, 
                TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            log.error("Error updating last activity: {}", e.getMessage());
            // Non-critical error, continue without activity tracking
        }
    }

    /**
     * Check if user has been active within the activity window
     */
    private boolean isUserActive(UUID userId) {
        try {
            String key = LAST_ACTIVITY_PREFIX + userId;
            Long lastActivity = (Long) redisTemplate.opsForValue().get(key);
            
            if (lastActivity == null) {
                // No activity record found, user may be new or record expired
                return true; // Allow and create new activity record
            }

            // Check if activity is within 30-day window
            long timeSinceLastActivity = System.currentTimeMillis() - lastActivity;
            return timeSinceLastActivity < refreshTokenExpiration;
        } catch (Exception e) {
            log.error("Error checking user activity: {}", e.getMessage());
            // On Redis error, allow the request (fail open)
            return true;
        }
    }

    /**
     * Extract all claims from token
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}