.PHONY: help up down logs clean build dev stream search workflow monitoring sandbox all stop

# Default target
help: ## Show this help message
	@echo "Baheka Sentinel - Container Management"
	@echo "======================================"
	@echo "Available commands:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-20s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

# Core profile (lightweight development)
up: ## Start core services only (API, UI, DB, Keycloak, OPA)
	@echo "🚀 Starting Baheka Sentinel core services..."
	docker compose --profile core up -d
	@echo "✅ Core services started!"
	@echo "   • Dashboard: http://localhost:3000"
	@echo "   • API: http://localhost:8080"
	@echo "   • Keycloak: http://localhost:8081"

# Development with streaming
dev: ## Start core + streaming (Redpanda)
	@echo "🚀 Starting development environment with streaming..."
	docker compose --profile core --profile stream up -d
	@echo "✅ Development environment ready!"
	@echo "   • Dashboard: http://localhost:3000"
	@echo "   • API: http://localhost:8080"
	@echo "   • Kafka UI: http://localhost:8083"

# Add streaming to running core
stream: ## Add streaming services to running environment
	@echo "📡 Adding streaming services..."
	docker compose --profile stream up -d
	@echo "✅ Streaming services added!"
	@echo "   • Kafka UI: http://localhost:8083"

# Add search capabilities
search: ## Add search services (OpenSearch)
	@echo "🔍 Adding search services..."
	docker compose --profile search up -d
	@echo "✅ Search services added!"
	@echo "   • OpenSearch: http://localhost:9200"
	@echo "   • Dashboards: http://localhost:5601"

# Add workflow engine
workflow: ## Add workflow services (Camunda/Zeebe)
	@echo "⚙️ Adding workflow services..."
	docker compose --profile workflow up -d
	@echo "✅ Workflow services added!"
	@echo "   • Zeebe Gateway: localhost:26500"
	@echo "   • Operate: http://localhost:8084"

# Add monitoring
monitoring: ## Add monitoring services (Prometheus/Grafana)
	@echo "📊 Adding monitoring services..."
	docker compose --profile monitoring up -d
	@echo "✅ Monitoring services added!"
	@echo "   • Prometheus: http://localhost:9090"
	@echo "   • Grafana: http://localhost:3001 (admin/admin123)"

# Add sandbox mocking
sandbox: ## Add sandbox services (WireMock)
	@echo "🏖️ Adding sandbox services..."
	docker compose --profile sandbox up -d
	@echo "✅ Sandbox services added!"
	@echo "   • WireMock: http://localhost:8089"

# Full stack (rarely needed)
all: ## Start all services (heavy on resources)
	@echo "🚀 Starting full Baheka Sentinel stack..."
	docker compose --profile core --profile stream --profile search --profile workflow --profile monitoring --profile sandbox up -d
	@echo "✅ Full stack started!"
	@make status

# Stop all services
down: ## Stop all services
	@echo "⏹️ Stopping all services..."
	docker compose down
	@echo "✅ All services stopped!"

# Stop and remove volumes
clean: ## Stop services and remove volumes
	@echo "🧹 Cleaning up..."
	docker compose down -v
	docker system prune -f
	@echo "✅ Cleanup complete!"

# Build images
build: ## Build all application images
	@echo "🔨 Building application images..."
	docker compose build --parallel
	@echo "✅ Images built!"

# Show logs
logs: ## Show logs for all running services
	docker compose logs -f

# Show service status
status: ## Show status of all services
	@echo "📊 Service Status:"
	@echo "=================="
	@docker compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"

# Quick restart
restart: ## Restart all running services
	@echo "🔄 Restarting services..."
	docker compose restart
	@echo "✅ Services restarted!"

# Resource usage
stats: ## Show resource usage
	@echo "💻 Resource Usage:"
	@echo "=================="
	@docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}"

# Development helpers
backend-logs: ## Show backend service logs
	docker compose logs -f api

frontend-logs: ## Show frontend service logs
	docker compose logs -f ui

db-logs: ## Show database logs
	docker compose logs -f db

# Database utilities
db-shell: ## Connect to database shell
	docker compose exec db psql -U sentinel -d sentinel

# Reset database
db-reset: ## Reset database (WARNING: destroys data)
	@echo "⚠️ This will destroy all data! Press Ctrl+C to cancel..."
	@sleep 5
	docker compose stop db
	docker volume rm baheka-_sentinal_pgdata || true
	docker compose up -d db
	@echo "✅ Database reset complete!"

# Health check
health: ## Check health of all services
	@echo "🏥 Health Check:"
	@echo "================"
	@curl -s http://localhost:8080/actuator/health | jq -r '.status // "UNKNOWN"' | sed 's/^/API: /'
	@curl -s http://localhost:3000 > /dev/null && echo "UI: UP" || echo "UI: DOWN"
	@docker compose exec -T db pg_isready -U sentinel 2>/dev/null && echo "DB: UP" || echo "DB: DOWN"

# Show URLs
urls: ## Show all service URLs
	@echo "🌐 Service URLs:"
	@echo "================"
	@echo "Dashboard:        http://localhost:3000"
	@echo "API Gateway:      http://localhost:8080"
	@echo "Keycloak:         http://localhost:8081"
	@echo "Kafka UI:         http://localhost:8083"
	@echo "OpenSearch:       http://localhost:9200"
	@echo "Search Dashboard: http://localhost:5601"
	@echo "Camunda Operate:  http://localhost:8084"
	@echo "WireMock:         http://localhost:8089"
	@echo "Prometheus:       http://localhost:9090"
	@echo "Grafana:          http://localhost:3001"
