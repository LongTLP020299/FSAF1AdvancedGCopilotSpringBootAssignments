# Payment Gateway Health Indicator Documentation

## Overview

This project implements a custom Spring Boot HealthIndicator for monitoring the Payment Gateway status. The implementation includes both custom health endpoints and Spring Boot Actuator integration.

## Components Created

### 1. PaymentGatewayHealthIndicator.java
- **Location**: `src/main/java/com/fsoft/ecommerce/health/PaymentGatewayHealthIndicator.java`
- **Purpose**: Core health checking logic that simulates payment gateway status monitoring
- **Features**:
  - Random UP/DOWN status simulation (80% uptime)
  - Detailed health metrics including response time, transaction count, success rate
  - Error handling with retry information
  - Timestamp tracking for last health check

### 2. CustomHealthController.java
- **Location**: `src/main/java/com/fsoft/ecommerce/controller/CustomHealthController.java`
- **Purpose**: REST controller exposing custom health endpoints
- **Endpoints**:
  - `GET /api/health/payment-gateway` - Payment gateway specific health
  - `GET /api/health/system` - Overall system health including payment gateway

### 3. Application Configuration
- **Location**: `src/main/resources/application.yml`
- **Purpose**: Spring Boot Actuator configuration for comprehensive health monitoring
- **Features**:
  - Always show health details: `show-details: always`
  - Show health components: `show-components: always`
  - Expose multiple actuator endpoints: health, info, metrics, env, beans, mappings, etc.

## Health Check Endpoints

### Custom Endpoints

#### Payment Gateway Health
```
GET /api/health/payment-gateway
```

**Response Examples:**

**When UP:**
```json
{
  "status": "UP",
  "gateway": "Stripe Payment Gateway",
  "version": "v2.1.0",
  "endpoint": "https://api.stripe.com/v1/charges",
  "responseTime": "120ms",
  "lastChecked": "2025-07-30T15:38:45.123",
  "transactionsProcessed": 3456,
  "successRate": "99.8%",
  "operationalStatus": "NORMAL"
}
```

**When DOWN:**
```json
{
  "status": "DOWN",
  "gateway": "Stripe Payment Gateway",
  "version": "v2.1.0",
  "endpoint": "https://api.stripe.com/v1/charges",
  "error": "Connection timeout after 5000ms",
  "lastChecked": "2025-07-30T15:38:45.123",
  "retryAttempts": 3,
  "nextRetryIn": "30 seconds",
  "operationalStatus": "SERVICE_UNAVAILABLE"
}
```

#### System Health
```
GET /api/health/system
```

**Response Example:**
```json
{
  "paymentGateway": {
    "status": "UP",
    "gateway": "Stripe Payment Gateway",
    "version": "v2.1.0",
    "endpoint": "https://api.stripe.com/v1/charges",
    "responseTime": "120ms",
    "lastChecked": "2025-07-30T15:38:45.123",
    "transactionsProcessed": 3456,
    "successRate": "99.8%",
    "operationalStatus": "NORMAL"
  },
  "database": {
    "status": "UP",
    "database": "MySQL",
    "validationQuery": "SELECT 1"
  },
  "cache": {
    "status": "UP",
    "provider": "ConcurrentMapCacheManager",
    "caches": ["product-details", "products", "searchProducts"]
  },
  "status": "UP",
  "timestamp": "2025-07-30T15:38:45.123"
}
```

### Spring Boot Actuator Endpoints

#### Main Health Endpoint
```
GET /actuator/health
```

This endpoint now includes the standard Spring Boot health indicators plus our custom components.

#### Other Actuator Endpoints
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics
- `GET /actuator/env` - Environment properties
- `GET /actuator/beans` - Spring beans information
- `GET /actuator/mappings` - Request mappings
- `GET /actuator/configprops` - Configuration properties
- `GET /actuator/loggers` - Logger configuration

## Configuration Details

### YAML Configuration
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env,beans,mappings,configprops,loggers
      base-path: /actuator
  endpoint:
    health:
      show-details: always
      show-components: always
  health:
    diskspace:
      enabled: true
    db:
      enabled: true
    defaults:
      enabled: true
  info:
    env:
      enabled: true
    java:
      enabled: true
    os:
      enabled: true
```

## Testing

### Unit Tests
- **Location**: `src/test/java/com/fsoft/ecommerce/health/PaymentGatewayHealthIndicatorTest.java`
- **Coverage**: 6 test methods covering all health check scenarios
- **Results**: All tests pass ✅

### Test Results
```
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
```

## Production Implementation Notes

### Current Implementation (Demo)
- Uses `Random` to simulate 80% uptime
- Simulates network delay with `Thread.sleep(100)`
- Returns mock transaction counts and success rates

### Production Implementation Recommendations
```java
private boolean checkPaymentGatewayStatus() {
    try {
        // Make actual HTTP call to payment provider's health endpoint
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(
            "https://api.stripe.com/v1/account", String.class);
        
        return response.getStatusCode().is2xxSuccessful();
    } catch (Exception e) {
        logger.error("Payment gateway health check failed", e);
        return false;
    }
}
```

### Additional Production Features to Consider
1. **Circuit Breaker Pattern**: Implement using Spring Cloud Circuit Breaker
2. **Caching**: Cache health status for a few seconds to avoid overwhelming the payment provider
3. **Metrics**: Integration with Micrometer for detailed metrics collection
4. **Alerting**: Integration with monitoring systems (Prometheus, Grafana)
5. **Timeouts**: Configurable timeouts for health checks
6. **Authentication**: Secure health endpoints in production environments

## Usage Examples

### Testing with curl
```bash
# Test payment gateway health
curl http://localhost:8080/api/health/payment-gateway

# Test system health
curl http://localhost:8080/api/health/system

# Test Spring Boot actuator health
curl http://localhost:8080/actuator/health
```

### Integration with Monitoring
The health endpoints can be easily integrated with:
- **Kubernetes**: Liveness and readiness probes
- **Load Balancers**: Health checks for routing decisions
- **Monitoring Systems**: Prometheus scraping, Grafana dashboards
- **CI/CD Pipelines**: Health verification during deployments

## Summary

✅ **PaymentGatewayHealthIndicator** - Custom health indicator with simulation
✅ **CustomHealthController** - REST endpoints for health monitoring  
✅ **Spring Boot Actuator Configuration** - Full health details enabled
✅ **Comprehensive Testing** - Unit tests with 100% pass rate
✅ **Documentation** - Complete API documentation and usage examples
✅ **Production Ready Architecture** - Clear path to production implementation
