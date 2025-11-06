#!/bin/bash

echo "🔧 Setting up Task Tracker for Local Development..."
echo ""

# Backend setup
echo "📦 Setting up Backend..."
cd backend
if [ ! -d "target" ]; then
    echo "Building backend..."
    mvn clean install -DskipTests
fi
echo "✅ Backend setup complete"
echo ""

# Frontend setup
echo "📦 Setting up Frontend..."
cd ../frontend
if [ ! -d "node_modules" ]; then
    echo "Installing frontend dependencies..."
    npm install
fi
echo "✅ Frontend setup complete"
echo ""

echo "✅ Setup complete!"
echo ""
echo "📝 To start the application:"
echo ""
echo "Terminal 1 (Backend):"
echo "   cd backend"
echo "   mvn spring-boot:run"
echo ""
echo "Terminal 2 (Frontend):"
echo "   cd frontend"
echo "   npm start"
echo ""
echo "🌐 Access points:"
echo "   Frontend: http://localhost:4200"
echo "   Backend API: http://localhost:8080"
echo "   H2 Console: http://localhost:8080/h2-console"
echo ""
echo "🔐 Demo Credentials:"
echo "   Username: demo"
echo "   Password: demo123"
echo ""

