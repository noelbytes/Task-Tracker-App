# 🚀 Free Deployment Guide - Task Tracker App

## 🎯 Best Free Options

I recommend **Render.com** for your Task Tracker app. It offers:
- ✅ Free PostgreSQL database
- ✅ Free web service hosting
- ✅ Free static site hosting
- ✅ Automatic HTTPS
- ✅ GitHub integration
- ✅ Easy setup

**Alternative**: Railway.app (also excellent and free)

---

## 📦 Option 1: Render.com (RECOMMENDED)

### Overview
- **Backend**: Free Web Service (750 hours/month)
- **Frontend**: Free Static Site
- **Database**: Free PostgreSQL (expires after 90 days, but can renew)
- **Cost**: $0/month
- **Limitation**: Services sleep after 15 min of inactivity

### Prerequisites
1. GitHub account
2. Render.com account (free)
3. Push your code to GitHub

---

## 🔧 Step-by-Step: Deploy to Render

### STEP 1: Push Code to GitHub

```bash
cd /home/noelbytes/Projects/Task-Tracker-App

# Initialize git if not already
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit - Task Tracker App"

# Create repo on GitHub, then:
git remote add origin https://github.com/YOUR_USERNAME/task-tracker-app.git
git branch -M main
git push -u origin main
```

### STEP 2: Deploy PostgreSQL Database

1. **Go to** https://dashboard.render.com
2. **Click** "New +" → "PostgreSQL"
3. **Configure**:
   - Name: `tasktracker-db`
   - Region: Choose closest to you
   - Plan: **Free**
4. **Click** "Create Database"
5. **Copy** from the database page:
   - Internal Database URL (for backend)

### STEP 3: Deploy Backend (Spring Boot)

1. **Click** "New +" → "Web Service"
2. **Connect** your GitHub repository
3. **Select** your repo
4. **Configure**:
   - Name: `tasktracker-backend`
   - Region: Same as database
   - Branch: `main`
   - Root Directory: `backend`
   - Environment: `Docker`
   - Build/Start Commands: leave empty (Dockerfile handles it)
   - Plan: **Free**

5. **Add Environment Variables** (click "Advanced" → "Add Environment Variable"):
   ```
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=jdbc:postgresql://<host>:5432/<db>
   DB_USERNAME=<db user>
   DB_PASSWORD=<db password>
   JWT_SECRET=<run: openssl rand -base64 64>
   CORS_ORIGINS=https://YOUR-FRONTEND-URL.onrender.com
   ```
   
   From Internal Database URL:
   - Example: `postgresql://user:pass@dpg-xxxx.<region>-postgres.render.com:5432/dbname`
   - Set:
     - `DATABASE_URL = jdbc:postgresql://dpg-xxxx.<region>-postgres.render.com:5432/dbname`
     - `DB_USERNAME = user`
     - `DB_PASSWORD = pass`

6. **Click** "Create Web Service"
7. **Wait** for deployment (5-10 minutes)
8. **Copy** your backend URL: `https://tasktracker-backend.onrender.com`

### STEP 4: Update Frontend Configuration

Before deploying frontend, update the API URL:

**Edit** `frontend/src/environments/environment.prod.ts`:

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://tasktracker-backend.onrender.com/api'  // Your backend URL
};
```

**Commit and push**:
```bash
git add frontend/src/environments/environment.prod.ts
git commit -m "Update production API URL"
git push
```

### STEP 5: Deploy Frontend (Angular)

1. **Click** "New +" → "Static Site"
2. **Connect** your GitHub repository
3. **Select** your repo
4. **Configure**:
   - Name: `tasktracker-frontend`
   - Branch: `main`
   - Root Directory: `frontend`
   - Build Command: `npm ci && npm run build`
   - Publish Directory: `dist/frontend/browser`

5. **Click** "Create Static Site"
6. **Wait** for deployment (3-5 minutes)
7. **Copy** your frontend URL: `https://tasktracker-frontend.onrender.com`

### STEP 6: Update CORS_ORIGINS

1. Go to your backend service on Render
2. Click **Environment**
3. Update `CORS_ORIGINS` to your actual frontend URL
4. Click **Save Changes** (backend will redeploy)

### STEP 7: Test Your Deployment

1. **Visit** your frontend URL
2. **Login** with demo/demo123
3. **Test**: Create, update, delete tasks

---

## ⚡ Notes for Render Free Tier
- Services sleep after inactivity (wake-up 30–60s)
- Free PostgreSQL expires after 90 days (email reminders)

---

## 🚂 Option 2: Railway.app (Alternative)
- $5 free monthly credit
- Simpler DX
- Steps similar; see FREE_DEPLOYMENT_GUIDE.md for details
