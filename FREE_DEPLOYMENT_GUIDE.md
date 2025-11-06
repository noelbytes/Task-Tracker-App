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
   - Database: `tasktracker`
   - User: `tasktracker` (or default)
   - Region: Choose closest to you
   - Plan: **Free**
4. **Click** "Create Database"
5. **Copy** the following from the database page:
   - Internal Database URL (for backend)
   - External Database URL (if needed)

### STEP 3: Deploy Backend (Spring Boot)

1. **Click** "New +" → "Web Service"
2. **Connect** your GitHub repository
3. **Select** your `task-tracker-app` repo
4. **Configure**:
   - Name: `tasktracker-backend`
   - Region: Same as database
   - Branch: `main`
   - Root Directory: `backend`
   - Environment: `Docker`
   - Build Command: (leave empty, uses Dockerfile)
   - Start Command: (leave empty, uses Dockerfile)
   - Plan: **Free**

5. **Add Environment Variables** (click "Advanced" → "Add Environment Variable"):
   ```
   SPRING_PROFILES_ACTIVE=prod
   JDBC_DATABASE_URL=jdbc:postgresql://[COPY_INTERNAL_DATABASE_URL_FROM_RENDER_DB_AND_ADD_jdbc_PREFIX]
   JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-12345
   CORS_ORIGINS=https://YOUR-FRONTEND-URL.onrender.com
   ```
   
   **Important:** Copy the "Internal Database URL" from your PostgreSQL database page.  
   If it shows `postgresql://user:pass@host:5432/db`, change it to `jdbc:postgresql://user:pass@host:5432/db`

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
3. **Select** your `task-tracker-app` repo
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

Now that you have the frontend URL, update backend:

1. Go to your backend service on Render
2. Click **Environment**
3. Update `CORS_ORIGINS` to: `https://tasktracker-frontend.onrender.com`
4. Click **Save Changes** (backend will redeploy)

### STEP 7: Test Your Deployment

1. **Visit** your frontend URL
2. **Login** with demo/demo123
3. **Test** all features:
   - Create tasks
   - Update tasks
   - Delete tasks
   - View analytics

---

## ⚡ Important Notes for Render Free Tier

### Services Sleep After 15 Minutes
- Free services sleep after inactivity
- First request after sleep takes 30-60 seconds to wake up
- Keep-alive services exist but may violate ToS

### Database Limitations
- Free PostgreSQL expires after 90 days
- You'll get email reminders
- Can create a new free database and migrate data

### Build Minutes
- 750 hours/month is plenty for personal projects
- Only counts when services are running

---

## 🚂 Option 2: Railway.app (Alternative)

### Overview
- $5 free credit per month
- More generous than Render (services don't sleep as often)
- Simpler deployment
- Automatic PostgreSQL setup

### Steps

1. **Go to** https://railway.app
2. **Sign up** with GitHub
3. **Click** "New Project" → "Deploy from GitHub repo"
4. **Select** your repository
5. **Add PostgreSQL**:
   - Click "New" → "Database" → "Add PostgreSQL"
   - Railway auto-configures DATABASE_URL

6. **Deploy Backend**:
   - Railway detects Dockerfile automatically
   - Add environment variables:
     ```
     SPRING_PROFILES_ACTIVE=prod
     JWT_SECRET=your-secret-key
     CORS_ORIGINS=https://your-frontend.up.railway.app
     ```
   - Set root directory: `backend`

7. **Deploy Frontend**:
   - Add another service from same repo
   - Set root directory: `frontend`
   - Railway detects Dockerfile automatically

8. **Generate Domains**:
   - Click each service → "Settings" → "Generate Domain"
   - Update CORS_ORIGINS with frontend domain

---

## 🌐 Option 3: Vercel (Frontend) + Render (Backend)

### For Frontend Only
Vercel is excellent for Angular/React:

1. **Go to** https://vercel.com
2. **Import** your GitHub repo
3. **Configure**:
   - Framework: Angular
   - Root Directory: `frontend`
   - Build Command: `npm run build`
   - Output Directory: `dist/frontend/browser`
   - Environment Variables:
     ```
     PRODUCTION=true
     ```

4. **Deploy** - Vercel handles everything automatically

### Backend on Render
Follow Render backend steps above, but update CORS to Vercel URL.

---

## 🔒 Security Checklist Before Deploying

### ✅ Must Do:

1. **Change JWT Secret**:
   ```bash
   # Generate a strong secret
   openssl rand -base64 64
   ```
   Use this value for `JWT_SECRET` environment variable

2. **Update CORS Origins**:
   - Never use `*` in production
   - Only allow your actual frontend URL

3. **Review Environment Variables**:
   - No sensitive data in code
   - All secrets in environment variables

4. **Database Security**:
   - Use strong passwords
   - Use internal database URLs when possible

5. **HTTPS Only**:
   - All platforms provide free HTTPS
   - Ensure your app redirects HTTP → HTTPS

---

## 📊 Cost Comparison

| Platform | Backend | Frontend | Database | Sleep? | Cost |
|----------|---------|----------|----------|--------|------|
| **Render** | ✅ Free | ✅ Free | ✅ Free (90d) | Yes (15m) | $0 |
| **Railway** | ✅ $5 credit | ✅ $5 credit | ✅ Included | Sometimes | $0* |
| **Vercel + Render** | Render | Vercel Free | Render | Mixed | $0 |
| **Heroku** | ❌ No free | ❌ No free | ❌ No free | N/A | $7+ |

*Railway: $5/month free credit, may not cover full month if heavily used

---

## 🎯 My Recommendation: Render

**For your Task Tracker app, use Render because:**

1. ✅ **Completely free** for all components
2. ✅ **Simple setup** with GitHub integration
3. ✅ **PostgreSQL included** (unlike some others)
4. ✅ **Automatic HTTPS** and domains
5. ✅ **Easy environment variable management**
6. ✅ **Good for portfolios** (can wake services on-demand)

**The only downside**: 15-minute sleep time
- But for a portfolio/demo project, this is acceptable
- First load takes 30-60 seconds, then fast

---

## 📝 Quick Deploy Commands

After setting up Render services, you can deploy updates with:

```bash
# Make your changes
git add .
git commit -m "Your changes"
git push

# Render automatically deploys from GitHub!
# No additional commands needed
```

---

## 🆘 Troubleshooting

### Backend Won't Start
- Check environment variables are set correctly
- Verify DATABASE_URL format
- Check Render logs: Dashboard → Service → Logs

### Frontend Can't Connect to Backend
- Verify CORS_ORIGINS includes frontend URL
- Check environment.prod.ts has correct backend URL
- Verify backend is running (not sleeping)

### Database Connection Errors
- Use **Internal Database URL** for backend (faster, free traffic)
- Verify DB_USERNAME and DB_PASSWORD match
- Check PostgreSQL is running on Render

### 502 Bad Gateway
- Backend is sleeping, wait 30-60 seconds
- Or backend failed to start, check logs

---

## 🚀 Next Steps

1. **Choose a platform** (I recommend Render)
2. **Push code to GitHub**
3. **Follow the step-by-step guide above**
4. **Test your deployment**
5. **Share your app!**

---

## 📞 Need Help?

If you encounter issues:
1. Check Render/Railway logs (very detailed)
2. Verify all environment variables
3. Test backend API directly: `https://your-backend.onrender.com/actuator/health`
4. Check CORS settings if frontend can't connect

---

**Ready to deploy? Start with Render - it's the easiest and completely free!** 🎉

