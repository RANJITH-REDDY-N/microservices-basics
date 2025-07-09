# Changelog

All notable changes to the User Microservice project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1] - 2024-01-XX

### Fixed
- **Java 23 Compatibility**: Fixed Mockito/ByteBuddy compatibility issues with Java 23
  - Added ByteBuddy experimental support in Maven Surefire plugin
  - Updated test configurations to use `@SpringBootTest` with `@MockBean`
  - Resolved `java.lang.IllegalStateException` in tests

- **Test Security Configuration**: Fixed HTTP 403 errors in controller tests
  - Added custom `TestSecurityConfig` to disable CSRF for testing
  - Updated controller tests to use `@WebMvcTest` with proper security configuration
  - Fixed import issues with `TestConfiguration`

- **Test Architecture**: Improved test structure and reliability
  - Service tests now use integration testing approach with Spring context
  - Controller tests use web layer testing with MockMvc
  - Added proper test profiles and configurations

### Changed
- **Maven Configuration**: Updated `pom.xml` with ByteBuddy experimental support
  ```xml
  <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <version>3.1.2</version>
      <configuration>
          <argLine>
              -Dnet.bytebuddy.experimental=true
              -XX:+EnableDynamicAgentLoading
          </argLine>
      </configuration>
  </plugin>
  ```

- **Test Classes**: Updated test implementations
  - `UserServiceTest.java`: Changed to use `@SpringBootTest` with `@MockBean`
  - `UserControllerTest.java`: Added custom security configuration and proper imports
  - Added `TestSecurityConfig` inner classes for test-specific security

### Added
- **Documentation**: Enhanced testing documentation
  - Created comprehensive `TESTING.md` with troubleshooting guide
  - Updated `README.md` with test configuration details
  - Updated `API_DOCUMENTATION.md` with testing information
  - Added `CHANGELOG.md` for version tracking

- **Test Configuration**: Added test-specific configurations
  - Custom security configuration for testing
  - Test profiles and properties
  - Debug and troubleshooting information

### Technical Details
- **ByteBuddy Configuration**: Added experimental support for Java 23
  - `-Dnet.bytebuddy.experimental=true`: Enables experimental ByteBuddy features
  - `-XX:+EnableDynamicAgentLoading`: Enables dynamic agent loading for testing

- **Test Strategy Changes**:
  - Service tests: Use `@SpringBootTest` instead of pure Mockito
  - Controller tests: Use `@WebMvcTest` with custom security
  - Mocking: Use `@MockBean` for Spring-managed beans

- **Security Testing**: Custom test security configuration
  ```java
  @TestConfiguration
  static class TestSecurityConfig {
      @Bean
      public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
          http
              .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
          return http.build();
      }
  }
  ```

## [1.0.0] - 2024-01-XX

### Added
- **Initial Release**: Complete user microservice with JWT authentication
- **Core Features**:
  - User management (CRUD operations)
  - JWT-based authentication and authorization
  - Role-based access control (USER, ADMIN, MODERATOR)
  - Password security with BCrypt hashing
  - Input validation with custom error messages

- **Technical Stack**:
  - Spring Boot 3.2.0
  - H2 in-memory database with JPA/Hibernate
  - Spring Security with JWT
  - Swagger/OpenAPI 3 documentation
  - Apache Kafka for event publishing
  - JUnit 5 with Mockito for testing
  - Maven build tool

- **Security Features**:
  - JWT token generation and validation
  - Password encryption using BCrypt
  - Role-based authorization
  - CORS configuration
  - Stateless session management

- **API Endpoints**:
  - User registration and authentication
  - User management (CRUD operations)
  - Token validation and current user info
  - Role-based access control

- **Documentation**:
  - Comprehensive README.md
  - Detailed API documentation
  - Swagger UI integration
  - Code examples and usage guides

- **Testing**:
  - Unit tests for service layer
  - Controller tests for API endpoints
  - Exception handling tests
  - Security configuration tests

---

## Version History

- **v1.0.1**: Java 23 compatibility fixes and test improvements
- **v1.0.0**: Initial release with complete user microservice

## Compatibility

- **Java**: 17+ (tested with Java 23)
- **Spring Boot**: 3.2.0
- **Maven**: 3.6+
- **Database**: H2 (in-memory)
- **Kafka**: Apache Kafka (optional)

## Migration Guide

### From v1.0.0 to v1.0.1
No breaking changes. The update focuses on test compatibility and documentation improvements.

### Test Configuration Changes
If you have custom test configurations, ensure they follow the new pattern:
- Use `@SpringBootTest` for service tests
- Use `@WebMvcTest` for controller tests
- Use `@MockBean` instead of `@Mock` for Spring-managed beans
- Add custom security configuration for controller tests

---

**Note**: This changelog follows the [Keep a Changelog](https://keepachangelog.com/) format and uses [Semantic Versioning](https://semver.org/). 