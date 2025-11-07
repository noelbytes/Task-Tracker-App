#!/bin/bash

# Test which environment is being used in production build

echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║          Testing Production Build Configuration              ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

cd frontend

echo "Building with production configuration..."
echo ""

npm run build -- --configuration production

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Build successful!"
    echo ""
    echo "Checking compiled files for API URL..."
    echo ""

    # Search for the API URL in the compiled files
    if grep -r "task-tracker-app-1uok.onrender.com" dist/frontend/browser/ 2>/dev/null; then
        echo ""
        echo "╔═══════════════════════════════════════════════════════════════╗"
        echo "║         ✅ CORRECT - Using Production API URL! ✅             ║"
        echo "╚═══════════════════════════════════════════════════════════════╝"
        echo ""
        echo "The production build correctly uses:"
        echo "  https://task-tracker-app-1uok.onrender.com/api"
        echo ""
    elif grep -r "localhost:8080" dist/frontend/browser/ 2>/dev/null; then
        echo ""
        echo "╔═══════════════════════════════════════════════════════════════╗"
        echo "║          ❌ ERROR - Still Using localhost:8080! ❌            ║"
        echo "╚═══════════════════════════════════════════════════════════════╝"
        echo ""
        echo "The build is NOT using the production environment."
        echo "This means fileReplacements is not working."
        echo ""
    else
        echo ""
        echo "⚠️  Could not find API URL in compiled files"
        echo ""
    fi
else
    echo ""
    echo "❌ Build failed!"
    echo ""
fi

cd ..

