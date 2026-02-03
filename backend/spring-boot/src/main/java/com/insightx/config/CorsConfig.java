package com.insightx.config;

// CORS Configuration for Flutter Client
// Configures Cross-Origin Resource Sharing to allow Flutter app requests
//
// Key Responsibilities:
// - Allow Flutter client origins
// - Configure allowed HTTP methods
// - Set allowed headers
// - Enable credentials (cookies, authorization headers)
// - Define max age for preflight caching
//
// Development Settings:
// - Allowed origins: http://localhost:* (Flutter web dev server)
// - Allowed origins: * (for mobile development)
//
// Production Settings:
// - Allowed origins: https://app.insightx.com
// - Allowed origins: https://www.insightx.com
//
// Allowed Methods:
// - GET, POST, PUT, DELETE, PATCH, OPTIONS
//
// Allowed Headers:
// - Authorization (for JWT tokens)
// - Content-Type
// - Accept
// - X-Request-ID (for tracing)
//
// Exposed Headers:
// - X-Total-Count (for pagination)
// - X-Page-Number
// - X-Page-Size
//
// Configuration should be environment-aware via Spring profiles