@description('The location for all resources')
param location string = 'Southeast Asia'

@description('Base name for resources')
param baseName string = 'springboot-migration'

@description('Environment name (test or prod)')
@allowed(['test', 'prod'])
param environment string = 'test'

@description('Container image to deploy')
param containerImage string

@description('Database administrator login')
@secure()
param dbAdminLogin string

@description('Database administrator password')
@secure()
param dbAdminPassword string

var resourceSuffix = '${baseName}-${environment}'
var acrName = replace('${resourceSuffix}acr', '-', '')
var logAnalyticsName = '${resourceSuffix}-logs'
var containerAppEnvName = '${resourceSuffix}-env'
var containerAppName = '${resourceSuffix}-app'
var postgresName = '${resourceSuffix}-postgres'
var keyVaultName = '${resourceSuffix}-kv'

// Azure Container Registry
resource acr 'Microsoft.ContainerRegistry/registries@2023-01-01-preview' = {
  name: acrName
  location: location
  sku: {
    name: 'Standard'
  }
  properties: {
    adminUserEnabled: true
  }
  tags: {
    Environment: environment
    Application: 'spring-boot-migration'
  }
}

// Log Analytics Workspace
resource logAnalytics 'Microsoft.OperationalInsights/workspaces@2021-12-01-preview' = {
  name: logAnalyticsName
  location: location
  sku: {
    name: 'PerGB2018'
  }
  properties: {
    retentionInDays: environment == 'prod' ? 90 : 30
  }
  tags: {
    Environment: environment
    Application: 'spring-boot-migration'
  }
}

// Key Vault for secrets management
resource keyVault 'Microsoft.KeyVault/vaults@2023-02-01' = {
  name: keyVaultName
  location: location
  properties: {
    tenantId: tenant().tenantId
    sku: {
      family: 'A'
      name: 'standard'
    }
    accessPolicies: []
    enabledForDeployment: false
    enabledForDiskEncryption: false
    enabledForTemplateDeployment: true
  }
  tags: {
    Environment: environment
    Application: 'spring-boot-migration'
  }
}

// PostgreSQL Flexible Server
resource postgres 'Microsoft.DBforPostgreSQL/flexibleServers@2022-12-01' = {
  name: postgresName
  location: location
  sku: {
    name: environment == 'prod' ? 'Standard_D2s_v3' : 'Standard_B1ms'
    tier: environment == 'prod' ? 'GeneralPurpose' : 'Burstable'
  }
  properties: {
    administratorLogin: dbAdminLogin
    administratorLoginPassword: dbAdminPassword
    storage: {
      storageSizeGB: environment == 'prod' ? 128 : 32
    }
    version: '14'
    backup: {
      backupRetentionDays: environment == 'prod' ? 30 : 7
      geoRedundantBackup: environment == 'prod' ? 'Enabled' : 'Disabled'
    }
    highAvailability: environment == 'prod' ? {
      mode: 'ZoneRedundant'
    } : {
      mode: 'Disabled'
    }
  }
  tags: {
    Environment: environment
    Application: 'spring-boot-migration'
  }
}

// PostgreSQL Database
resource postgresDatabase 'Microsoft.DBforPostgreSQL/flexibleServers/databases@2022-12-01' = {
  parent: postgres
  name: 'springbootmigration'
  properties: {
    charset: 'UTF8'
    collation: 'en_US.utf8'
  }
}

// Container Apps Environment
resource containerEnv 'Microsoft.App/managedEnvironments@2023-05-01' = {
  name: containerAppEnvName
  location: location
  properties: {
    appLogsConfiguration: {
      destination: 'log-analytics'
      logAnalyticsConfiguration: {
        customerId: logAnalytics.properties.customerId
        sharedKey: listKeys(logAnalytics.id, logAnalytics.apiVersion).primarySharedKey
      }
    }
  }
  tags: {
    Environment: environment
    Application: 'spring-boot-migration'
  }
}

// Container App
resource containerApp 'Microsoft.App/containerApps@2023-05-01' = {
  name: containerAppName
  location: location
  properties: {
    managedEnvironmentId: containerEnv.id
    configuration: {
      activeRevisionsMode: 'Single'
      ingress: {
        external: true
        targetPort: 8080
        allowInsecure: false
        traffic: [
          {
            weight: 100
            latestRevision: true
          }
        ]
      }
      registries: [
        {
          server: acr.properties.loginServer
          username: acr.listCredentials().username
          passwordSecretRef: 'acr-password'
        }
      ]
      secrets: [
        {
          name: 'acr-password'
          value: acr.listCredentials().passwords[0].value
        }
        {
          name: 'db-connection-string'
          value: 'postgresql://${dbAdminLogin}:${dbAdminPassword}@${postgres.properties.fullyQualifiedDomainName}:5432/${postgresDatabase.name}?sslmode=require'
        }
      ]
    }
    template: {
      containers: [
        {
          name: 'spring-boot-app'
          image: containerImage
          resources: {
            cpu: environment == 'prod' ? json('1.0') : json('0.5')
            memory: environment == 'prod' ? '2Gi' : '1Gi'
          }
          env: [
            {
              name: 'SPRING_PROFILES_ACTIVE'
              value: environment
            }
            {
              name: 'SPRING_DATASOURCE_URL'
              secretRef: 'db-connection-string'
            }
          ]
          probes: [
            {
              type: 'Liveness'
              httpGet: {
                path: '/ping'
                port: 8080
                scheme: 'HTTP'
              }
              initialDelaySeconds: 30
              periodSeconds: 10
              timeoutSeconds: 5
              failureThreshold: 3
            }
            {
              type: 'Readiness'
              httpGet: {
                path: '/ping'
                port: 8080
                scheme: 'HTTP'
              }
              initialDelaySeconds: 10
              periodSeconds: 5
              timeoutSeconds: 3
              failureThreshold: 3
            }
          ]
        }
      ]
      scale: {
        minReplicas: environment == 'prod' ? 2 : 1
        maxReplicas: environment == 'prod' ? 10 : 3
        rules: [
          {
            name: 'http-scaling'
            http: {
              metadata: {
                concurrentRequests: '100'
              }
            }
          }
        ]
      }
    }
  }
  tags: {
    Environment: environment
    Application: 'spring-boot-migration'
  }
}

// Outputs
output acrLoginServer string = acr.properties.loginServer
output acrName string = acr.name
output containerAppUrl string = 'https://${containerApp.properties.configuration.ingress.fqdn}'
output logAnalyticsWorkspaceId string = logAnalytics.properties.customerId
output environmentName string = containerEnv.name
output keyVaultName string = keyVault.name
output postgresServerName string = postgres.name
output postgresDatabaseName string = postgresDatabase.name