#!/bin/bash

echo "🔍 Task Tracker - Project Verification"
echo "======================================"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} $1"
        return 0
    else
        echo -e "${RED}✗${NC} $1 (missing)"
        return 1
    fi
}

check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✓${NC} $1/"
        return 0
    else
        echo -e "${RED}✗${NC} $1/ (missing)"
        return 1
    fi
}

echo "📁 Checking Project Structure..."
echo ""

# Root files
echo "Root Files:"
check_file "README.md"
check_file "API.md"
check_file "DEPLOYMENT.md"
check_file "PROJECT_SUMMARY.md"
check_file "docker-compose.yml"
check_file ".gitignore"
check_file "start.sh"
check_file "setup-dev.sh"
echo ""

# Backend structure
echo "Backend Structure:"
check_dir "backend"
check_file "backend/pom.xml"
check_file "backend/Dockerfile"
check_file "backend/src/main/resources/application.properties"
check_file "backend/src/main/resources/application-prod.properties"
check_file "backend/src/main/java/com/tasktracker/TaskTrackerApplication.java"
echo ""

echo "Backend Components:"
check_dir "backend/src/main/java/com/tasktracker/config"
check_dir "backend/src/main/java/com/tasktracker/controller"
check_dir "backend/src/main/java/com/tasktracker/dto"
check_dir "backend/src/main/java/com/tasktracker/model"
check_dir "backend/src/main/java/com/tasktracker/repository"
check_dir "backend/src/main/java/com/tasktracker/security"
check_dir "backend/src/main/java/com/tasktracker/service"
echo ""

# Frontend structure
echo "Frontend Structure:"
check_dir "frontend"
check_file "frontend/package.json"
check_file "frontend/Dockerfile"
check_file "frontend/nginx.conf"
check_file "frontend/angular.json"
check_file "frontend/tsconfig.json"
echo ""

echo "Frontend Components:"
check_dir "frontend/src/app/components/login"
check_dir "frontend/src/app/components/task-list"
check_dir "frontend/src/app/components/task-form"
check_dir "frontend/src/app/components/analytics"
check_dir "frontend/src/app/services"
check_dir "frontend/src/app/models"
check_dir "frontend/src/app/guards"
check_dir "frontend/src/app/interceptors"
check_dir "frontend/src/environments"
echo ""

echo "Frontend Key Files:"
check_file "frontend/src/app/app.routes.ts"
check_file "frontend/src/app/app.config.ts"
check_file "frontend/src/app/services/auth.service.ts"
check_file "frontend/src/app/services/task.service.ts"
check_file "frontend/src/environments/environment.ts"
check_file "frontend/src/environments/environment.prod.ts"
echo ""

# Check for required tools
echo "🔧 Checking Required Tools..."
echo ""

if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    echo -e "${GREEN}✓${NC} Java: $JAVA_VERSION"
else
    echo -e "${YELLOW}⚠${NC} Java not found (required for backend)"
fi

if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version 2>&1 | head -n 1)
    echo -e "${GREEN}✓${NC} Maven: $MVN_VERSION"
else
    echo -e "${YELLOW}⚠${NC} Maven not found (required for backend)"
fi

if command -v node &> /dev/null; then
    NODE_VERSION=$(node --version)
    echo -e "${GREEN}✓${NC} Node.js: $NODE_VERSION"
else
    echo -e "${YELLOW}⚠${NC} Node.js not found (required for frontend)"
fi

if command -v npm &> /dev/null; then
    NPM_VERSION=$(npm --version)
    echo -e "${GREEN}✓${NC} npm: $NPM_VERSION"
else
    echo -e "${YELLOW}⚠${NC} npm not found (required for frontend)"
fi

if command -v docker &> /dev/null; then
    DOCKER_VERSION=$(docker --version)
    echo -e "${GREEN}✓${NC} Docker: $DOCKER_VERSION"
else
    echo -e "${YELLOW}⚠${NC} Docker not found (optional, for containerized deployment)"
fi

if command -v docker-compose &> /dev/null || docker compose version &> /dev/null; then
    echo -e "${GREEN}✓${NC} Docker Compose: Available"
else
    echo -e "${YELLOW}⚠${NC} Docker Compose not found (optional, for containerized deployment)"
fi

echo ""
echo "======================================"
echo "✅ Project structure verification complete!"
echo ""
echo "📝 Next Steps:"
echo "   1. Review README.md for setup instructions"
echo "   2. Run ./setup-dev.sh for local development"
echo "   3. Or run ./start.sh for Docker deployment"
echo ""
echo "🔐 Demo Credentials:"
echo "   Username: demo"
echo "   Password: demo123"
echo ""

