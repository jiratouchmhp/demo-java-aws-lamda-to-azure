#!/bin/bash
set -e

# Configuration
RESOURCE_GROUP="rg-springboot"
LOCATION="eastasia"
ACR_NAME="myacr"
ACA_ENV_NAME="aca-env"
APP_NAME="springboot-api"
IMAGE_TAG="1.0.0"

echo "Starting Azure deployment for Spring Boot Migration..."

# Build the application
echo "Building the application..."
mvn clean package -DskipTests

# Build and push Docker image to Azure Container Registry
echo "Building and pushing Docker image to ACR..."
az acr build --image $APP_NAME:$IMAGE_TAG --registry $ACR_NAME --file Dockerfile .

# Create Container App Environment if it doesn't exist
echo "Creating Container App Environment..."
az containerapp env create \
  --name $ACA_ENV_NAME \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --enable-workload-profiles false

# Deploy the application using YAML configuration
echo "Deploying application to Azure Container Apps..."
az containerapp create \
  --resource-group $RESOURCE_GROUP \
  --environment $ACA_ENV_NAME \
  --name $APP_NAME \
  --image $ACR_NAME.azurecr.io/$APP_NAME:$IMAGE_TAG \
  --target-port 8080 \
  --ingress external \
  --min-replicas 1 \
  --max-replicas 5 \
  --cpu 0.5 \
  --memory 1Gi \
  --env-vars SPRING_PROFILES_ACTIVE=prod

echo "Deployment completed successfully!"
echo "Your application should be available at the URL provided by Azure Container Apps."

# Get the application URL
echo "Getting application URL..."
az containerapp show \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --query properties.configuration.ingress.fqdn \
  --output tsv