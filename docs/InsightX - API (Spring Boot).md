# InsightX Backend - Spring Boot Setup Guide

## Overview
This is the Spring Boot backend for InsightX, a cross-media intelligence platform that unifies movies, books, and games into a single, coherent discovery experience.

## Architecture
- **Framework**: Spring Boot 3.2.2
- **Java Version**: 17
- **Database**: PostgreSQL 16
- **Cache**: Redis 7
- **Build Tool**: Maven

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker (for PostgreSQL and Redis)
- Git

## Quick Start

### 1. Start Infrastructure Services
The docker-compose.yml file in the project root sets up PostgreSQL and Redis:

```bash
cd /path/to/insightx/backend
docker-compose up -d
```

This starts:
- PostgreSQL on port 5432 (database: insightx, user: insightx, password: insightx)
- Redis on port 6379

### 2. Configure Environment Variables
Create a `.env` file or set environment variables:

```bash
# JWT Secret (IMPORTANT: Change in production!)
export JWT_SECRET="your-very-long-and-secure-secret-key-at-least-256-bits"

# FastAPI Service URL (when FastAPI is ready)
export FASTAPI_URL="http://localhost:8000"
export FASTAPI_SERVICE_TOKEN="your-service-token"

# CORS Origins (for Flutter client)
export CORS_ORIGINS="http://localhost:*"
```

### 3. Build the Project
```bash
mvn clean install
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

Or run with a specific profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will start on http://localhost:8080

## Project Structure

```
src/main/java/com/insightx/
├── InsightXApplication.java          # Main application entry point
├── config/                            # Configuration classes
│   ├── SecurityConfig.java           # Spring Security setup
│   ├── RedisConfig.java              # Redis configuration
│   ├── WebClientConfig.java          # FastAPI client config
│   └── CorsConfig.java               # CORS configuration
├── entities/                          # JPA entities
│   ├── User.java
│   ├── UserPreference.java
│   ├── WatchedEntry.java
│   ├── Rating.java
│   ├── Review.java
│   ├── Bookmark.java
│   ├── TasteProfile.java
│   └── MediaType.java                # Enum
├── repositories/                      # Data access layer
│   ├── UserRepository.java
│   ├── WatchedEntryRepository.java
│   ├── RatingRepository.java
│   ├── ReviewRepository.java
│   ├── BookmarkRepository.java
│   ├── UserPreferenceRepository.java
│   └── TasteProfileRepository.java
├── services/                          # Business logic
│   ├── AuthService.java
│   ├── UserService.java
│   ├── WatchedService.java
│   ├── RatingService.java
│   ├── ReviewService.java
│   ├── BookmarkService.java
│   ├── PreferenceService.java
│   ├── TasteProfileService.java
│   └── FastAPIService.java
├── controllers/                       # REST API endpoints
│   ├── AuthController.java
│   ├── UserController.java
│   ├── MediaController.java
│   ├── ReviewController.java
│   ├── RecommendationController.java
│   └── PreferenceController.java
├── security/                          # Security components
│   ├── JwtAuthenticationFilter.java
│   └── JwtTokenProvider.java
└── exceptions/                        # Exception handling
    ├── GlobalExceptionHandler.java
    └── CustomExceptions.java

