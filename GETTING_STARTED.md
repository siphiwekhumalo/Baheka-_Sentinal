# Baheka Sentinel - Quick Start Guide

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 17+
- Node.js 18+
- Git

### 1. Start Infrastructure Services
```bash
# Start all infrastructure (Kafka, PostgreSQL, Redis, etc.)
docker-compose up -d

# Wait for services to be ready (about 2-3 minutes)
./scripts/start-services.sh
```

### 2. Install Frontend Dependencies
```bash
cd frontend
npm install
```

### 3. Start Backend Services
```bash
# Each service runs on different ports:
# Gateway: 8080
# Risk: 8081
# AML: 8082
# Compliance: 8083
# Security: 8084
# Notification: 8085

cd backend
./gradlew :services:sentinel-gateway:bootRun &
./gradlew :services:sentinel-risk:bootRun &
./gradlew :services:sentinel-aml:bootRun &
```

### 4. Start Frontend
```bash
cd frontend
npm start
```

## 📊 Service URLs

| Service | URL | Description |
|---------|-----|-------------|
| Frontend | http://localhost:3000 | React Dashboard |
| API Gateway | http://localhost:8080 | Main API Entry |
| Risk Service | http://localhost:8081 | Risk Management |
| AML Service | http://localhost:8082 | AML Monitoring |
| Keycloak | http://localhost:8080/auth | Identity Management |
| Kafka UI | http://localhost:8082 | Message Queue Management |
| Grafana | http://localhost:3000 | Monitoring Dashboard |
| Prometheus | http://localhost:9090 | Metrics Collection |

## 🔐 Default Credentials

| Service | Username | Password |
|---------|----------|----------|
| Keycloak Admin | admin | admin123 |
| Grafana | admin | admin123 |
| PostgreSQL | baheka_user | baheka_password |

## 🏗️ Architecture Overview

### Microservices
- **API Gateway** - Routes and load balancing
- **Risk Service** - Credit risk, VaR, Basel III calculations
- **AML Service** - Transaction monitoring, sanctions screening
- **Compliance Service** - Regulatory reporting, audit trails
- **Security Service** - IAM, access control, security events
- **Notification Service** - Alerts, emails, SMS

### Infrastructure
- **Kafka** - Event streaming and messaging
- **PostgreSQL** - Primary database with TimescaleDB
- **Redis** - Caching and session storage
- **Elasticsearch** - Search and analytics
- **Keycloak** - Identity and access management
- **Vault** - Secrets management

### Frontend
- **React 18** with TypeScript
- **Tailwind CSS** for styling
- **React Query** for data fetching
- **React Router** for navigation

## 📈 MVP Features

### ✅ Implemented
- [x] Microservices architecture
- [x] Docker containerization
- [x] Database schema and migrations
- [x] Risk calculation service
- [x] Basic React frontend
- [x] API Gateway routing
- [x] Monitoring with Prometheus/Grafana
- [x] Message streaming with Kafka

### 🚧 In Development
- [ ] AML transaction monitoring rules
- [ ] Compliance reporting engine
- [ ] Security policy management
- [ ] ML model integration
- [ ] Real-time dashboards
- [ ] Authentication integration

### 📋 Roadmap
- [ ] Advanced ML fraud detection
- [ ] Real-time sanctions screening
- [ ] Automated regulatory reports
- [ ] Mobile application
- [ ] API marketplace
- [ ] Multi-tenant architecture

## 🛠️ Development

### Adding New Features
1. Create feature branch: `git checkout -b feature/new-feature`
2. Implement backend service changes
3. Update frontend components
4. Add tests
5. Update documentation
6. Create pull request

### Running Tests
```bash
# Backend tests
cd backend
./gradlew test

# Frontend tests
cd frontend
npm test
```

### Database Migrations
```bash
# Create new migration
cd backend/services/sentinel-risk
./gradlew flywayMigrate
```

## 🔧 Configuration

### Environment Variables
```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=baheka_sentinel
DB_USER=baheka_user
DB_PASSWORD=baheka_password

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Keycloak
KEYCLOAK_URL=http://localhost:8080/auth
KEYCLOAK_REALM=baheka-sentinel
```

## 📞 Support

- Documentation: `docs/`
- API Documentation: http://localhost:8080/swagger-ui.html
- Issues: GitHub Issues
- Email: support@baheka.com

## 🚀 Deployment

### Production Deployment
1. Configure environment variables
2. Build Docker images
3. Deploy to Kubernetes
4. Set up monitoring and alerting
5. Configure backup and recovery

### Kubernetes Deployment
```bash
# Apply Kubernetes manifests
kubectl apply -f k8s/

# Check deployment status
kubectl get pods -n baheka-sentinel
```

---

**Baheka Sentinel** - Empowering financial institutions with comprehensive risk, compliance, and security management.
