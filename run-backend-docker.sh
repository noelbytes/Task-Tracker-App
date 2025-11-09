#!/bin/bash

# Script to build and run the backend Docker container

echo "========================================="
echo "Building Backend Docker Image..."
echo "========================================="
cd /home/noelbytes/Projects/Task_Tracker_App/backend
docker build -t tasktracker-backend .

if [ $? -ne 0 ]; then
    echo "Error: Docker build failed"
    exit 1
fi

echo ""
echo "========================================="
echo "Checking for existing containers..."
echo "========================================="

# Stop and remove existing containers if they exist
docker stop tasktracker-backend 2>/dev/null || true
docker rm tasktracker-backend 2>/dev/null || true
docker stop tasktracker-postgres 2>/dev/null || true
docker rm tasktracker-postgres 2>/dev/null || true

echo ""
echo "========================================="
echo "Starting PostgreSQL container..."
echo "========================================="
docker run -d \
  --name tasktracker-postgres \
  -e POSTGRES_DB=tasktracker \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine

echo "Waiting for PostgreSQL to be ready..."
sleep 10

echo ""
echo "========================================="
echo "Starting Backend container..."
echo "========================================="
docker run -d \
  --name tasktracker-backend \
  --link tasktracker-postgres:postgres \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://postgres:5432/tasktracker \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  -e JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970 \
  -e JWT_EXPIRATION=86400000 \
  -e CORS_ORIGINS=http://localhost:4200 \
  -p 8080:8080 \
  tasktracker-backend

echo ""
echo "========================================="
echo "Waiting for backend to start..."
echo "========================================="
sleep 5

echo ""
echo "========================================="
echo "Backend Logs:"
echo "========================================="
docker logs tasktracker-backend

echo ""
echo "========================================="
echo "Container Status:"
echo "========================================="
docker ps -a | grep tasktracker

echo ""
echo "========================================="
echo "To follow logs: docker logs -f tasktracker-backend"
echo "To stop: docker stop tasktracker-backend tasktracker-postgres"
echo "To remove: docker rm tasktracker-backend tasktracker-postgres"
echo "========================================="

