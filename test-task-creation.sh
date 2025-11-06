#!/bin/bash

echo "=========================================="
echo "🧪 Task Creation - CORS Fix Test"
echo "=========================================="
echo ""

echo "1. Checking containers..."
if sudo docker ps | grep -q tasktracker-backend; then
    echo "✅ Backend is running"
else
    echo "❌ Backend is NOT running"
    echo "   Starting backend..."
    sudo docker compose up -d backend
    sleep 10
fi

echo ""
echo "2. Testing CORS preflight (OPTIONS request)..."
RESPONSE=$(curl -s -X OPTIONS http://localhost:8080/api/tasks \
  -H "Origin: http://localhost" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type,Authorization" \
  -o /dev/null -w "%{http_code}")

if [ "$RESPONSE" = "200" ] || [ "$RESPONSE" = "204" ]; then
    echo "✅ OPTIONS request successful (HTTP $RESPONSE)"
    echo "   CORS preflight is working!"
else
    echo "❌ OPTIONS request failed (HTTP $RESPONSE)"
    echo "   Expected: 200 or 204"
    echo "   This may still be starting up, wait 10 seconds and try again"
fi

echo ""
echo "3. Testing backend health..."
BACKEND_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/ 2>&1)
if [ "$BACKEND_STATUS" != "000" ]; then
    echo "✅ Backend is responding (HTTP $BACKEND_STATUS)"
else
    echo "⚠️  Backend not responding yet, may still be starting..."
fi

echo ""
echo "=========================================="
echo "🎯 How to Test Task Creation"
echo "=========================================="
echo ""
echo "1. Open http://localhost/login in your browser"
echo "2. Login with:"
echo "   Username: demo"
echo "   Password: demo123"
echo ""
echo "3. Try creating a new task:"
echo "   • Click 'Add Task' or 'New Task' button"
echo "   • Fill in the form"
echo "   • Click 'Save' or 'Create'"
echo ""
echo "4. Check browser console (F12):"
echo "   • Should see OPTIONS /api/tasks → 200 OK"
echo "   • Should see POST /api/tasks → 201 Created"
echo "   • Task should appear in the list!"
echo ""
echo "=========================================="
echo "📋 What Was Fixed"
echo "=========================================="
echo ""
echo "✅ Added OPTIONS request matcher to SecurityConfig"
echo "✅ Enabled CORS configuration in security chain"
echo "✅ Backend rebuilt with CORS fixes"
echo ""
echo "The issue was that Spring Security was blocking"
echo "CORS preflight OPTIONS requests with 403 Forbidden."
echo "This prevented the browser from making POST requests."
echo ""
echo "=========================================="
echo ""
echo "Backend logs (last 10 lines):"
sudo docker logs tasktracker-backend --tail=10 2>&1 | grep -v "DEBUG"
echo ""
echo "=========================================="

