# Spring Boot Azure Migration API

**Successfully migrated from AWS Lambda Java to Containerized Spring Boot (Clean Architecture) for Azure**

## Migration Summary

This project has been successfully migrated from an AWS Lambda-based Spring Boot application to a containerized Spring Boot application following Clean Architecture principles, ready for deployment on Azure Container Apps.

## Architecture Overview

The application follows Clean Architecture principles with the following layers:

```
src/main/java/com/example/migration/
├── MigrationApplication.java          # Main application class
├── domain/                           # Domain layer (entities, repository interfaces)
│   ├── Course.java
│   └── CourseRepository.java
├── application/                      # Application layer (use cases, services)
│   └── CourseService.java
├── adapter/                          # Adapter layer (infrastructure)
│   └── persistence/
│       └── InMemoryCourseRepository.java
├── controller/                       # Controller layer (web/API)
│   ├── CourseController.java
│   ├── PingController.java
│   └── GlobalExceptionHandler.java
└── dto/                             # Data Transfer Objects
    ├── CourseRequest.java
    └── CourseResponse.java
```

## Key Features

- ✅ **Clean Architecture**: Separated concerns with domain, application, adapter, and controller layers
- ✅ **Dependency Injection**: Constructor-based dependency injection
- ✅ **Validation**: Request validation with `@Valid` and Jakarta Validation
- ✅ **Error Handling**: Global exception handling with `@ControllerAdvice`
- ✅ **OpenAPI Documentation**: Auto-generated API docs at `/swagger-ui.html`
- ✅ **Health Checks**: Spring Boot Actuator endpoints
- ✅ **Structured Logging**: SLF4J with configurable log levels
- ✅ **Testing**: Unit and integration tests
- ✅ **Containerization**: Docker support with multi-stage builds
- ✅ **Azure Ready**: Deployment configuration for Azure Container Apps

## API Endpoints

### Health Check
- `GET /ping` - Simple health check endpoint
- `GET /actuator/health` - Detailed health information

### Course Management  
- `GET /courses` - Get all courses
- `POST /courses` - Create a new course
- `GET /courses/{id}` - Get course by ID
- `PUT /courses/{id}` - Update course
- `DELETE /courses/{id}` - Delete course

### API Documentation
- `GET /v3/api-docs` - OpenAPI specification (JSON)
- `GET /swagger-ui.html` - Interactive API documentation

## Running the Application

### Local Development
```bash
mvn spring-boot:run
```

### Using JAR
```bash
mvn clean package
java -jar target/spring-boot-azure-migration-1.0.0.jar
```

### Using Docker
```bash
docker build -t springboot-api .
docker run -p 8080:8080 springboot-api
```

### Environment Profiles
- `dev` - Development profile (default)
- `prod` - Production profile

Set the profile using: `SPRING_PROFILES_ACTIVE=prod`

## Testing

Run all tests:
```bash
mvn test
```

The test suite includes:
- Unit tests for controllers with MockMvc
- Integration tests for the full application context
- Validation testing

## Azure Deployment

### Prerequisites
- Azure CLI installed and logged in
- Azure Container Registry (ACR) created
- Resource group created

### Deploy to Azure Container Apps
```bash
# Run the deployment script
./deployment/deploy-to-azure.sh
```

Or manually:
```bash
# Build and push to ACR
az acr build --image springboot-api:1.0.0 --registry myacr --file Dockerfile .

# Deploy to Container Apps
az containerapp create \
  --resource-group rg-springboot \
  --environment aca-env \
  --name springboot-api \
  --image myacr.azurecr.io/springboot-api:1.0.0 \
  --target-port 8080 \
  --ingress external \
  --min-replicas 1 \
  --max-replicas 5
```

## Migration Completed Features

### ✅ Step 1: ASSESS & INITIALIZE SPRING BOOT PROJECT
- Removed AWS Lambda dependencies
- Updated pom.xml for standard Spring Boot
- Created clean architecture folder structure
- Added application.yml configuration

### ✅ Step 2: DEFINE DOMAIN & APPLICATION LAYERS  
- Moved Course to domain layer as entity
- Created CourseRepository interface (port)
- Implemented CourseService (use case)

### ✅ Step 3: IMPLEMENT ADAPTER (INFRASTRUCTURE) LAYER
- Created InMemoryCourseRepository implementation
- Moved persistence logic to adapter layer

### ✅ Step 4: DEVELOP CONTROLLER LAYER
- Removed AWS Lambda handler
- Updated controllers for standard Spring Boot
- Added OpenAPI documentation with Swagger

### ✅ Step 5: INCREMENTAL MIGRATION & VERIFICATION
- Added GlobalExceptionHandler with @ControllerAdvice
- Added request validation with @Valid
- Implemented structured logging with SLF4J
- Created comprehensive test suite

### ✅ Step 6: CONTAINERIZATION & AZURE DEPLOYMENT
- Created multi-stage Dockerfile
- Added Azure Container Apps deployment YAML
- Provided deployment scripts and documentation

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.6**
- **Spring Web MVC**
- **Spring Boot Validation**
- **Spring Boot Actuator**
- **SpringDoc OpenAPI 3**
- **Lombok**
- **JUnit 5**
- **Docker**
- **Azure Container Apps**

## Configuration

The application supports environment-specific configuration:

```yaml
# Development (default)
server.port: 8080
logging.level.com.example.migration: DEBUG

# Production  
logging.level.com.example.migration: INFO
```

## Next Steps

Consider these enhancements for production:
- Replace in-memory repository with persistent storage (Azure SQL, Cosmos DB)
- Add security with Spring Security
- Implement caching with Redis
- Add monitoring with Application Insights
- Set up CI/CD pipeline with GitHub Actions