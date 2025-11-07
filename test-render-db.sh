#!/bin/bash

# Test Render PostgreSQL Connection Locally
# This script tests if you can connect to your Render database from your local machine

echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║          Testing Render PostgreSQL Connection                ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

# Your Render database credentials
DB_HOST="dpg-d46i7dhr0fns73fn6c50-a.ohio-postgres.render.com"
DB_PORT="5432"
DB_NAME="tasktracker_db_01j9"
DB_USER="tasktracker_db_01j9_user"
DB_PASS="YmeGK9T6SfHMN4NyZSdT0r1VgdZs3hEy"

echo "Testing connection to:"
echo "  Host: $DB_HOST"
echo "  Port: $DB_PORT"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"
echo ""

# Check if psql is installed
if ! command -v psql &> /dev/null; then
    echo "❌ psql is not installed."
    echo ""
    echo "Install it with:"
    echo "  sudo apt-get install postgresql-client"
    echo ""
    exit 1
fi

echo "✅ psql is installed"
echo ""
echo "Attempting connection..."
echo ""

# Try to connect and run a simple query
PGPASSWORD=$DB_PASS psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "SELECT version();"

if [ $? -eq 0 ]; then
    echo ""
    echo "╔═══════════════════════════════════════════════════════════════╗"
    echo "║                  ✅ CONNECTION SUCCESSFUL! ✅                  ║"
    echo "╚═══════════════════════════════════════════════════════════════╝"
    echo ""
    echo "Your database connection works!"
    echo ""
    echo "Now test with your Spring Boot app:"
    echo ""
    echo "  export DATABASE_URL=\"jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME\""
    echo "  export DB_USERNAME=\"$DB_USER\""
    echo "  export DB_PASSWORD=\"$DB_PASS\""
    echo "  export SPRING_PROFILES_ACTIVE=\"prod\""
    echo "  export JWT_SECRET=\"your-jwt-secret\""
    echo "  export CORS_ORIGINS=\"http://localhost:4200\""
    echo ""
    echo "  cd backend"
    echo "  ./mvnw spring-boot:run"
    echo ""
else
    echo ""
    echo "╔═══════════════════════════════════════════════════════════════╗"
    echo "║                  ❌ CONNECTION FAILED! ❌                      ║"
    echo "╚═══════════════════════════════════════════════════════════════╝"
    echo ""
    echo "Possible reasons:"
    echo "  1. Firewall blocking connection"
    echo "  2. Incorrect credentials"
    echo "  3. Database not running"
    echo "  4. Network connectivity issues"
    echo ""
    echo "Try using the External Database URL instead (from Render dashboard)"
    echo ""
fi

