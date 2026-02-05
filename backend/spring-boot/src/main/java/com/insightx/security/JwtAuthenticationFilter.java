package com.insightx.security;

import com.insightx.entities.User;
import com.insightx.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * JWT Authentication Filter - Intercepts and validates JWT tokens
 * 
 * Features:
 * - Extract JWT from Authorization header
 * - Validate token signature and expiration
 * - Load user details with Redis caching
 * - Set Spring Security authentication
 * - Skip public endpoints
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_CACHE_PREFIX = "user:cache:";

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extractTokenFromRequest(request);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                UUID userId = jwtTokenProvider.getUserIdFromToken(token);
                
                // Try to get user from cache first, then DB
                User user = getCachedUser(userId);
                
                if (user != null && user.getActive() && user.getEmailVerified()) {
                    // Create authentication token with user's role
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                    );
                    
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("User {} authenticated successfully", user.getUsername());
                } else {
                    log.warn("User {} is inactive or email not verified", userId);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip filter for public endpoints but NOT for /auth/me (needs auth)
        // Only skip: /auth/register, /auth/login, /auth/verify-email, /auth/resend-verification, /auth/refresh
        return path.endsWith("/auth/register") ||
               path.endsWith("/auth/login") ||
               path.endsWith("/auth/verify-email") ||
               path.endsWith("/auth/resend-verification") ||
               path.endsWith("/auth/refresh") ||
               path.contains("/actuator/") ||
               path.contains("/v3/api-docs") ||
               path.contains("/swagger-ui");
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }

    /**
     * Get user from database (no caching for simplicity)
     */
    private User getCachedUser(UUID userId) {
        try {
            var userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                log.debug("User {} loaded from database", userId);
                return user;
            }
            
            log.warn("User {} not found in database", userId);
            return null;
        } catch (Exception e) {
            log.error("Error loading user from database: {}", e.getMessage());
            return null;
        }
    }
}