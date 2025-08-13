#!/bin/bash

# Baheka Sentinel - Start All Services Script
# This script starts all microservices in the correct order

set -e

echo "🏦 Starting Baheka Sentinel Platform..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker first."
    exit 1
fi

# Start infrastructure services
print_status "Starting infrastructure services..."
docker-compose up -d zookeeper kafka schema-registry postgres redis elasticsearch keycloak vault

# Wait for services to be healthy
print_status "Waiting for services to be ready..."
sleep 30

# Check Kafka is ready
print_status "Checking Kafka connectivity..."
until docker exec baheka-kafka kafka-topics --bootstrap-server localhost:9092 --list > /dev/null 2>&1; do
    print_warning "Waiting for Kafka to be ready..."
    sleep 5
done

# Check PostgreSQL is ready
print_status "Checking PostgreSQL connectivity..."
until docker exec baheka-postgres pg_isready -U baheka_user -d baheka_sentinel > /dev/null 2>&1; do
    print_warning "Waiting for PostgreSQL to be ready..."
    sleep 5
done

# Create Kafka topics
print_status "Creating Kafka topics..."
docker exec baheka-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic transactions --partitions 3 --replication-factor 1 --if-not-exists
docker exec baheka-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic risk-events --partitions 3 --replication-factor 1 --if-not-exists
docker exec baheka-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic aml-alerts --partitions 3 --replication-factor 1 --if-not-exists
docker exec baheka-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic compliance-events --partitions 3 --replication-factor 1 --if-not-exists
docker exec baheka-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic security-events --partitions 3 --replication-factor 1 --if-not-exists

print_status "Kafka topics created successfully!"

# Start monitoring services
print_status "Starting monitoring services..."
docker-compose up -d prometheus grafana kafka-ui

# Check if backend services directory exists
if [ -d "backend" ]; then
    print_status "Starting backend services..."
    
    # Start each service in the background
    services=("gateway" "risk" "aml" "compliance" "security" "notification")
    
    for service in "${services[@]}"; do
        service_dir="backend/services/sentinel-$service"
        if [ -d "$service_dir" ]; then
            print_status "Starting $service service..."
            cd "$service_dir"
            
            # Build and start the service
            if [ -f "gradlew" ]; then
                ./gradlew bootRun > "../../logs/$service.log" 2>&1 &
            elif [ -f "mvnw" ]; then
                ./mvnw spring-boot:run > "../../logs/$service.log" 2>&1 &
            else
                print_warning "No build script found for $service service"
            fi
            
            cd - > /dev/null
            sleep 5
        else
            print_warning "Service directory not found: $service_dir"
        fi
    done
fi

# Start frontend if it exists
if [ -d "frontend" ]; then
    print_status "Starting frontend..."
    cd frontend
    
    if [ -f "package.json" ]; then
        # Install dependencies if node_modules doesn't exist
        if [ ! -d "node_modules" ]; then
            print_status "Installing frontend dependencies..."
            npm install
        fi
        
        # Start the development server
        npm start > ../logs/frontend.log 2>&1 &
        cd - > /dev/null
    else
        print_warning "No package.json found in frontend directory"
    fi
fi

# Display service URLs
echo ""
echo -e "${BLUE}🚀 Baheka Sentinel Platform Started Successfully!${NC}"
echo ""
echo "📊 Service URLs:"
echo "  Frontend:           http://localhost:3000"
echo "  API Gateway:        http://localhost:8080"
echo "  Keycloak:           http://localhost:8080/auth"
echo "  Kafka UI:           http://localhost:8082"
echo "  Grafana:            http://localhost:3000 (admin/admin123)"
echo "  Prometheus:         http://localhost:9090"
echo "  Elasticsearch:      http://localhost:9200"
echo ""
echo "🔐 Default Credentials:"
echo "  Keycloak Admin:     admin/admin123"
echo "  Grafana:            admin/admin123"
echo "  Database:           baheka_user/baheka_password"
echo ""
echo "📋 Useful Commands:"
echo "  View logs:          docker-compose logs -f [service]"
echo "  Stop all:           docker-compose down"
echo "  Restart:            docker-compose restart [service]"
echo ""
print_status "All services are starting up. Please wait a few minutes for everything to be fully ready."
