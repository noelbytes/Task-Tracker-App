#!/bin/bash

echo "=========================================="
echo "🚀 Deployment Preparation Checklist"
echo "=========================================="
echo ""

# Check if git is initialized
if [ -d .git ]; then
    echo "✅ Git repository initialized"
else
    echo "❌ Git not initialized"
    echo "   Run: git init"
    ISSUES=1
fi

# Check if remote is set
if git remote -v | grep -q "origin"; then
    echo "✅ Git remote configured"
    git remote -v | head -2
else
    echo "⚠️  No git remote configured"
    echo "   You'll need to:"
    echo "   1. Create a repo on GitHub"
    echo "   2. Run: git remote add origin <your-repo-url>"
    ISSUES=1
fi

echo ""
echo "=========================================="
echo "📋 Files Check"
echo "=========================================="
echo ""

# Check Dockerfiles
if [ -f backend/Dockerfile ]; then
    echo "✅ Backend Dockerfile exists"
else
    echo "❌ Backend Dockerfile missing"
    ISSUES=1
fi

if [ -f frontend/Dockerfile ]; then
    echo "✅ Frontend Dockerfile exists"
else
    echo "❌ Frontend Dockerfile missing"
    ISSUES=1
fi

# Check environment files
if [ -f frontend/src/environments/environment.prod.ts ]; then
    echo "✅ Production environment file exists"
    echo "   Current API URL:"
    grep "apiUrl" frontend/src/environments/environment.prod.ts
else
    echo "❌ Production environment file missing"
    ISSUES=1
fi

# Check if render.yaml exists
if [ -f render.yaml ]; then
    echo "✅ render.yaml exists (for Render deployment)"
else
    echo "⚠️  render.yaml not found (optional)"
fi

echo ""
echo "=========================================="
echo "🔒 Security Check"
echo "=========================================="
echo ""

# Check for hardcoded secrets
echo "Checking for potential hardcoded secrets..."
if grep -r "password.*=" backend/src --include="*.java" --include="*.properties" | grep -v "spring.datasource.password" | grep -q .; then
    echo "⚠️  Found potential hardcoded passwords in backend"
    echo "   Review these before deploying"
else
    echo "✅ No obvious hardcoded passwords found"
fi

# Check JWT secret in code
if grep -r "jwt.*secret.*=" backend/src --include="*.java" | grep -v "@Value" | grep -q .; then
    echo "⚠️  JWT secret may be hardcoded"
    echo "   Use environment variable instead"
else
    echo "✅ JWT secret uses environment variable"
fi

echo ""
echo "=========================================="
echo "📦 Dependencies Check"
echo "=========================================="
echo ""

# Check if package.json exists
if [ -f frontend/package.json ]; then
    echo "✅ Frontend package.json exists"
else
    echo "❌ Frontend package.json missing"
    ISSUES=1
fi

# Check if pom.xml exists
if [ -f backend/pom.xml ]; then
    echo "✅ Backend pom.xml exists"
else
    echo "❌ Backend pom.xml missing"
    ISSUES=1
fi

echo ""
echo "=========================================="
echo "🎯 Deployment Recommendations"
echo "=========================================="
echo ""

echo "Based on your app, I recommend:"
echo ""
echo "🏆 Option 1: Render.com (BEST FOR YOU)"
echo "   ✅ Completely free"
echo "   ✅ PostgreSQL included"
echo "   ✅ GitHub integration"
echo "   ✅ Easy setup"
echo "   ⚠️  Services sleep after 15 minutes"
echo ""
echo "🥈 Option 2: Railway.app"
echo "   ✅ \$5 free credit/month"
echo "   ✅ Services sleep less often"
echo "   ✅ Simpler deployment"
echo "   ⚠️  May run out of credit if heavily used"
echo ""
echo "🥉 Option 3: Vercel (Frontend) + Render (Backend)"
echo "   ✅ Best performance for frontend"
echo "   ✅ No sleep on frontend"
echo "   ⚠️  Backend still sleeps on Render"
echo ""

echo "=========================================="
echo "📝 Next Steps"
echo "=========================================="
echo ""

if [ -n "$ISSUES" ]; then
    echo "⚠️  Issues found above - fix them before deploying"
    echo ""
fi

echo "1. Choose a platform (Render recommended)"
echo "2. Create account and sign in with GitHub"
echo "3. Push your code to GitHub:"
echo ""
echo "   git add ."
echo "   git commit -m 'Prepare for deployment'"
echo "   git push origin main"
echo ""
echo "4. Follow the deployment guide:"
echo "   See FREE_DEPLOYMENT_GUIDE.md"
echo ""
echo "5. Update environment variables on your platform"
echo "6. Deploy and test!"
echo ""

echo "=========================================="
echo "🔧 Generate JWT Secret"
echo "=========================================="
echo ""
echo "For deployment, generate a secure JWT secret:"
echo ""

if command -v openssl &> /dev/null; then
    JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')
    echo "Generated JWT Secret (copy this):"
    echo ""
    echo "$JWT_SECRET"
    echo ""
    echo "Use this value for JWT_SECRET environment variable"
else
    echo "openssl not found. Use this online:"
    echo "https://www.grc.com/passwords.htm"
    echo "Copy the 63 character password"
fi

echo ""
echo "=========================================="
echo "✅ Checklist Complete!"
echo "=========================================="
echo ""
echo "Ready to deploy? Read FREE_DEPLOYMENT_GUIDE.md"
echo ""

