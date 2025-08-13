# Baheka Sentinel
## Integrated Risk, Compliance & Security Platform for Financial Institutions

Baheka Sentinel is a comprehensive platform delivering real-time risk management, fraud detection, automated compliance, and bank-grade security for Tier 1 & 2 banks, microfinance institutions, and payment processors.

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Make (optional, for convenience)
- 4GB+ RAM available for Docker

### Lightweight Development Setup

```bash
# Clone and start core services only
git clone <repository-url>
cd baheka-sentinel

# Start core services (API, UI, DB, Auth)
make up
# OR: docker compose --profile core up -d

# Access the platform
open http://localhost:3000  # Dashboard
open http://localhost:8080  # API Gateway
open http://localhost:8081  # Keycloak Admin
```

### Profile-Based Development

Start only what you need:

```bash
# Core only (lightweight) - ~2GB RAM
make up

# Add streaming when working on AML/events
make stream
# OR: docker compose --profile stream up -d

# Add search for analytics work
make search

# Add workflow for complex business processes
make workflow

# Add monitoring for performance testing
make monitoring

# Add sandbox for external API mocking
make sandbox

# Full stack (development only)
make all
```

## �️ Architecture

### Technology Stack
- **Backend**: Java 21 + Spring Boot 3 + Kotlin
- **Frontend**: React 18 + TypeScript + Tailwind CSS
- **Streaming**: Redpanda (Kafka-compatible, single node)
- **Database**: PostgreSQL 16 + Redis
- **Search**: OpenSearch (single node)
- **Security**: Keycloak + OPA (Open Policy Agent)
- **Workflow**: Camunda 8 / Zeebe
- **Monitoring**: Prometheus + Grafana
- **Deployment**: Docker + Multi-stage builds

### Service Profiles

| Profile | Services | Resources | Use Case |
|---------|----------|-----------|----------|
| `core` | API, UI, DB, Auth, Cache | ~2GB RAM | Daily development |
| `stream` | + Redpanda, Kafka UI | +512MB | Event streaming work |
| `search` | + OpenSearch, Dashboards | +512MB | Analytics development |
| `workflow` | + Zeebe, Operate | +512MB | Business process work |
| `monitoring` | + Prometheus, Grafana | +256MB | Performance testing |
| `sandbox` | + WireMock | +128MB | External API mocking |

## �🏦 Product Modules

### A. Sentinel Risk
- Credit, market, and operational risk monitoring
- Real-time risk scoring with ML models
- Basel III/IV capital ratio calculations
- VaR and stress testing

### B. Sentinel AML
- Real-time fraud detection
- Sanctions screening (OFAC, UN, EU, FIC SA)
- Transaction monitoring with AI anomaly detection
- SAR filing automation

### C. Sentinel Compliance
- Automated regulatory reporting (XBRL, CSV)
- Policy tracking and attestation workflows
- Audit-ready evidence vault
- POPIA/FIC Act compliance

### D. Sentinel Secure
- Multi-factor authentication (MFA)
- Identity and Access Management (IAM)
- Privileged Access Management (PAM)
- Endpoint security integration

## 📊 Service URLs

| Service | URL | Profile | Description |
|---------|-----|---------|-------------|
| **Frontend** | http://localhost:3000 | `core` | React Dashboard |
| **API Gateway** | http://localhost:8080 | `core` | Main API Entry |
| **Keycloak** | http://localhost:8081 | `core` | Identity Management |
| **Kafka UI** | http://localhost:8083 | `stream` | Message Queue UI |
| **OpenSearch** | http://localhost:9200 | `search` | Search API |
| **Search Dashboard** | http://localhost:5601 | `search` | Kibana-like UI |
| **Camunda Operate** | http://localhost:8084 | `workflow` | Workflow Management |
| **WireMock** | http://localhost:8089 | `sandbox` | API Mocking |
| **Prometheus** | http://localhost:9090 | `monitoring` | Metrics Collection |
| **Grafana** | http://localhost:3001 | `monitoring` | Dashboards |

## 🔧 Development Commands

