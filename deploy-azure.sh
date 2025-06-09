#!/bin/bash

# Azure Container Apps Deployment Script
# This script automates the deployment of the Spring Boot application to Azure Container Apps

set -e

# Configuration
RESOURCE_GROUP="rg-springboot"
LOCATION="eastus"
ACR_NAME="myacr"
APP_NAME="springboot-api"
ENVIRONMENT_NAME="aca-env"
IMAGE_TAG="1.0.0"

echo "=== Azure Container Apps Deployment Script ==="
echo "Resource Group: $RESOURCE_GROUP"
echo "Location: $LOCATION"
echo "ACR Name: $ACR_NAME"
echo "App Name: $APP_NAME"
echo "Image Tag: $IMAGE_TAG"
echo

# Step 1: Create Resource Group
echo "Step 1: Creating resource group..."
az group create \
  --name $RESOURCE_GROUP \
  --location $LOCATION \
  --output table

# Step 2: Create Azure Container Registry
echo "Step 2: Creating Azure Container Registry..."
az acr create \
  --name $ACR_NAME \
  --resource-group $RESOURCE_GROUP \
  --sku Basic \
  --admin-enabled true \
  --output table

# Step 3: Build and push Docker image to ACR
echo "Step 3: Building and pushing Docker image..."
az acr build \
  --registry $ACR_NAME \
  --image springboot-api:$IMAGE_TAG \
  --file Dockerfile \
  .

# Step 4: Create Container App Environment
echo "Step 4: Creating Container App Environment..."
az containerapp env create \
  --name $ENVIRONMENT_NAME \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --output table

# Step 5: Deploy Container App using ARM template
echo "Step 5: Deploying Container App..."
az deployment group create \
  --resource-group $RESOURCE_GROUP \
  --template-file azure-container-app-template.json \
  --parameters \
    containerAppName=$APP_NAME \
    environmentName=$ENVIRONMENT_NAME \
    acrName=$ACR_NAME \
    imageTag=$IMAGE_TAG \
  --output table

# Step 6: Get the application URL
echo "Step 6: Getting application URL..."
FQDN=$(az containerapp show \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --query properties.configuration.ingress.fqdn \
  --output tsv)

echo
echo "=== Deployment Complete ==="
echo "Application URL: https://$FQDN"
echo "Health Check: https://$FQDN/ping"
echo "API Documentation: https://$FQDN/swagger-ui.html"
echo

# Step 7: Test the deployment
echo "Step 7: Testing the deployment..."
echo "Testing health endpoint..."
curl -f "https://$FQDN/ping" || echo "Health check failed"

echo
echo "Deployment script completed successfully!"