package com.insightx.config;

// WebClient Configuration for FastAPI Communication
// Configures non-blocking HTTP client for intelligence service integration
//
// Key Responsibilities:
// - Create WebClient bean for FastAPI calls
// - Configure connection pooling
// - Set timeouts (connect, read, write)
// - Add retry logic with exponential backoff
// - Configure service-level authentication
// - Add request/response logging
//
// FastAPI Endpoints to Integrate:
// - GET /api/media/{type}/{id} - Get media metadata
// - GET /api/recommendations/user/{userId} - Get personalized recommendations
// - POST /api/recommendations/similar - Get similar media
// - GET /api/providers/{region}/{mediaId} - Get watch providers
// - POST /api/ai/explain - Get AI explanation for recommendation
// - GET /api/themes/extract - Extract themes from user profile
//
// Configuration:
// - Base URL: http://localhost:8000 (FastAPI default port)
// - Connection timeout: 5 seconds
// - Read timeout: 30 seconds
// - Max connections: 100
// - Retry attempts: 3 with exponential backoff
//
// Authentication:
// - Add custom header: X-Service-Token
// - Token configured via environment variable
//
// Error Handling:
// - Catch WebClientException
// - Log errors with request details
// - Return Optional.empty() or throw custom exception
// - Circuit breaker pattern for repeated failures