```bash
# Service management
make up          # Start core services
make down        # Stop all services
make restart     # Restart running services
make clean       # Stop and remove volumes

# Development helpers
make logs        # Show all logs
make status      # Show service status
make health      # Check service health
make urls        # List all URLs

# Database utilities
make db-shell    # Connect to database
make db-reset    # Reset database (WARNING: destroys data)

# Resource monitoring
make stats       # Show resource usage

# Individual service logs
make backend-logs
make frontend-logs
make db-logs
```

## 💻 Resource Optimization

### Memory Limits per Service
- **API Gateway**: 768MB (JVM optimized)
- **Frontend**: 256MB (Nginx + static files)
- **PostgreSQL**: 512MB (tuned for development)
- **Keycloak**: 512MB (dev mode)
- **Redis**: 128MB (LRU cache)
- **Redpanda**: 512MB (single node)
- **OpenSearch**: 512MB (small heap)

### JVM Optimizations
- Serial GC for low memory usage
- Container-aware heap sizing
- Tiered compilation for faster startup
- Reduced memory footprint

## 🔐 Security & Compliance

### Built-in Security
- **Authentication**: Keycloak with OIDC/SAML
- **Authorization**: OPA-based policy engine
- **Data Protection**: Field-level encryption
- **Audit Trails**: Immutable logging
- **Container Security**: Non-root users, minimal images

### Compliance Standards
- SOC 2 Type II ready
- ISO 27001 compliant
- POPIA (South Africa) compliant
- FIC Act compliant
- Basel III/IV regulatory standards

## 🧪 Sandbox & Testing

### Mock External APIs
WireMock provides mock responses for:
- **Sanctions Screening**: OFAC, UN, EU lists
- **KYC Verification**: Identity and document checks  
- **PEP Screening**: Politically Exposed Persons

### Sample API Calls
```bash
# Test sanctions screening
curl -X POST http://localhost:8089/sanctions/screen \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "country": "US"}'

# Test KYC verification
curl -X POST http://localhost:8089/kyc/verify \
  -H "Content-Type: application/json" \
  -d '{"customerId": "12345", "documentType": "PASSPORT"}'
```

## 📈 Deployment Options

### Local Development
```bash
# Lightweight setup
make up

# With streaming for AML work
make dev
```

### Cloud Deployment
- **AWS**: EKS with Fargate
- **Azure**: AKS with virtual nodes
- **GCP**: GKE Autopilot
- **On-premise**: Kubernetes + Docker

### Production Considerations
- Use managed databases (RDS, Azure Database)
- External Kafka clusters (MSK, Event Hubs)
- Load balancers and auto-scaling
- Secrets management (Vault, Key Vault)

## 💰 Target Market & Monetization

### Target Clients
- **Tier 1 & 2 Banks**: Comprehensive compliance platform
- **Microfinance & Digital Banks**: Affordable, cloud-ready tools
- **Payment Processors**: Real-time fraud detection

### Revenue Model
- **Enterprise License**: $50K-$500K annually per bank
- **Implementation Services**: $25K-$200K one-time
- **Compliance-as-a-Service**: $5K-$50K monthly for smaller institutions

## � Development Status

### ✅ Completed (MVP)
- [x] Profile-based containerization
- [x] Microservices architecture
- [x] React dashboard with TypeScript
- [x] API Gateway with routing
- [x] Database schema and migrations
- [x] Identity management (Keycloak)
- [x] Policy engine (OPA)
- [x] Resource optimization
- [x] Development tooling

### 🔄 In Progress
- [ ] Risk calculation microservice
- [ ] AML transaction monitoring
- [ ] Compliance reporting engine
- [ ] ML model integration
- [ ] Real-time dashboards

### 📋 Roadmap
- [ ] Advanced ML fraud detection
- [ ] Multi-tenant architecture
- [ ] Mobile application
- [ ] API marketplace
- [ ] White-label solutions

## 📞 Support

- **Documentation**: Available in `docs/` directory
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Health Checks**: `make health`
- **Logs**: `make logs`

## 🤝 Contributing

1. Start with core profile for development
2. Add specific profiles as needed
3. Follow containerization best practices
4. Test with resource constraints
5. Update documentation

---

**Baheka Sentinel** - Transforming financial risk management with lightweight, scalable, and compliant technology.

For support: support@baheka.com
