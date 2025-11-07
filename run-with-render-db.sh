#!/bin/bash

# Run Spring Boot backend locally connected to Render PostgreSQL
# This tests if your backend can connect to the production database

echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║     Running Backend Locally with Render Database             ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

# Set environment variables
export DATABASE_URL="jdbc:postgresql://dpg-d46i7dhr0fns73fn6c50-a.ohio-postgres.render.com:5432/tasktracker_db_01j9"
export DB_USERNAME="tasktracker_db_01j9_user"
export DB_PASSWORD="YmeGK9T6SfHMN4NyZSdT0r1VgdZs3hEy"
export SPRING_PROFILES_ACTIVE="prod"
export JWT_SECRET="9zTtC/gmpO5ROWyD+WClwfMBRoaz02xkgbBNuIt3+JvYiWNIbhOyO8PA3DqYU7Tvl4g++jRcz1bl9wqssi9lNg=="
export CORS_ORIGINS="http://localhost:4200"

echo "Environment variables set:"
echo "  DATABASE_URL: $DATABASE_URL"
echo "  DB_USERNAME: $DB_USERNAME"
echo "  SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"
echo "  CORS_ORIGINS: $CORS_ORIGINS"
echo ""
echo "Starting Spring Boot application..."
echo ""
echo "⚠️  NOTE: This will connect to your PRODUCTION database on Render!"
echo "    Any data changes will affect your live database."
echo ""
echo "Press Ctrl+C to stop the application"
echo ""

cd backend
./mvnw spring-boot:run

