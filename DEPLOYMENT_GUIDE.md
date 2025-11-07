# 📚 Task Tracker App - Complete Deployment Guide

## 🎯 Quick Start

**Your App URLs:**
- Backend: `https://task-tracker-app-1uok.onrender.com`
- Frontend: Create new static site following instructions below
- Demo Login: `demo` / `demo123`

---

## 🚀 Deploy to Render (Free)

### 1️⃣ Deploy Database (1 minute)

1. Go to https://dashboard.render.com
2. New + → PostgreSQL → Free
3. Name: `tasktracker-db`
4. Copy the **Internal Database URL**

### 2️⃣ Deploy Backend (5 minutes)

1. New + → Web Service
2. Connect GitHub repo: `Task-Tracker-App`
3. Configure:
   - Root Directory: `backend`
   - Environment: Docker
   - Plan: Free

4. Add Environment Variables:
   ```
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=jdbc:postgresql://<host>:5432/<database>
   DB_USERNAME=<db user>
   DB_PASSWORD=<db password>
   JWT_SECRET=<generate with: openssl rand -base64 64>
   CORS_ORIGINS=https://<your-frontend-url>.onrender.com
   ```

   **Format DATABASE_URL correctly:**
   - Internal URL example: `postgresql://user:pass@dpg-xxx.ohio-postgres.render.com:5432/db`
   - Set as: `jdbc:postgresql://dpg-xxx.ohio-postgres.render.com:5432/db`
   - Username and password go in separate variables

5. Create Web Service
6. Wait 5-10 minutes
7. Copy backend URL

### 3️⃣ Deploy Frontend (5 minutes)

1. New + → Static Site
2. Connect GitHub repo
3. Configure:
   ```
   Root Directory: frontend
   Build Command: npm ci && npm run build -- --configuration production
   Publish Directory: dist/frontend/browser
   ```

4. **DO NOT add any environment variables**
5. Create Static Site
6. Wait 3-5 minutes
7. Copy frontend URL

### 4️⃣ Update CORS (1 minute)

1. Go to Backend service → Environment
2. Update `CORS_ORIGINS` to your frontend URL
3. Save (backend will redeploy)

---

## ✅ Verification

Visit frontend URL and:
- Login with demo/demo123
- Create/edit/delete tasks
- View analytics
- Check Network tab: requests should go to your backend URL (NOT localhost:8080)

---

## 🐳 Run with Docker

```bash
# Start everything
docker-compose up -d

# Access the app
Frontend: http://localhost
Backend: http://localhost:8080
```

---

## 💻 Run Locally (Development)

### Backend:
```bash
cd backend
./mvnw spring-boot:run
# Runs on http://localhost:8080
```

### Frontend:
```bash
cd frontend
npm install
npm start
# Runs on http://localhost:4200
```

### Test with Render Database:
```bash
./run-with-render-db.sh
```

---

## 🆘 Troubleshooting

### Frontend calls localhost:8080 instead of production backend

**Cause:** Build cache or missing `--configuration production` flag

**Fix:**
1. Verify Build Command: `npm ci && npm run build -- --configuration production`
2. Manual Deploy → Clear build cache & deploy
3. Wait for rebuild (3-5 minutes)

### Backend won't start (Connection to localhost:5432)

**Cause:** Missing or incorrect DATABASE_URL environment variable

**Fix:**
1. Check DATABASE_URL format: `jdbc:postgresql://<host>:5432/<database>`
2. Must include full hostname (e.g., `.ohio-postgres.render.com`)
3. NO username:password in URL (use separate DB_USERNAME and DB_PASSWORD)

### CORS errors

**Cause:** CORS_ORIGINS doesn't match frontend URL

**Fix:**
1. Check exact frontend URL (including https://)
2. Update CORS_ORIGINS on backend
3. Save and wait for redeploy

### Build fails on Render

**Cause:** Incorrect Root Directory or Build Command

**Fix:**
- Backend Root Directory: `backend`
- Frontend Root Directory: `frontend`
- Frontend Build Command must include `-- --configuration production`

---

## 📁 Project Structure

```
Task-Tracker-App/
├── backend/                 # Spring Boot API
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/               # Angular App
│   ├── src/
│   │   ├── app/
│   │   └── environments/
│   │       ├── environment.ts          # Development (localhost:8080)
│   │       └── environment.prod.ts     # Production (Render backend URL)
│   ├── angular.json
│   └── package.json
├── docker-compose.yml
└── DEPLOYMENT_GUIDE.md    # This file
```

---

## 🔧 Configuration Files

### Backend Environment Variables (Render)
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://<host>:5432/<database>
DB_USERNAME=<username>
DB_PASSWORD=<password>
JWT_SECRET=<secret>
CORS_ORIGINS=<frontend-url>
```

### Frontend Environment (No variables needed!)
The API URL is configured in code:
- `frontend/src/environments/environment.prod.ts`

---

## 🎓 Key Concepts

### Static Site vs Web Service
- **Static Site (Frontend):** Pre-built HTML/CSS/JS files, no runtime variables
- **Web Service (Backend):** Running server that reads environment variables

### Angular Production Build
- Uses `fileReplacements` to swap `environment.ts` with `environment.prod.ts`
- API URL is hardcoded into JavaScript during build
- Must include `--configuration production` flag

### JDBC URL Format
- Spring Boot needs: `jdbc:postgresql://host:port/database`
- Render provides: `postgresql://user:pass@host:port/database`
- Extract host, port, database → put user/pass in separate variables

---

## 📊 Environment Summary

| Component | Environment | API URL |
|-----------|-------------|---------|
| Frontend Local | `environment.ts` | `http://localhost:8080/api` |
| Frontend Production | `environment.prod.ts` | `https://task-tracker-app-1uok.onrender.com/api` |
| Backend Local | application.properties | H2 in-memory database |
| Backend Production | application-prod.properties | PostgreSQL on Render |

---

## 🎉 Success Checklist

- [ ] Backend deployed and running
- [ ] Database connected (no localhost:5432 errors)
- [ ] Frontend deployed with production build
- [ ] CORS configured correctly
- [ ] Login works
- [ ] Can create/edit/delete tasks
- [ ] Analytics page works
- [ ] No localhost:8080 in Network tab

---

## 📞 Need Help?

Check the logs:
- **Render:** Dashboard → Service → Logs tab
- **Local:** Terminal output

Common issues are covered in the Troubleshooting section above.

---

**Total Deployment Time:** ~15 minutes  
**Cost:** $0/month (Free tier)  
**Technologies:** Spring Boot, Angular, PostgreSQL, Docker, Render

---

*Last Updated: November 2024*

