# 🚀 QUICK START: Deploy to Render (5 Minutes)

## Prerequisites
- ✅ GitHub account
- ✅ Render.com account (free - sign up at https://render.com)

---

## Step 1: Push to GitHub (2 minutes)

```bash
cd /home/noelbytes/Projects/Task-Tracker-App

# Initialize git if needed
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit - Task Tracker App"

# Create a new repository on GitHub.com, then:
git remote add origin https://github.com/YOUR_USERNAME/task-tracker-app.git
git branch -M main
git push -u origin main
```

---

## Step 2: Deploy Database (1 minute)

1. Go to https://dashboard.render.com
2. Click **"New +"** → **"PostgreSQL"**
3. Settings:
   - Name: `tasktracker-db`
   - Plan: **Free**
4. Click **"Create Database"**
5. **COPY** the "Internal Database URL" (you'll need it)

---

## Step 3: Deploy Backend (2 minutes)

1. Click **"New +"** → **"Web Service"**
2. Connect your GitHub repo
3. Settings:
   - Name: `tasktracker-backend`
   - Root Directory: `backend`
   - Environment: **Docker**
   - Plan: **Free**

4. Click **"Advanced"** and add these environment variables:

| Key | Value |
|-----|-------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DATABASE_URL` | `jdbc:postgresql://<host>:5432/<db>` |
| `DB_USERNAME` | `<db user>` |
| `DB_PASSWORD` | `<db password>` |
| `JWT_SECRET` | Copy from prepare-deployment.sh output |
| `CORS_ORIGINS` | `https://tasktracker-frontend.onrender.com` (adjust if different) |

   Get these from your DB page:
   - Internal URL example: `postgresql://user:pass@dpg-xxxx.<region>-postgres.render.com:5432/db`  
   - Set:
     - `DATABASE_URL = jdbc:postgresql://dpg-xxxx.<region>-postgres.render.com:5432/db`
     - `DB_USERNAME = user`
     - `DB_PASSWORD = pass`

5. Click **"Create Web Service"**
6. Wait 5-10 minutes for build
7. **COPY** your backend URL: `https://tasktracker-backend-XXXX.onrender.com`

---

## Step 4: Update Frontend Config (30 seconds)

Edit `frontend/src/environments/environment.prod.ts`:

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://tasktracker-backend-XXXX.onrender.com/api'  // YOUR backend URL
};
```

Commit and push:
```bash
git add frontend/src/environments/environment.prod.ts
git commit -m "Update production API URL"
git push
```

---

## Step 5: Deploy Frontend (2 minutes)

1. Click **"New +"** → **"Static Site"**
2. Connect your GitHub repo
3. Settings:
   - Name: `tasktracker-frontend`
   - Root Directory: `frontend`
   - Build Command: `npm ci && npm run build`
   - Publish Directory: `dist/frontend/browser`

4. Click **"Create Static Site"**
5. Wait 3-5 minutes for build

---

## Step 6: Update CORS (1 minute)

1. Go to your **backend service** on Render
2. Click **"Environment"**
3. Update `CORS_ORIGINS` to your actual frontend URL
4. Click **"Save Changes"**

---

## Step 7: Test! 🎉

1. Visit your frontend URL: `https://tasktracker-frontend-XXXX.onrender.com`
2. Login with: **demo** / **demo123**
3. Create a task to test everything works!

---

## ⚠️ Important Notes

- Free services sleep after 15 minutes
- First request after sleep takes 30-60 seconds
- Free PostgreSQL expires after 90 days

---

## 🐛 Troubleshooting

- Backend won't start? Check logs, verify DATABASE_URL/DB_USERNAME/DB_PASSWORD
- Frontend can't connect? Check CORS and API URL
- 502 Bad Gateway? Service waking up or failed startup

---

## 🎯 Your Deployed App

After deployment, you'll have:
- ✅ Frontend: `https://tasktracker-frontend-XXXX.onrender.com`
- ✅ Backend: `https://tasktracker-backend-XXXX.onrender.com`
- ✅ Database: PostgreSQL on Render
- ✅ HTTPS (automatic)
- ✅ Custom domain support (optional)

**Total Cost: $0/month** 🎉

---

## 📚 More Details

For alternatives and detailed explanations, see:
- **FREE_DEPLOYMENT_GUIDE.md** - Complete guide with all options
- **DEPLOYMENT.md** - Original deployment documentation

---

**That's it! Your Task Tracker app is now live and accessible to anyone!** 🚀
