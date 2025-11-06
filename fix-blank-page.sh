#!/bin/bash

echo "=========================================="
echo "🔧 COMPLETE FIX FOR BLANK PAGE ISSUE"
echo "=========================================="
echo ""

cd /home/noelbytes/Projects/Task-Tracker-App

echo "1️⃣ Stopping all containers..."
sudo docker compose down

echo ""
echo "2️⃣ Rebuilding frontend with --no-cache..."
sudo docker compose build --no-cache frontend

echo ""
echo "3️⃣ Starting all services..."
sudo docker compose up -d

echo ""
echo "4️⃣ Waiting for services to start..."
sleep 5

echo ""
echo "5️⃣ Container Status:"
sudo docker compose ps

echo ""
echo "6️⃣ Checking frontend files:"
sudo docker exec tasktracker-frontend ls -lh /usr/share/nginx/html/ | grep -E "(index|main|styles)"

echo ""
echo "=========================================="
echo "✅ REBUILD COMPLETE!"
echo "=========================================="
echo ""
echo "🌐 NOW DO THIS IN YOUR BROWSER:"
echo ""
echo "   1. Open DevTools (F12 or Ctrl+Shift+I)"
echo "   2. Right-click the Refresh button"
echo "   3. Select 'Empty Cache and Hard Reload'"
echo ""
echo "   OR use keyboard shortcut:"
echo "   - Chrome/Firefox: Ctrl + Shift + R"
echo "   - Or: Ctrl + F5"
echo ""
echo "   Then go to: http://localhost/login"
echo ""
echo "=========================================="
echo ""
echo "📝 If still blank, check browser console (F12) for errors"
echo "📋 View logs: sudo docker compose logs -f frontend"
echo ""

