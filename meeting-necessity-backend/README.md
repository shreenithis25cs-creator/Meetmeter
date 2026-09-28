# Meeting Necessity Score — Backend (Spring Boot 3)

Spring Boot 3 REST API backend service for Google Calendar sync, AI/Rule-based necessity scoring, cost calculations, and user settings management.

## 🛠 Tech Stack
- **Framework**: Spring Boot 3.3.4 (Java 17+)
- **Security & OAuth**: Spring Security + OAuth2 Client
- **Data & Persistence**: Spring Data JPA, H2 in-memory DB / PostgreSQL
- **HTTP Client**: Spring WebFlux `WebClient` for Google Calendar & Claude AI API
- **Token Security**: AES-256 JPA Attribute Converter for OAuth token encryption

## 📁 Key Packages & Architecture
- `com.meetingnecessity.score.controller`: REST Controllers (`AuthController`, `MeetingController`, `DashboardController`, `SettingsController`)
- `com.meetingnecessity.score.service`: Business Logic (`ScoringService`, `ClaudeApiService`, `GoogleCalendarService`, `DashboardService`)
- `com.meetingnecessity.score.model`: JPA Entities (`User`, `Meeting`, `Score`, `Suggestion`, `Settings`)
- `com.meetingnecessity.score.repository`: Spring Data Repositories
- `com.meetingnecessity.score.util`: Security and Encryption Utilities (`EncryptedTokenConverter`)

## 🚀 Running the Backend

### Prerequisites
- JDK 17+ installed
- Apache Maven 3.8+ installed

### Run with Maven
```bash
mvn clean spring-boot:run
```
The server will start on port `8080`.

### Configure API Keys (Optional)
In `src/main/resources/application.properties`:
```properties
# Claude AI Key (optional for live AI evaluation)
anthropic.api.key=YOUR_ANTHROPIC_API_KEY

# Google OAuth Credentials (optional for live Google Calendar sync)
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
```
