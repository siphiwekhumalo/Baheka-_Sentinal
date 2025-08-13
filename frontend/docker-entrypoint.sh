#!/bin/sh
set -e

echo "Starting Baheka Sentinel Frontend..."

# Replace API_URL placeholder in built files if needed
if [ -n "$API_URL" ]; then
    echo "Configuring API URL: $API_URL"
    find /usr/share/nginx/html -name "*.js" -exec sed -i "s|http://localhost:8080|$API_URL|g" {} \;
fi

# Replace other environment variables if needed
if [ -n "$APP_ENV" ]; then
    echo "Environment: $APP_ENV"
fi

echo "Frontend configuration complete. Starting nginx..."

# Execute the original command
exec "$@"