src/main/resources/
└── application.yml                    # Application configuration
```

## API Endpoints

### Authentication (Public)
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Refresh token

### User Management (Authenticated)
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update profile
- `PUT /api/users/region` - Update region
- `PUT /api/users/password` - Change password
- `GET /api/users/statistics` - Get user stats

### Media Operations (Authenticated)
- `GET /api/media/{type}/{id}` - Get media details
- `POST /api/media/watched` - Mark as watched
- `POST /api/media/rating` - Submit rating
- `POST /api/media/bookmark` - Add bookmark
- `GET /api/media/watched` - Get watch history
- `GET /api/media/ratings` - Get user ratings
- `GET /api/media/bookmarks` - Get bookmarks

### Reviews (Authenticated)
- `POST /api/reviews` - Create review
- `GET /api/reviews/my` - Get user's reviews
- `PUT /api/reviews/{id}` - Update review
- `DELETE /api/reviews/{id}` - Delete review

### Recommendations (Authenticated)
- `GET /api/recommendations` - Get personalized recommendations
- `GET /api/recommendations/similar/{type}/{id}` - Get similar media
- `GET /api/recommendations/taste-profile` - Get taste profile

### Preferences (Authenticated)
- `GET /api/preferences` - Get all preferences
- `PUT /api/preferences/{key}` - Update preference

## Development Workflow

### Using GitHub Copilot
Each file in this project contains detailed comments explaining:
- Purpose and responsibilities
- Key methods to implement
- Business logic and rules
- Integration points
- Error handling strategies

Use GitHub Copilot to:
1. Generate method implementations based on comments
2. Create DTO classes
3. Write validation logic
4. Implement repository queries
5. Create test cases

### Database Migrations
Currently using `spring.jpa.hibernate.ddl-auto=update` for development.

For production, consider using:
- Flyway or Liquibase for versioned migrations
- Set `ddl-auto=validate` to prevent auto-schema updates

### Testing
Run tests:
```bash
mvn test
```

Run with coverage:
```bash
mvn test jacoco:report
```

### Code Quality
Run static analysis:
```bash
mvn checkstyle:check
mvn spotbugs:check
```

## Configuration

### Database
Configure in `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/insightx
    username: insightx
    password: insightx
```

### Redis
```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

### JWT
```yaml
spring:
  security:
    jwt:
      secret: ${JWT_SECRET}
      access-token-expiration: 3600000   # 1 hour
      refresh-token-expiration: 604800000 # 7 days
```

## Integration with FastAPI
The backend communicates with FastAPI for:
- Media metadata aggregation
- Personalized recommendations
- AI explanations
- Theme extraction

Configure FastAPI URL:
```yaml
fastapi:
  base-url: ${FASTAPI_URL:http://localhost:8000}
  service-token: ${FASTAPI_SERVICE_TOKEN}
```

## Security

### JWT Token Flow
1. User logs in with credentials
2. Backend validates and generates JWT access token
3. Client stores token (secure storage)
4. Client sends token in Authorization header: `Bearer <token>`
5. Backend validates token on each request
6. Token expires after 1 hour, refresh token lasts 7 days

### Password Security
- Passwords hashed with BCrypt
- Never stored in plain text
- Minimum requirements enforced

### CORS
Configure allowed origins for Flutter client:
```yaml
cors:
  allowed-origins: http://localhost:*
```

## Deployment

### Building for Production
```bash
mvn clean package -DskipTests
```

This creates `target/insightx-backend-1.0.0.jar`

### Running the JAR
```bash
java -jar target/insightx-backend-1.0.0.jar --spring.profiles.active=prod
```

### Docker (Future)
```dockerfile
FROM eclipse-temurin:17-jre-alpine
COPY target/insightx-backend-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Monitoring

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Metrics
```bash
curl http://localhost:8080/actuator/metrics
```

## Troubleshooting

### Database Connection Issues
- Verify PostgreSQL is running: `docker ps`
- Check connection settings in application.yml
- Ensure database exists: `psql -U insightx -d insightx`

### Redis Connection Issues
- Verify Redis is running: `docker ps`
- Test connection: `redis-cli ping`

### JWT Token Issues
- Verify JWT_SECRET environment variable is set
- Check token expiration times
- Ensure secret is at least 256 bits

## Next Steps

1. **Implement Core Features**
   - Start with entity classes
   - Create repositories
   - Implement services
   - Build controllers

2. **Add DTOs**
   - Create request/response DTOs for all endpoints
   - Add validation annotations

3. **Write Tests**
   - Unit tests for services
   - Integration tests for controllers
   - Repository tests

4. **Integrate with FastAPI**
   - Implement FastAPIService methods
   - Test integration points

5. **Add Documentation**
   - Configure SpringDoc/Swagger
   - Document all endpoints
   - Add API examples

## Resources

- Spring Boot Docs: https://spring.io/projects/spring-boot
- Spring Security: https://spring.io/projects/spring-security
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- JWT: https://jwt.io/
- PostgreSQL: https://www.postgresql.org/docs/

## Contributing

All placeholder files contain detailed implementation comments. Use GitHub Copilot to accelerate development while following the documented patterns and best practices.

## License

[Your License Here]