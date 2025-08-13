# Baheka Sentinel - Quick Start Guide

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Make (recommended)
- 4GB+ RAM available for Docker
- Git

### 1. Clone and Start Core Services (Recommended)
```bash
# Clone the repository
git clone <repository-url>
cd baheka-sentinel

# Start lightweight core services (~2GB RAM)
make up

# OR without Make:
docker compose --profile core up -d
```

### 2. Verify Services
```bash
# Check service status
make status

# Check health
make health

# View all available URLs
make urls
```

### 3. Access the Platform
```bash
# Open dashboard in browser
open http://localhost:3000

# API Gateway
curl http://localhost:8080/actuator/health

# Keycloak Admin Console
open http://localhost:8081
```

## 📊 Service URLs

| Service | URL | Profile | Credentials |
|---------|-----|---------|-------------|
| **Dashboard** | http://localhost:3000 | `core` | Auto-login |
| **API Gateway** | http://localhost:8080 | `core` | JWT required |
| **Keycloak Admin** | http://localhost:8081 | `core` | admin/admin123 |
| **Database** | localhost:5432 | `core` | sentinel/sentinel |
| **Kafka UI** | http://localhost:8083 | `stream` | No auth |
| **Search** | http://localhost:9200 | `search` | No auth |
| **WireMock** | http://localhost:8089 | `sandbox` | No auth |
| **Grafana** | http://localhost:3001 | `monitoring` | admin/admin123 |

## 🔧 Profile-Based Development

### Core Profile (Always Start Here)
```bash
make up
# Services: API, UI, Database, Authentication, Cache
# RAM: ~2GB
# Use for: Basic development, UI work, API testing
```

### Add Streaming for AML Work
```bash
make stream
# Additional: Redpanda (Kafka), Kafka UI
# RAM: +512MB
# Use for: Event processing, transaction monitoring
```

### Add Search for Analytics
```bash
make search  
# Additional: OpenSearch, Dashboards
# RAM: +512MB
# Use for: Log analysis, search features, dashboards
```

### Add Workflow Engine
```bash
make workflow
# Additional: Zeebe, Camunda Operate
# RAM: +512MB  
# Use for: Business process automation, approval workflows
```

### Add Monitoring Stack
```bash
make monitoring
# Additional: Prometheus, Grafana
# RAM: +256MB
# Use for: Performance testing, metrics collection
```

### Add External API Mocking
```bash
make sandbox
# Additional: WireMock
# RAM: +128MB
# Use for: Development without external dependencies
```

## 🏗️ Architecture Overview

### Microservices
- **API Gateway** - Authentication, routing, rate limiting
- **Risk Service** - Credit risk, VaR, Basel III calculations  
- **AML Service** - Transaction monitoring, sanctions screening
- **Compliance Service** - Regulatory reporting, audit trails
- **Security Service** - IAM, access control, security events

### Infrastructure
- **PostgreSQL** - Primary database (optimized for containers)
- **Redis** - Caching and session storage
- **Redpanda** - Kafka-compatible streaming (single node)
- **OpenSearch** - Search and analytics (single node)
- **Keycloak** - Identity and access management (dev mode)
- **OPA** - Policy engine for authorization

### Frontend
- **React 18** with TypeScript
- **Tailwind CSS** for styling
- **React Query** for data fetching
- **React Router** for navigation

## 📈 Development Workflow

### Daily Development
```bash
# Start minimal stack
make up

# Work on your features...

# View logs
make logs

# Stop when done
make down
```

### AML/Streaming Development
```bash
# Start core + streaming
make dev
# OR: make up && make stream

# Test message processing
# Kafka UI available at http://localhost:8083
```

### Full Stack Testing
```bash
# Start everything (rarely needed)
make all

# Monitor resources
make stats

# Stop when done
make clean
```

## 🛠️ Development Commands

### Service Management
```bash
make up          # Start core services
make down        # Stop all services  
make restart     # Restart running services
make clean       # Stop and remove volumes
make build       # Build application images
```

### Monitoring & Debugging
```bash
make status      # Show service status
make health      # Check service health
make logs        # Show all logs
make stats       # Show resource usage
make urls        # List all service URLs
```

### Database Operations
```bash
make db-shell    # Connect to PostgreSQL
make db-reset    # Reset database (destroys data!)
```

### Individual Service Logs
```bash
make backend-logs   # API Gateway logs
make frontend-logs  # React app logs
make db-logs       # Database logs
```

## 🧪 Testing External APIs

### Sandbox Mode
```bash
# Start sandbox services
make sandbox

# Test sanctions screening
curl -X POST http://localhost:8089/sanctions/screen \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "country": "US"}'

# Test KYC verification  
curl -X POST http://localhost:8089/kyc/verify \
  -H "Content-Type: application/json" \
  -d '{"customerId": "12345", "documentType": "PASSPORT"}'

# Test PEP screening
curl "http://localhost:8089/pep/search?name=politician"
```

## 📊 Resource Management

### Memory Usage by Profile
- **Core**: ~2GB (API + UI + DB + Auth)
- **+ Stream**: +512MB (Redpanda)
- **+ Search**: +512MB (OpenSearch)  
- **+ Workflow**: +512MB (Zeebe)
- **+ Monitoring**: +256MB (Prometheus/Grafana)
- **+ Sandbox**: +128MB (WireMock)

### Optimization Tips
- Start with `core` profile only
- Add profiles as needed for specific work
- Use `make clean` to free resources
- Monitor with `make stats`

## 🔐 Security Configuration

### Default Credentials
- **Keycloak Admin**: admin/admin123
- **Grafana**: admin/admin123  
- **Database**: sentinel/sentinel

### JWT Configuration
- **Issuer**: http://localhost:8081/realms/baheka-sentinel
- **Algorithms**: RS256
- **Token expiry**: 15 minutes (configurable)

### Role-Based Access
- **admin**: Full access to all APIs
- **risk_manager**: Risk service access
- **aml_analyst**: AML service access
- **compliance_officer**: Compliance service access
- **viewer**: Read-only access

## 🔧 Configuration

### Environment Variables
```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sentinel
DB_USER=sentinel
DB_PASSWORD=sentinel

# Kafka (when using stream profile)
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Keycloak
KEYCLOAK_URL=http://localhost:8081
KEYCLOAK_REALM=baheka-sentinel
```

## 📞 Support

### Health Checks
```bash
# Overall health
make health

# Individual service health
curl http://localhost:8080/actuator/health  # API
curl http://localhost:3000                  # Frontend  
curl http://localhost:8081/health/ready     # Keycloak
```

### Common Issues
1. **Port conflicts**: Check if ports 3000, 8080, 8081 are available
2. **Memory issues**: Reduce profiles or increase Docker memory limit
3. **Database connection**: Ensure PostgreSQL is healthy (`make health`)
4. **Authentication**: Check Keycloak is running on port 8081

### Getting Help
- **Logs**: `make logs` for all services
- **Status**: `make status` for service overview
- **Documentation**: Available in `docs/` directory
- **API Docs**: http://localhost:8080/swagger-ui.html

---

**Baheka Sentinel** - Start lightweight, scale as needed!
