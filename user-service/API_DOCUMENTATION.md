# API Documentation

## Overview

The User Microservice provides RESTful APIs for user management with JWT authentication. All APIs are documented using OpenAPI 3.0 specification and can be accessed via Swagger UI.

**Base URL**: `http://localhost:8081`  
**API Version**: v1.0.0  
**Content-Type**: `application/json`

## Authentication

### JWT Token
Most endpoints require a valid JWT token in the Authorization header:
```
Authorization: Bearer <jwt-token>
```

### Token Structure
```json
{
  "sub": "username",
  "userId": 1,
  "role": "USER",
  "iat": 1704067200,
  "exp": 1704153600
}
```

## Error Responses

### Standard Error Format
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2024-01-01T10:00:00"
}
```

### Validation Error Format
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2024-01-01T10:00:00",
  "errors": {
    "username": "Username must be between 3 and 50 characters",
    "email": "Email should be valid"
  }
}
```

### HTTP Status Codes
- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `404` - Not Found
- `409` - Conflict
- `500` - Internal Server Error

---

## Authentication APIs

### 1. User Registration

**Endpoint**: `POST /api/users/register`  
**Description**: Register a new user account  
**Authentication**: Not required

#### Request Body
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "USER"
}
```

#### Request Validation
- `username`: Required, 3-50 characters, unique
- `email`: Required, valid email format, unique
- `password`: Required, minimum 6 characters
- `role`: Optional, defaults to "USER" (USER, ADMIN, MODERATOR)

#### Response (201 Created)
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

#### Error Responses
- `400` - Validation errors
- `409` - Username or email already exists

---

### 2. User Login

**Endpoint**: `POST /api/auth/login`  
**Description**: Authenticate user and get JWT token  
**Authentication**: Not required

#### Request Body
```json
{
  "usernameOrEmail": "john_doe",
  "password": "password123"
}
```

#### Request Validation
- `usernameOrEmail`: Required, can be username or email
- `password`: Required

#### Response (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER"
}
```

#### Error Responses
- `400` - Validation errors
- `401` - Invalid credentials

---

### 3. Validate Token

**Endpoint**: `POST /api/auth/validate`  
**Description**: Validate JWT token and return user information  
**Authentication**: Required (JWT token)

#### Request Headers
```
Authorization: Bearer <jwt-token>
```

#### Response (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER"
}
```

#### Error Responses
- `401` - Invalid or expired token

---

### 4. Get Current User

**Endpoint**: `GET /api/auth/me`  
**Description**: Get information about the currently authenticated user  
**Authentication**: Required (JWT token)

#### Request Headers
```
Authorization: Bearer <jwt-token>
```

#### Response (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER"
}
```

#### Error Responses
- `401` - Not authenticated

---

## User Management APIs

### 1. Get User by ID

**Endpoint**: `GET /api/users/{id}`  
**Description**: Retrieve user information by ID  
**Authentication**: Required (JWT token)  
**Authorization**: ADMIN role or own profile

#### Path Parameters
- `id`: User ID (Long)

#### Response (200 OK)
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

#### Error Responses
- `401` - Not authenticated
- `403` - Insufficient permissions
- `404` - User not found

---

### 2. Get User by Username

**Endpoint**: `GET /api/users/username/{username}`  
**Description**: Retrieve user information by username  
**Authentication**: Required (JWT token)  
**Authorization**: ADMIN role only

#### Path Parameters
- `username`: Username (String)

#### Response (200 OK)
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

#### Error Responses
- `401` - Not authenticated
- `403` - Insufficient permissions
- `404` - User not found

---

### 3. Get User by Email

**Endpoint**: `GET /api/users/email/{email}`  
**Description**: Retrieve user information by email  
**Authentication**: Required (JWT token)  
**Authorization**: ADMIN role only

#### Path Parameters
- `email`: Email address (String)

#### Response (200 OK)
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

#### Error Responses
- `401` - Not authenticated
- `403` - Insufficient permissions
- `404` - User not found

---

### 4. Get All Users

**Endpoint**: `GET /api/users`  
**Description**: Retrieve list of all users  
**Authentication**: Required (JWT token)  
**Authorization**: ADMIN role only

#### Response (200 OK)
```json
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "USER",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  },
  {
    "id": 2,
    "username": "admin_user",
    "email": "admin@example.com",
    "role": "ADMIN",
    "createdAt": "2024-01-01T11:00:00",
    "updatedAt": "2024-01-01T11:00:00"
  }
]
```

