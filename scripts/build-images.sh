#!/bin/bash

# Baheka Sentinel - Build All Docker Images
# This script builds all Docker images for the platform

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
VERSION=${VERSION:-1.0.0}
REGISTRY=${REGISTRY:-baheka}
BUILD_ARGS=${BUILD_ARGS:-""}

print_status() {
    echo -e "${GREEN}[BUILD]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE} Baheka Sentinel - Build Images${NC}"
    echo -e "${BLUE}================================${NC}"
    echo "Version: $VERSION"
    echo "Registry: $REGISTRY"
    echo ""
}

# Function to build Java services
build_java_service() {
    local service_name=$1
    local port=$2
    
    print_status "Building $service_name..."
    
    # Build with Gradle first
    cd backend
    ./gradlew :services:$service_name:bootJar --no-daemon
    
    # Copy JAR to service directory for Docker build
    mkdir -p services/$service_name/target
    cp services/$service_name/build/libs/*.jar services/$service_name/target/
    
    # Build Docker image
    docker build \
        -f services/$service_name/Dockerfile \
        -t $REGISTRY/sentinel-$service_name:$VERSION \
        -t $REGISTRY/sentinel-$service_name:latest \
        $BUILD_ARGS \
        services/$service_name
    
    cd ..
    
    print_status "$service_name build completed"
}

# Function to build frontend
build_frontend() {
    print_status "Building frontend..."
    
    cd frontend
    
    # Build Docker image with multi-stage build
    docker build \
        -t $REGISTRY/sentinel-frontend:$VERSION \
        -t $REGISTRY/sentinel-frontend:latest \
        $BUILD_ARGS \
        .
    
    cd ..
    
    print_status "Frontend build completed"
}

# Function to verify images
verify_images() {
    print_status "Verifying built images..."
    
    local images=(
        "sentinel-gateway"
        "sentinel-risk" 
        "sentinel-aml"
        "sentinel-frontend"
    )
    
    for image in "${images[@]}"; do
        if docker images | grep -q "$REGISTRY/$image"; then
            local size=$(docker images --format "table {{.Repository}}:{{.Tag}}\t{{.Size}}" | grep "$REGISTRY/$image:$VERSION" | awk '{print $2}')
            print_status "✓ $REGISTRY/$image:$VERSION ($size)"
        else
            print_error "✗ $REGISTRY/$image:$VERSION not found"
            exit 1
        fi
    done
    
    echo ""
    print_status "All images built successfully!"
}

# Function to push images (optional)
push_images() {
    if [ "$PUSH_IMAGES" = "true" ]; then
        print_status "Pushing images to registry..."
        
        docker push $REGISTRY/sentinel-gateway:$VERSION
        docker push $REGISTRY/sentinel-gateway:latest
        docker push $REGISTRY/sentinel-risk:$VERSION
        docker push $REGISTRY/sentinel-risk:latest
        docker push $REGISTRY/sentinel-aml:$VERSION
        docker push $REGISTRY/sentinel-aml:latest
        docker push $REGISTRY/sentinel-frontend:$VERSION
        docker push $REGISTRY/sentinel-frontend:latest
        
        print_status "Images pushed successfully!"
    fi
}

# Function to clean up build artifacts
cleanup() {
    print_status "Cleaning up build artifacts..."
    
    # Clean Gradle build artifacts
    cd backend && ./gradlew clean && cd ..
    
    # Remove copied JARs
    find backend/services -name "target" -type d -exec rm -rf {} + 2>/dev/null || true
    
    print_status "Cleanup completed"
}

# Main execution
main() {
    print_header
    
    # Check prerequisites
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed or not in PATH"
        exit 1
    fi
    
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed or not in PATH"
        exit 1
    fi
    
    # Build all services
    print_status "Starting build process..."
    
    # Build Java microservices
    build_java_service "sentinel-gateway" 8080
    build_java_service "sentinel-risk" 8081
    build_java_service "sentinel-aml" 8082
    
    # Build frontend
    build_frontend
    
    # Verify all images
    verify_images
    
    # Push images if requested
    push_images
    
    # Cleanup if requested
    if [ "$CLEANUP" = "true" ]; then
        cleanup
    fi
    
    echo ""
    print_status "🎉 Build completed successfully!"
    echo ""
    echo "Built images:"
    echo "  • $REGISTRY/sentinel-gateway:$VERSION"
    echo "  • $REGISTRY/sentinel-risk:$VERSION" 
    echo "  • $REGISTRY/sentinel-aml:$VERSION"
    echo "  • $REGISTRY/sentinel-frontend:$VERSION"
    echo ""
    echo "Next steps:"
    echo "  • Run: docker-compose -f docker-compose.prod.yml up -d"
    echo "  • View: http://localhost:3000 (Frontend)"
    echo "  • Monitor: http://localhost:3001 (Grafana)"
    echo ""
}

# Handle script arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --version)
            VERSION="$2"
            shift 2
            ;;
        --registry)
            REGISTRY="$2"
            shift 2
            ;;
        --push)
            PUSH_IMAGES="true"
            shift
            ;;
        --cleanup)
            CLEANUP="true"
            shift
            ;;
        --help)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --version VERSION    Set image version (default: 1.0.0)"
            echo "  --registry REGISTRY  Set registry name (default: baheka)"
            echo "  --push              Push images to registry"
            echo "  --cleanup           Clean build artifacts after build"
            echo "  --help              Show this help message"
            echo ""
            echo "Environment variables:"
            echo "  VERSION             Image version"
            echo "  REGISTRY            Registry name"
            echo "  BUILD_ARGS          Additional build arguments"
            echo "  PUSH_IMAGES         Push images (true/false)"
            echo "  CLEANUP             Cleanup after build (true/false)"
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Run main function
main
