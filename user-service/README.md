# User Microservice

A comprehensive Spring Boot microservice for user management with JWT authentication, Kafka event publishing, and RESTful APIs.

## 🚀 Features

### Core Functionality
- **User Management**: Create, read, update, and delete user accounts
- **Authentication**: JWT-based authentication and authorization
- **Role-based Access Control**: Support for USER, ADMIN, and MODERATOR roles
- **Password Security**: BCrypt password hashing
- **Data Validation**: Comprehensive input validation with custom error messages

### Technical Stack
- **Framework**: Spring Boot 3.2.0
- **Database**: H2 (in-memory) with JPA/Hibernate
- **Security**: Spring Security with JWT
- **Documentation**: Swagger/OpenAPI 3
- **Messaging**: Apache Kafka for event publishing
- **Testing**: JUnit 5 with Mockito
- **Build Tool**: Maven

### Security Features
- JWT token generation and validation
- Password encryption using BCrypt
- Role-based authorization
- CORS configuration
- Stateless session management

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Apache Kafka (for event publishing)
- Git

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd MicroservicesBasics/user-service
```

### 2. Configure Application
The application uses `application.properties` for configuration. Key configurations:

```properties
# Server
server.port=8081

# Database (H2 in-memory)
spring.datasource.url=jdbc:h2:mem:userdb
spring.datasource.username=sa
spring.datasource.password=password

# JWT
jwt.secret=your-secret-key-here-make-it-very-long-and-secure-in-production
jwt.expiration=86400000

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
```

### 3. Start Kafka (Optional)
If you want to use Kafka event publishing:
```bash
# Start Kafka and Zookeeper
kafka-server-start.sh config/server.properties
```

### 4. Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8081`

## 📚 API Documentation

### Swagger UI
Access the interactive API documentation at:
- **Swagger UI**: `http://localhost:8081/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8081/api-docs`

### H2 Database Console
Access the in-memory database console at:
- **H2 Console**: `http://localhost:8081/h2-console`
- **JDBC URL**: `jdbc:h2:mem:userdb`
- **Username**: `sa`
- **Password**: `password`

## 🔐 API Endpoints

### Authentication APIs

#### 1. User Registration
```http
POST /api/users/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "USER"
}
```

#### 2. User Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "john_doe",
  "password": "password123"
}
```

#### 3. Validate Token
```http
POST /api/auth/validate
Authorization: Bearer <jwt-token>
```

#### 4. Get Current User
```http
GET /api/auth/me
Authorization: Bearer <jwt-token>
```

### User Management APIs

#### 1. Get User by ID
```http
GET /api/users/{id}
Authorization: Bearer <jwt-token>
```

#### 2. Get User by Username
```http
GET /api/users/username/{username}
Authorization: Bearer <jwt-token>
```

#### 3. Get User by Email
```http
GET /api/users/email/{email}
Authorization: Bearer <jwt-token>
```

#### 4. Get All Users
```http
GET /api/users
Authorization: Bearer <jwt-token>
```

#### 5. Update User
```http
PUT /api/users/{id}
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "username": "john_doe_updated",
  "email": "john.updated@example.com",
  "password": "newpassword123",
  "role": "ADMIN"
}
```

#### 6. Delete User
```http
DELETE /api/users/{id}
Authorization: Bearer <jwt-token>
```

## 🔑 Authentication & Authorization

### JWT Token Structure
The JWT token contains:
- **Subject**: Username
- **Claims**: User ID, Role
- **Expiration**: 24 hours (configurable)

### Role-based Access Control
- **USER**: Can access their own profile
- **ADMIN**: Can access all user data and perform all operations
- **MODERATOR**: Intermediate role (can be extended)

### Authorization Rules
- Public endpoints: `/api/auth/**`, `/api/users/register`
- Protected endpoints: All other endpoints require valid JWT token
- Role-based access: Some endpoints require specific roles

## 📊 Data Models

### User Entity
```java
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

### User Roles
- `USER`: Standard user role
- `ADMIN`: Administrator with full access
- `MODERATOR`: Moderator role

## 🧪 Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn test jacoco:report
```

### Test Configuration
The project includes comprehensive unit tests with special configurations to handle Java 23 compatibility:

#### Maven Surefire Plugin Configuration
The `pom.xml` includes ByteBuddy experimental support for Java 23:
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

#### Test Architecture
- **Service Tests**: Use `@SpringBootTest` with `@MockBean` to avoid Mockito/Java 23 compatibility issues
- **Controller Tests**: Use `@WebMvcTest` with custom security configuration for testing
- **Test Security**: Custom `TestSecurityConfig` disables CSRF and permits all requests for testing

### Test Coverage
The project includes comprehensive unit tests for:
- Service layer business logic (integration tests with Spring context)
- Controller API endpoints (web layer tests with MockMvc)
- JWT utility functions
- Password encoding
- Exception handling
- Security configurations

For detailed testing information, see [TESTING.md](TESTING.md).

## 📡 Kafka Integration

### Event Publishing
The service publishes events to Kafka for:
- **User Created**: When a new user is registered
- **User Updated**: When user information is modified
- **User Deleted**: When a user account is deleted

### Event Structure
```json
{
  "eventType": "USER_CREATED",
  "userId": 1,
  "username": "john_doe",
  "timestamp": 1704067200000
}
```

### Kafka Topics
- **user-events**: All user-related events

## 🔧 Configuration

### Environment Variables
You can override configuration using environment variables:
```bash
export SERVER_PORT=8082
export JWT_SECRET=your-custom-secret
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

### Profile-based Configuration
Create profile-specific properties files:
- `application-dev.properties`
- `application-prod.properties`
- `application-test.properties`

## 🚀 Deployment

### Docker (Optional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/user-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Kubernetes (Optional)
The service can be deployed to Kubernetes with proper configuration for:
- Database connectivity
- Kafka connectivity
- JWT secret management
- Health checks

## 📝 Logging

The application uses SLF4J with Logback for logging. Log levels can be configured in `application.properties`:
```properties
logging.level.com.microservices.userservice=DEBUG
logging.level.org.springframework.security=INFO
```

## 🔍 Monitoring

### Health Checks
- **Health Endpoint**: `/actuator/health`
- **Info Endpoint**: `/actuator/info`

### Metrics (Optional)
Add Spring Boot Actuator for metrics:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 📚 Documentation

- **[README.md](README.md)**: Project overview and setup guide
- **[API_DOCUMENTATION.md](API_DOCUMENTATION.md)**: Detailed API reference
- **[TESTING.md](TESTING.md)**: Comprehensive testing guide and troubleshooting
- **[CHANGELOG.md](CHANGELOG.md)**: Version history and changes

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the API documentation at `/swagger-ui.html`
- Review the test cases for usage examples
- Consult the [TESTING.md](TESTING.md) for troubleshooting

## 🔄 Version History

- **v1.0.1**: Java 23 compatibility fixes and test improvements
- **v1.0.0**: Initial release with user management and JWT authentication
- Features: User CRUD, JWT auth, Kafka events, Swagger docs, Unit tests

For detailed version history, see [CHANGELOG.md](CHANGELOG.md).

---

**Happy Coding! 🎉** 