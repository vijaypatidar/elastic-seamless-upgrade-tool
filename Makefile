.PHONY: up down status logs


DOCKER_COMPOSE_FILE=docker-compose.yml


define compose_file
version: '3.8'

services:
  mongodb:
    image: mongo:6.0
    container_name: mongodb
    ports:
      - '27017:27017'
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: admin123
    volumes:
      - mongo-data:/data/db

  backend:
    image: hyperflex/elastic-seamless-upgrade-backend:latest
    container_name: backend
    environment:
      MONGO_URI: mongodb://admin:admin123@mongodb:27017/
    depends_on:
      - mongodb
    ports:
      - '3000:3000'

  frontend:
    image: hyperflex/elastic-seamless-upgrade-frontend:latest
    container_name: frontend
    ports:
      - '8080:80'
    depends_on:
      - backend

volumes:
  mongo-data:
endef

export compose_file


up:
	@echo "$$compose_file" > $(DOCKER_COMPOSE_FILE)
	@echo "Starting containers..."
	@docker-compose up -d
	@sleep 5
	@$(MAKE) status


down:
	@echo "Stopping containers..."
	@docker-compose down


status:
	@for service in mongodb backend frontend; do \
		status=$$(docker inspect --format='{{.State.Status}}' $$service 2>/dev/null); \
		if [ "$$status" != "running" ]; then \
			echo "$$service failed to start. Logs:"; \
			docker logs $$service; \
			exit 1; \
		fi; \
	done
	@echo "Seamless upgrade tool running on:"
	@echo "http://localhost:8080"


logs:
	@docker-compose logs -f
