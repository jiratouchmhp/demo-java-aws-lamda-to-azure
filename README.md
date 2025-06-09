# Spring Boot Migration API

This project demonstrates the migration from AWS Lambda Java to a containerized Spring Boot application using clean architecture principles, targeting Azure Container Apps.

## Migration Overview

This application has been successfully migrated from:
- **From**: AWS Lambda with Spring Boot serverless container
- **To**: Containerized Spring Boot application with clean architecture
- **Target**: Azure Container Apps, Azure AKS, Azure App Services

## Architecture

The application follows clean architecture principles with the following layers:

```
src/main/java/com/example/migration/
├── MigrationApplication.java    # Main Spring Boot application
├── domain/                      # Domain layer (business entities & interfaces)
│   ├── Course.java             # Domain entity
│   └── CourseRepository.java   # Repository interface (port)
├── application/                 # Application layer (use cases & business logic)
│   └── CourseService.java      # Business orchestration
├── adapter/                     # Infrastructure layer (external concerns)
│   └── InMemoryCourseRepository.java  # Repository implementation
├── controller/                  # Presentation layer (REST controllers)
│   ├── CourseController.java   # CRUD operations for courses
│   ├── PingController.java     # Health check endpoint
│   └── GlobalExceptionHandler.java  # Error handling
└── dto/                        # Data Transfer Objects
    ├── CourseDto.java         # API request/response DTO
    └── CourseMapper.java      # Domain ⟷ DTO mapping
```

## Features

- **Health Check**: `GET /ping` - Simple health check endpoint
- **Course Management**: Full CRUD operations at `/courses`
  - `GET /courses` - List all courses
  - `POST /courses` - Create a new course  
  - `GET /courses/{id}` - Get course by ID
  - `PUT /courses/{id}` - Update course
  - `DELETE /courses/{id}` - Delete course
- **API Documentation**: Swagger UI available at `/swagger-ui.html`
- **Validation**: Request validation with proper error responses
- **Error Handling**: Global exception handling with structured responses
- **Logging**: Structured logging with SLF4J
- **Environment Profiles**: Development and production configurations

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.6**
- **Maven** for build management
- **Docker** for containerization
- **OpenAPI/Swagger** for API documentation
- **JUnit 5** for testing
- **Azure Container Apps** for deployment

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker (for containerization)
- Azure CLI (for Azure deployment)

### Running Locally

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd demo-java-aws-lamda-to-azure
   ```

2. **Build and run the application**
   ```bash
   mvn spring-boot:run
   ```

3. **Test the application**
   ```bash
   # Health check
   curl http://localhost:8080/ping
   
   # Create a course
   curl -X POST http://localhost:8080/courses \
     -H "Content-Type: application/json" \
     -d '{"id": 1, "name": "Spring Boot Fundamentals", "price": 99.99}'
   
   # Get all courses
   curl http://localhost:8080/courses
   ```

4. **View API documentation**
   Open http://localhost:8080/swagger-ui.html in your browser

### Running Tests

```bash
mvn test
```

## Containerization

### Build Docker Image

```bash
docker build -t springboot-api:1.0.0 .
```

### Run Container Locally

```bash
docker run -p 8080:8080 springboot-api:1.0.0
```

## Azure Deployment

### Automated Deployment

Use the provided deployment script:

```bash
chmod +x deploy-azure.sh
./deploy-azure.sh
```

### Manual Deployment

1. **Create Azure Container Registry**
   ```bash
   az acr create --name myacr --resource-group rg-springboot --sku Basic
   ```

2. **Build and push image**
   ```bash
   az acr build --registry myacr --image springboot-api:1.0.0 --file Dockerfile .
   ```

3. **Create Container App Environment**
   ```bash
   az containerapp env create --name aca-env --resource-group rg-springboot --location eastus
   ```

4. **Deploy using ARM template**
   ```bash
   az deployment group create \
     --resource-group rg-springboot \
     --template-file azure-container-app-template.json \
     --parameters containerAppName=springboot-api environmentName=aca-env acrName=myacr
   ```

## Configuration

### Application Profiles

- **Development** (`dev`): Debug logging enabled, detailed error responses
- **Production** (`prod`): Optimized logging, secure error responses

### Environment Variables

- `SPRING_PROFILES_ACTIVE`: Set to `dev` or `prod`
- `SERVER_PORT`: Server port (default: 8080)

## Migration Benefits

✅ **Scalability**: Horizontal scaling with container orchestration  
✅ **Portability**: Runs on any container platform (Azure, AWS, GCP, on-premises)  
✅ **Clean Architecture**: Separation of concerns, testable, maintainable  
✅ **API Documentation**: Auto-generated OpenAPI/Swagger documentation  
✅ **Validation**: Robust input validation and error handling  
✅ **Testing**: Comprehensive unit and integration tests  
✅ **Monitoring**: Health checks and structured logging  
✅ **DevOps Ready**: Docker containerization and Azure deployment automation  

## API Examples

### Health Check
```bash
GET /ping
Response: {"pong":"Hello, World!"}
```

### Create Course
```bash
POST /courses
Content-Type: application/json

{
  "id": 1,
  "name": "Spring Boot Fundamentals", 
  "price": 99.99
}
```

### Get All Courses
```bash
GET /courses
Response: [{"id":1,"name":"Spring Boot Fundamentals","price":99.99}]
```

## Contributing

1. Follow clean architecture principles
2. Maintain test coverage
3. Update documentation for API changes
4. Use conventional commit messages

## License

This project is part of a migration demonstration from AWS Lambda to Azure Container Apps.