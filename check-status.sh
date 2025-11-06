 tas#!/bin/bash
# Quick verification and access script for Task Tracker App

echo "=========================================="
echo "🔍 Task Tracker - Status Check"
echo "=========================================="
echo ""

# Check if containers are running
echo "1. Checking Docker containers..."
if sudo docker ps | grep -q tasktracker-frontend; then
    echo "✅ Frontend container is running"
else
    echo "❌ Frontend container is NOT running"
    echo "   Run: sudo docker compose up -d"
fi

if sudo docker ps | grep -q tasktracker-backend; then
    echo "✅ Backend container is running"
else
    echo "❌ Backend container is NOT running"
    echo "   Run: sudo docker compose up -d"
fi

if sudo docker ps | grep -q tasktracker-postgres; then
    echo "✅ Database container is running"
else
    echo "❌ Database container is NOT running"
    echo "   Run: sudo docker compose up -d"
fi

echo ""
echo "2. Testing HTTP endpoints..."

# Test frontend
if curl -s -I http://localhost/ | grep -q "200 OK"; then
    echo "✅ Frontend is accessible at http://localhost"
else
    echo "⚠️  Frontend may not be responding"
fi

# Test backend
if curl -s -I http://localhost:8080/ | head -1 | grep -q "HTTP"; then
    echo "✅ Backend is accessible at http://localhost:8080"
else
    echo "⚠️  Backend may not be responding"
fi

echo ""
echo "=========================================="
echo "🌐 Access Your Application"
echo "=========================================="
echo ""
echo "Frontend: http://localhost"
echo "Login Page: http://localhost/login"
echo ""
echo "Demo Credentials:"
echo "  Username: demo"
echo "  Password: demo123"
echo ""
echo "=========================================="
echo "🧹 IMPORTANT: Clear Browser Cache!"
echo "=========================================="
echo ""
echo "The containers have been rebuilt with fixes."
echo "You MUST clear your browser cache to see changes:"
echo ""
echo "Method 1: Hard Reload"
echo "  • Press Ctrl + Shift + R (or Ctrl + F5)"
echo ""
echo "Method 2: Incognito Mode (Easiest)"
echo "  • Press Ctrl + Shift + N"
echo "  • Go to http://localhost/login"
echo ""
echo "Method 3: Disable Cache in DevTools"
echo "  • Press F12"
echo "  • Network tab"
echo "  • Check 'Disable cache'"
echo "  • Keep DevTools open and reload"
echo ""
echo "=========================================="

