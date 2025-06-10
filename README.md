# Spring Boot Migration from AWS Lambda to Azure

This project demonstrates the migration from AWS Lambda Java functions to a containerized Spring Boot application following Clean Architecture principles, designed for deployment on Azure Container Apps, Azure AKS, or Azure App Services.

## Architecture Overview

The application follows Clean Architecture principles with the following layers:

- **Domain Layer**: Core business entities and repository interfaces (framework-free)
- **Application Layer**: Business logic and use case orchestration
- **Adapter Layer**: Infrastructure implementations (persistence, external services)
- **Controller Layer**: REST API endpoints with OpenAPI documentation

## Features

- RESTful API with OpenAPI/Swagger documentation
- Health check endpoint (`/ping`)
- Course management CRUD operations (`/courses`)
- Input validation with detailed error responses
- Structured logging with correlation IDs
- Multi-environment configuration (dev/test/prod)
- Containerized deployment with Docker
- Azure infrastructure as code with Bicep

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.6+
- Docker (for containerization)
- Azure CLI (for deployment)

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/jiratouchmhp/demo-java-aws-lamda-to-azure.git
   cd demo-java-aws-lamda-to-azure
   ```

2. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the application**
   - API: http://localhost:8080
   - Health check: http://localhost:8080/ping
   - API Documentation: http://localhost:8080/swagger-ui.html
   - OpenAPI Spec: http://localhost:8080/api-docs

### Running Tests

```bash
./mvnw test
```

### Building the Application

```bash
./mvnw clean package
```

## API Endpoints

### Health Check
- `GET /ping` - Returns service health status

### Course Management
- `GET /courses` - Get all courses
- `GET /courses/{id}` - Get course by ID
- `POST /courses` - Create a new course
- `PUT /courses/{id}` - Update an existing course
- `DELETE /courses/{id}` - Delete a course

### Example Request/Response

**Create Course:**
```bash
curl -X POST http://localhost:8080/courses \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "name": "Spring Boot Fundamentals",
    "price": 99.99
  }'
```

**Response:**
```json
{
  "id": 1,
  "name": "Spring Boot Fundamentals",
  "price": 99.99
}
```

## Environment Configuration

The application supports multiple environments:

- **dev**: Development with debug logging and OpenAPI UI enabled
- **test**: Testing with minimal logging
- **prod**: Production with optimized settings and security

Configure environment using:
```bash
export SPRING_PROFILES_ACTIVE=dev
```

Or via application arguments:
```bash
java -jar target/spring-boot-migration-1.0-SNAPSHOT.jar --spring.profiles.active=dev
```

## Containerization

### Build Docker Image

```bash
docker build -t spring-boot-migration:latest .
```

### Run Container

```bash
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev spring-boot-migration:latest
```

## Azure Deployment

### Prerequisites

- Azure CLI installed and logged in
- Azure subscription with appropriate permissions

### 1. Deploy Development Environment

```bash
# Create resource group
az group create --name springboot-migration-dev --location "Southeast Asia"

# Deploy infrastructure
az deployment group create \
  --resource-group springboot-migration-dev \
  --template-file infrastructure/main.bicep \
  --parameters @infrastructure/dev.parameters.json
```

### 2. Build and Push Container Image

```bash
# Get ACR login server from deployment output
ACR_LOGIN_SERVER=$(az deployment group show \
  --resource-group springboot-migration-dev \
  --name main \
  --query properties.outputs.acrLoginServer.value -o tsv)

# Build and tag image
docker build -t ${ACR_LOGIN_SERVER}/spring-boot-migration:latest .

# Login to ACR
az acr login --name $(echo $ACR_LOGIN_SERVER | cut -d'.' -f1)

# Push image
docker push ${ACR_LOGIN_SERVER}/spring-boot-migration:latest
```

### 3. Update Container App with New Image

```bash
# Update container app
az containerapp update \
  --resource-group springboot-migration-dev \
  --name springboot-migration-dev-app \
  --image ${ACR_LOGIN_SERVER}/spring-boot-migration:latest
```

### 4. Production Deployment

For production deployment with PostgreSQL database:

```bash
# Create production resource group
az group create --name springboot-migration-prod --location "Southeast Asia"

# Deploy production infrastructure
az deployment group create \
  --resource-group springboot-migration-prod \
  --template-file infrastructure/production.bicep \
  --parameters @infrastructure/prod.parameters.json
```

## Monitoring and Logging

The application includes:

- **Health checks**: Kubernetes-style liveness and readiness probes
- **Structured logging**: JSON-formatted logs with correlation IDs
- **Azure Monitor integration**: Automatic log collection and metrics
- **Application Insights**: Distributed tracing and performance monitoring

Access logs via Azure Portal:
1. Navigate to your Container App
2. Go to "Logs" section
3. Use KQL queries to analyze application logs

## Architecture Migration Notes

This project was migrated from AWS Lambda to containerized Spring Boot:

### Key Changes Made:
- ✅ Replaced AWS Lambda handler with standard Spring Boot application
- ✅ Migrated from serverless to containerized architecture
- ✅ Implemented Clean Architecture patterns
- ✅ Added comprehensive API documentation
- ✅ Enhanced error handling and validation
- ✅ Containerized with Docker for Azure deployment
- ✅ Infrastructure as Code with Bicep templates
- ✅ Multi-environment configuration

### Benefits of Migration:
- **Improved development experience**: Standard Spring Boot development workflow
- **Better testability**: Comprehensive unit and integration testing
- **Enhanced observability**: Structured logging and monitoring
- **Scalability**: Horizontal scaling with Azure Container Apps
- **Portability**: Can run on Azure Container Apps, AKS, or App Services

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Troubleshooting

### Common Issues

1. **Port already in use**: Change the port in `application.yml` or set `SERVER_PORT` environment variable
2. **Docker build fails**: Ensure Docker is running and you have sufficient disk space
3. **Azure deployment fails**: Verify Azure CLI is logged in and you have appropriate permissions

### Logs and Debugging

- **Local development**: Check console output for detailed error messages
- **Container logs**: `docker logs <container-id>`
- **Azure logs**: Use Azure Portal or Azure CLI to access Container App logs

For more detailed troubleshooting, enable debug logging:
```yaml
logging:
  level:
    com.example.migration: DEBUG
```