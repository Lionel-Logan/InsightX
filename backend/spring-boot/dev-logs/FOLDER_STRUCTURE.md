# InsightX Backend - Folder Structure Reference

## Complete Directory Layout

```
backend/spring-boot/
│
├── 📄 pom.xml                                    # Maven build configuration
├── 📄 .env                                       # Environment variables (JWT secret, etc.)
├── 📄 PHASE2_SUMMARY.md                          # Phase 2 implementation summary
│
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/com/insightx/
│   │   │   │
│   │   │   ├── 📄 InsightXApplication.java       # 🚀 Main application entry point
│   │   │   │
│   │   │   ├── 📁 config/                        # ⚙️ Configuration Classes
│   │   │   │   ├── SecurityConfig.java           # Spring Security + JWT
│   │   │   │   ├── RedisConfig.java              # Redis caching
│   │   │   │   ├── WebClientConfig.java          # FastAPI HTTP client
│   │   │   │   └── CorsConfig.java               # CORS for Flutter
│   │   │   │
│   │   │   ├── 📁 entities/                      # ✅ JPA Entities (IMPLEMENTED)
│   │   │   │   ├── User.java                     # User accounts
│   │   │   │   ├── UserPreference.java           # Key-value preferences
│   │   │   │   ├── UserSession.java              # JWT session management
│   │   │   │   ├── UserFollow.java               # Follow relationships
│   │   │   │   ├── WatchedEntry.java             # Watch history
│   │   │   │   ├── Rating.java                   # User ratings (1-10)
│   │   │   │   ├── Review.java                   # Text reviews
│   │   │   │   ├── Bookmark.java                 # Saved items
│   │   │   │   ├── TasteProfile.java             # Computed preferences
│   │   │   │   └── MediaType.java                # Enum: MOVIE, BOOK, GAME
│   │   │   │
│   │   │   ├── 📁 repositories/                  # 🗄️ Data Access Layer (TODO)
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── UserPreferenceRepository.java
│   │   │   │   ├── UserSessionRepository.java
│   │   │   │   ├── UserFollowRepository.java
│   │   │   │   ├── WatchedEntryRepository.java
│   │   │   │   ├── RatingRepository.java
│   │   │   │   ├── ReviewRepository.java
│   │   │   │   ├── BookmarkRepository.java
│   │   │   │   └── TasteProfileRepository.java
│   │   │   │
│   │   │   ├── 📁 services/                      # 💼 Business Logic (TODO)
│   │   │   │   ├── AuthService.java              # Login, register, JWT
│   │   │   │   ├── UserService.java              # User management
│   │   │   │   ├── SocialService.java            # Follow/unfollow
│   │   │   │   ├── PreferenceService.java        # User preferences
│   │   │   │   ├── WatchedService.java           # Mark as watched
│   │   │   │   ├── RatingService.java            # Submit ratings
│   │   │   │   ├── ReviewService.java            # Create/edit reviews
│   │   │   │   ├── BookmarkService.java          # Save/remove bookmarks
│   │   │   │   ├── TasteProfileService.java      # Generate profiles
│   │   │   │   └── FastAPIService.java           # Python backend integration
│   │   │   │
│   │   │   ├── 📁 controllers/                   # 🌐 REST API Endpoints (TODO)
│   │   │   │   ├── AuthController.java           # POST /api/auth/*
│   │   │   │   ├── UserController.java           # GET/PUT /api/users/*
│   │   │   │   ├── SocialController.java         # POST /api/social/*
│   │   │   │   ├── MediaController.java          # GET /api/media/*
│   │   │   │   ├── ReviewController.java         # POST /api/reviews/*
│   │   │   │   ├── RecommendationController.java # GET /api/recommendations/*
│   │   │   │   └── PreferenceController.java     # GET/PUT /api/preferences/*
│   │   │   │
│   │   │   ├── 📁 security/                      # 🔒 Security Components (TODO)
│   │   │   │   ├── JwtAuthenticationFilter.java  # JWT validation filter
│   │   │   │   ├── JwtTokenProvider.java         # JWT generation/validation
│   │   │   │   └── SecurityContextUtil.java      # Get current user
│   │   │   │
│   │   │   ├── 📁 exceptions/                    # ⚠️ Exception Handling (TODO)
│   │   │   │   ├── GlobalExceptionHandler.java   # Centralized error handling
│   │   │   │   └── CustomExceptions.java         # Custom exception classes
│   │   │   │
│   │   │   └── 📁 dto/                           # 📄 Data Transfer Objects (TODO)
│   │   │       ├── 📁 request/                   # API request DTOs
│   │   │       └── 📁 response/                  # API response DTOs
│   │   │
│   │   └── 📁 resources/
│   │       ├── 📄 application.yml                # ✅ Main configuration
│   │       ├── 📄 application.properties         # Alternative config format
│   │       │
│   │       └── 📁 db/migration/                  # ✅ Flyway Migrations (IMPLEMENTED)
│   │           ├── V1__create_users_table.sql
│   │           ├── V2__create_user_preferences_table.sql
│   │           ├── V3__create_user_sessions_table.sql
│   │           ├── V4__create_user_follows_table.sql
│   │           ├── V5__create_watched_entries_table.sql
│   │           ├── V6__create_ratings_table.sql
│   │           ├── V7__create_reviews_table.sql
│   │           ├── V8__create_bookmarks_table.sql
│   │           ├── V9__create_taste_profiles_table.sql
│   │           └── V10__create_additional_indexes.sql
│   │
│   └── 📁 test/                                  # Unit & Integration Tests (TODO)
│       └── 📁 java/com/insightx/
│
└── 📁 target/                                    # Maven build output
    └── insightx-backend-1.0.0.jar
```

