package com.insightx.controllers;

// PreferenceController - REST API endpoints for user preferences
// Base path: /api/preferences
// All endpoints require authentication (JWT)
//
// Endpoints:
//
// GET /api/preferences
// - Get all preferences for current user
// - Response: Map<String, String> (key-value pairs)
// - Status: 200 OK
//
// GET /api/preferences/{key}
// - Get specific preference by key
// - Path variable: key (preference name)
// - Response: { "key": "...", "value": "..." }
// - Status: 200 OK
// - Errors: 404 if preference not found
//
// PUT /api/preferences/{key}
// - Set or update a single preference
// - Path variable: key
// - Request body: { "value": "..." }
// - Response: { "key": "...", "value": "..." }
// - Status: 200 OK
// - Errors: 400 if validation fails
//
// PUT /api/preferences/batch
// - Update multiple preferences at once
// - Request body: Map<String, String> (multiple key-value pairs)
// - Response: Map<String, String> (updated preferences)
// - Status: 200 OK
// - Errors: 400 if any validation fails
//
// DELETE /api/preferences/{key}
// - Delete a specific preference
// - Path variable: key
// - Response: success message
// - Status: 200 OK
//
// DELETE /api/preferences/reset
// - Reset all preferences to defaults
// - Response: success message
// - Status: 200 OK
//
// GET /api/preferences/defaults
// - Get default preference values
// - Response: Map<String, String>
// - Status: 200 OK
// - Note: Useful for UI to show available preferences
//
// Dependencies:
// - PreferenceService
//
// Common Preference Keys (for reference):
// - "theme": "dark" | "light"
// - "language": "en" | "es" | "fr" | "de" | "ja" | "hi"
// - "explicit_content": "true" | "false"
// - "notification_recommendations": "true" | "false"
// - "default_media_type": "movie" | "book" | "game" | "all"
// - "results_per_page": "20" | "50" | "100"
// - "auto_play_trailers": "true" | "false"
// - "spoiler_warnings": "true" | "false"
// - "mature_content": "true" | "false"
//
// Validation:
// - Key format: lowercase, alphanumeric + underscore, max 50 chars
// - Value: depends on key, max 1000 chars
// - Validate against allowed values for specific keys
// - Return 400 with details if validation fails
//
// Default Values:
// - theme: "dark"
// - language: "en"
// - explicit_content: "false"
// - notification_recommendations: "true"
// - default_media_type: "all"
// - results_per_page: "20"
// - auto_play_trailers: "true"
//
// Response Format:
// - Simple key-value format
// - Include metadata like last updated timestamp
//
// Cache Invalidation:
// - Clear user preference cache on any update
// - Client should refresh preferences after changes
//
// Error Handling:
// - InvalidPreferenceKeyException -> 400
// - InvalidPreferenceValueException -> 400
// - PreferenceNotFoundException -> 404
// - Handled by global @ControllerAdvice
//
// Usage Flow:
// 1. Client loads all preferences at login
// 2. Caches preferences locally
// 3. Updates server when user changes settings
// 4. Server invalidates cache
// 5. Client receives updated preferences
//
// Future Enhancements:
// - Preference categories (UI, Privacy, Notifications, etc.)
// - Preference validation schemas
// - Preference sync across devices
// - Import/export preferences
// - Preference history/versioning