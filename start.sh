#!/bin/bash

# Get the docker tag from the first argument, default to 'latest'
DOCKER_TAG="${1:-latest}"

# Define the docker-compose.yml content
cat <<EOF > docker-compose.yml

services:
  seamless-upgrade-mongodb:
    image: mongo:8.0
    container_name: mongodb
    ports:
      - '27017:27017'
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: admin123
    volumes:
      - seamless-upgrade-mongodb-data:/data/db

  backend:
    image: hyperflex/elastic-seamless-upgrade-backend:$DOCKER_TAG
    container_name: backend
    pull_policy: always
    environment:
      MONGO_URI: mongodb://admin:admin123@seamless-upgrade-mongodb:27017/
    depends_on:
      - seamless-upgrade-mongodb
    ports:
      - '3000:3000'

  seamless-upgrade-tool:
    image: hyperflex/elastic-seamless-upgrade-tool:$DOCKER_TAG
    container_name: tool
    pull_policy: always
    ports:
      - '8080:8080'
    depends_on:
      - backend

volumes:
  seamless-upgrade-mongodb-data:
EOF

# Start the services
echo "Starting the containers..."
docker compose -p seamless-upgrade up -d

# Function to check service status
check_service() {
    local service_name=$1
    local status=$(docker inspect --format='{{.State.Status}}' $service_name 2>/dev/null)
    if [ "$status" != "running" ]; then
        echo "$service_name failed to start. Logs:"
        docker logs $service_name
        exit 1
    fi
}

# Wait a few seconds before checking status
sleep 5

# Check each service
check_service "mongodb"
check_service "backend"
check_service "frontend"

echo "Seamless upgrade tool running on:"
echo "http://localhost:8080"