## Legend

- ✅ **Implemented** - Fully complete and ready
- 🚀 **Main** - Application entry point
- ⚙️ **Config** - Configuration classes
- 🗄️ **Repository** - Data access layer
- 💼 **Service** - Business logic layer
- 🌐 **Controller** - REST API endpoints
- 🔒 **Security** - Authentication & authorization
- ⚠️ **Exception** - Error handling
- 📄 **DTO** - Data transfer objects
- TODO - To be implemented in future phases

## Package Responsibilities

| Package | Purpose | Status |
|---------|---------|--------|
| `entities` | JPA entities, database models | ✅ Complete (Phase 2) |
| `repositories` | Spring Data JPA repositories | 📝 Next Phase |
| `services` | Business logic, orchestration | 📝 Next Phase |
| `controllers` | REST API endpoints | 📝 Next Phase |
| `security` | Authentication, JWT, filters | 📝 Next Phase |
| `config` | Spring configuration classes | ⚠️ Partial (needs security config) |
| `exceptions` | Custom exceptions, handlers | 📝 Next Phase |
| `dto` | Request/response objects | 📝 Next Phase |

## Database Schema

All 9 tables created via Flyway migrations:

1. **users** - User accounts
2. **user_preferences** - Settings & privacy
3. **user_sessions** - JWT tokens
4. **user_follows** - Social relationships
5. **watched_entries** - Consumption history
6. **ratings** - User ratings (1-10)
7. **reviews** - Text reviews
8. **bookmarks** - Saved items
9. **taste_profiles** - Computed preferences (JSONB)

## Key Files Modified in Phase 2

- ✅ `pom.xml` - Added Flyway, Hypersistence dependencies
- ✅ `application.yml` - Configured Flyway, changed ddl-auto to validate
- ✅ All 10 entity classes - Full JPA implementations
- ✅ All 10 Flyway migration scripts - Complete database schema

## Running the Application

```bash
# 1. Start PostgreSQL and Redis
docker-compose up -d

# 2. Build project
mvn clean install

# 3. Run application
mvn spring-boot:run

# Flyway will automatically apply all migrations on startup
```

## What's Next?

See [PHASE2_SUMMARY.md](PHASE2_SUMMARY.md) for detailed implementation notes and next steps.
