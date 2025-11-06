#!/bin/bash

echo "🚀 Starting Task Tracker Application..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

echo "📦 Building and starting containers..."
docker-compose up --build

echo ""
echo "✅ Application is ready!"
echo ""
echo "🌐 Access points:"
echo "   Frontend: http://localhost"
echo "   Backend API: http://localhost:8080"
echo "   PostgreSQL: localhost:5432"
echo ""
echo "🔐 Demo Credentials:"
echo "   Username: demo"
echo "   Password: demo123"
echo ""