#### Error Responses
- `401` - Not authenticated
- `403` - Insufficient permissions

---

### 5. Update User

**Endpoint**: `PUT /api/users/{id}`  
**Description**: Update user information  
**Authentication**: Required (JWT token)  
**Authorization**: ADMIN role or own profile

#### Path Parameters
- `id`: User ID (Long)

#### Request Body
```json
{
  "username": "john_doe_updated",
  "email": "john.updated@example.com",
  "password": "newpassword123",
  "role": "ADMIN"
}
```

#### Request Validation
- `username`: Required, 3-50 characters, unique (excluding current user)
- `email`: Required, valid email format, unique (excluding current user)
- `password`: Optional, minimum 6 characters
- `role`: Optional (USER, ADMIN, MODERATOR)

#### Response (200 OK)
```json
{
  "id": 1,
  "username": "john_doe_updated",
  "email": "john.updated@example.com",
  "role": "ADMIN",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T12:00:00"
}
```

#### Error Responses
- `400` - Validation errors
- `401` - Not authenticated
- `403` - Insufficient permissions
- `404` - User not found
- `409` - Username or email already exists

---

### 6. Delete User

**Endpoint**: `DELETE /api/users/{id}`  
**Description**: Delete user account  
**Authentication**: Required (JWT token)  
**Authorization**: ADMIN role only

#### Path Parameters
- `id`: User ID (Long)

#### Response (204 No Content)
No response body

#### Error Responses
- `401` - Not authenticated
- `403` - Insufficient permissions
- `404` - User not found

---

## Data Models

### User Entity
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

### CreateUserRequest
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "USER"
}
```

### LoginRequest
```json
{
  "usernameOrEmail": "john_doe",
  "password": "password123"
}
```

### AuthResponse
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER"
}
```

### UserRole Enum
- `USER`: Standard user role
- `ADMIN`: Administrator with full access
- `MODERATOR`: Moderator role

---

## Rate Limiting

Currently, no rate limiting is implemented. Consider implementing rate limiting for production use.

## CORS Configuration

The API supports CORS with the following configuration:
- Allowed Origins: All (`*`)
- Allowed Methods: GET, POST, PUT, DELETE, OPTIONS
- Allowed Headers: All (`*`)
- Allow Credentials: true

---

## Examples

### Complete User Registration Flow

1. **Register User**
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "USER"
  }'
```

2. **Login**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "john_doe",
    "password": "password123"
  }'
```

3. **Get User Profile**
```bash
curl -X GET http://localhost:8081/api/users/1 \
  -H "Authorization: Bearer <jwt-token>"
```

### Error Handling Examples

1. **Validation Error**
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "jo",
    "email": "invalid-email",
    "password": "123"
  }'
```

Response:
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2024-01-01T10:00:00",
  "errors": {
    "username": "Username must be between 3 and 50 characters",
    "email": "Email should be valid",
    "password": "Password must be at least 6 characters"
  }
}
```

2. **Authentication Error**
```bash
curl -X GET http://localhost:8081/api/users/1 \
  -H "Authorization: Bearer invalid-token"
```

Response:
```json
{
  "status": 401,
  "message": "Invalid token",
  "timestamp": "2024-01-01T10:00:00"
}
```

---

## Testing

### Using Swagger UI
1. Start the application
2. Navigate to `http://localhost:8081/swagger-ui.html`
3. Use the interactive API documentation to test endpoints

### Using cURL
All examples in this documentation can be tested using cURL commands.

### Using Postman
Import the OpenAPI specification from `http://localhost:8081/api-docs` into Postman for easy testing.

### Unit Testing
The project includes comprehensive unit tests with special configurations:

#### Test Configuration
- **Service Tests**: Use `@SpringBootTest` with `@MockBean` for integration testing
- **Controller Tests**: Use `@WebMvcTest` with custom security configuration
- **Java 23 Compatibility**: ByteBuddy experimental support in Maven Surefire plugin
- **Test Security**: Custom `TestSecurityConfig` disables CSRF for testing

#### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run controller tests
mvn test -Dtest=UserControllerTest
```

---

## Support

For API support:
- Check the Swagger UI documentation
- Review the error responses
- Check the application logs
- Create an issue in the repository 