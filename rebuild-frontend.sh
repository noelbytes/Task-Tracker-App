#!/bin/bash

echo "🔧 Rebuilding frontend with fixes..."
cd /home/noelbytes/Projects/Task-Tracker-App

echo "📦 Building frontend container..."
sudo docker compose build frontend

echo "🚀 Starting frontend container..."
sudo docker compose up -d frontend

echo "✅ Done! Frontend rebuilt and restarted."
echo ""
echo "📊 Container status:"
sudo docker compose ps frontend

echo ""
echo "🌐 Access your app at: http://localhost"
echo "   Login page: http://localhost/login"
echo ""
echo "📝 To view logs: sudo docker compose logs -f frontend"

