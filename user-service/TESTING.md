# Testing Documentation

## Overview

This document provides comprehensive information about the testing setup, configurations, and troubleshooting for the User Microservice. The project includes unit tests, integration tests, and web layer tests with special configurations to handle Java 23 compatibility issues.

## Test Architecture

### Test Types

1. **Service Tests** (`UserServiceTest.java`)
   - Integration tests using `@SpringBootTest`
   - Uses `@MockBean` for dependencies
   - Tests business logic with Spring context

2. **Controller Tests** (`UserControllerTest.java`)
   - Web layer tests using `@WebMvcTest`
   - Uses `MockMvc` for HTTP request testing
   - Custom security configuration for testing

3. **Unit Tests** (Future)
   - Pure unit tests without Spring context
   - For utility classes and simple logic

## Java 23 Compatibility Issues

### Problem
- Mockito/ByteBuddy compatibility issues with Java 23
- Tests failing with `java.lang.IllegalStateException`
- ByteBuddy agent loading problems

### Solution
1. **Maven Surefire Plugin Configuration**
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

2. **Test Strategy Changes**
   - Use `@SpringBootTest` instead of pure Mockito for service tests
   - Use `@MockBean` instead of `@Mock` for Spring-managed beans
   - Custom security configuration for controller tests

## Test Configurations

### Service Tests (`UserServiceTest.java`)

```java
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {
    
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    @MockBean
    private KafkaProducerService kafkaProducerService;

    @MockBean
    private PasswordEncoder passwordEncoder;
}
```

**Key Points:**
- Uses `@SpringBootTest` for full Spring context
- Uses `@MockBean` instead of `@Mock` for Spring-managed dependencies
- Includes custom `TestSecurityConfig` for security testing

### Controller Tests (`UserControllerTest.java`)

```java
@WebMvcTest(UserController.class)
@TestPropertySource(properties = {
    "spring.security.user.name=test",
    "spring.security.user.password=test"
})
@Import(UserControllerTest.TestSecurityConfig.class)
public class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
}
```

**Key Points:**
- Uses `@WebMvcTest` for web layer testing
- Custom security configuration disables CSRF
- Uses `MockMvc` for HTTP request simulation

### Test Security Configuration

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

**Purpose:**
- Disables CSRF protection for testing
- Permits all requests without authentication
- Allows testing of endpoints without security constraints

## Running Tests

### Basic Commands
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run controller tests
mvn test -Dtest=UserControllerTest

# Run with verbose output
mvn test -X

# Run with coverage
mvn test jacoco:report
```

### Test Profiles
The tests use the `test` profile which can be configured in `application-test.properties`:
```properties
# Test-specific configurations
spring.datasource.url=jdbc:h2:mem:testdb
logging.level.com.microservices.userservice=DEBUG
```

### Debugging Tests
```bash
# Run tests with debug logging
mvn test -Dlogging.level.com.microservices.userservice=DEBUG

# Run specific test method
mvn test -Dtest=UserServiceTest#testCreateUserSuccess

# Run tests with system properties
mvn test -Dspring.profiles.active=test
```

## Test Coverage

### Current Coverage Areas
- ✅ User creation and validation
- ✅ User authentication and login
- ✅ User retrieval by ID, username, email
- ✅ User update operations
- ✅ User deletion
- ✅ Exception handling
- ✅ JWT token generation and validation
- ✅ Password encoding and verification
- ✅ Kafka event publishing
- ✅ Input validation
- ✅ Security configurations

### Missing Coverage (Future Enhancements)
- ❌ Integration tests with real database
- ❌ Kafka integration tests
- ❌ Performance tests
- ❌ Security penetration tests
- ❌ API contract tests

## Troubleshooting

### Common Issues

#### 1. ByteBuddy Compatibility
**Error:** `java.lang.IllegalStateException: Error during attachment using ByteBuddy agent`
**Solution:** Ensure Maven Surefire plugin has ByteBuddy experimental support enabled

#### 2. CSRF Token Issues
**Error:** `403 Forbidden` in controller tests
**Solution:** Use custom `TestSecurityConfig` that disables CSRF

#### 3. Spring Context Issues
**Error:** `No qualifying bean of type` in service tests
**Solution:** Use `@MockBean` instead of `@Mock` for Spring-managed beans

#### 4. Test Database Issues
**Error:** Database connection problems
**Solution:** Ensure H2 in-memory database is properly configured for tests

### Debug Commands
```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Run tests with debug output
mvn test -X -Dtest=UserServiceTest

# Check test dependencies
mvn dependency:tree -Dscope=test
```

## Best Practices

### Test Organization
1. **Arrange**: Set up test data and mocks
2. **Act**: Execute the method under test
3. **Assert**: Verify the expected behavior

### Mocking Guidelines
- Use `@MockBean` for Spring-managed beans
- Use `@Mock` only for simple utility classes
- Reset mocks in `@BeforeEach` for clean state

### Test Data Management
- Use `@BeforeEach` to set up test data
- Use `@AfterEach` to clean up if needed
- Use meaningful test data names

### Assertion Guidelines
- Test both positive and negative scenarios
- Verify mock interactions when relevant
- Use descriptive test method names

## Future Enhancements

### Planned Improvements
1. **Integration Tests**
   - Real database integration tests
   - Kafka integration tests
   - End-to-end API tests

2. **Performance Tests**
   - Load testing with JMeter
   - Stress testing for concurrent users
   - Memory and CPU profiling

3. **Security Tests**
   - Penetration testing
   - Security vulnerability scanning
   - JWT token security testing

4. **API Contract Tests**
   - OpenAPI specification validation
   - API versioning tests
   - Backward compatibility tests

### Test Infrastructure
1. **Test Containers**
   - Docker-based test environments
   - Real database and Kafka instances
   - Isolated test environments

2. **CI/CD Integration**
   - Automated test execution
   - Test coverage reporting
   - Quality gates

## References

- [Spring Boot Testing Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [ByteBuddy Documentation](https://bytebuddy.net/)

---

**Last Updated:** January 2024  
**Version:** 1.0.0  
**Compatibility:** Java 17+, Spring Boot 3.2.0, Java 23 