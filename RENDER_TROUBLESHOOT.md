# 🔧 Render Deployment Troubleshooting

## ❌ Error: "Connection to localhost:5432 refused"

**This means:** Your `JDBC_DATABASE_URL` environment variable is **NOT SET** on Render.

---

## ✅ Solution: Set Environment Variables Correctly

### Step-by-Step Fix:

#### 1. Get Your Database URL

1. Go to your Render Dashboard: https://dashboard.render.com
2. Click on your **PostgreSQL database** (e.g., `tasktracker-db`)
3. Scroll down to **"Connections"** section
4. **COPY** the **"Internal Database URL"**

It will look like:
```
postgresql://tasktracker_db_01j9_user:YmeGK9T6SfHMN4NyZSdT0r1VgdZs3hEy@dpg-d46i7dhr0fns73fn6c50-a/tasktracker_db_01j9
```

#### 2. Convert to JDBC Format

Add `jdbc:` at the beginning:
```
jdbc:postgresql://tasktracker_db_01j9_user:YmeGK9T6SfHMN4NyZSdT0r1VgdZs3hEy@dpg-d46i7dhr0fns73fn6c50-a/tasktracker_db_01j9
```

#### 3. Set Environment Variables on Backend Service

1. Go to your **Backend Web Service** (e.g., `tasktracker-backend`)
2. Click **"Environment"** tab on the left
3. Click **"Add Environment Variable"**
4. Add these **EXACTLY**:

| Key | Value | Example |
|-----|-------|---------|
| `SPRING_PROFILES_ACTIVE` | `prod` | `prod` |
| `JDBC_DATABASE_URL` | Your JDBC URL from step 2 | `jdbc:postgresql://tasktracker_db_01j9_user:YmeGK...@dpg-.../tasktracker_db_01j9` |
| `JWT_SECRET` | Generate with: `openssl rand -base64 64` | `a4f8b2c...` |
| `CORS_ORIGINS` | Your frontend URL | `https://tasktracker-frontend.onrender.com` |

5. Click **"Save Changes"**
6. Render will automatically redeploy (takes 3-5 minutes)

---

## 🔍 How to Verify Environment Variables Are Set

### In Render Dashboard:

1. Go to your backend service
2. Click **"Environment"** tab
3. You should see **4 variables** listed:
   - ✅ `SPRING_PROFILES_ACTIVE`
   - ✅ `JDBC_DATABASE_URL`
   - ✅ `JWT_SECRET`
   - ✅ `CORS_ORIGINS`

### In the Logs:

1. Click **"Logs"** tab
2. Look for these lines (should NOT say localhost):
   ```
   HikariPool-1 - Starting...
   HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@...
   ```

3. Should see:
   ```
   Started TaskTrackerApplication in X.XXX seconds
   ```

---

## 🚨 Common Mistakes

### ❌ WRONG: Using `DATABASE_URL` instead of `JDBC_DATABASE_URL`
```
DATABASE_URL=postgresql://user:pass@host:5432/db  ❌
```

### ✅ CORRECT: Using `JDBC_DATABASE_URL` with `jdbc:` prefix
```
JDBC_DATABASE_URL=jdbc:postgresql://user:pass@host:5432/db  ✅
```

---

### ❌ WRONG: Missing `jdbc:` prefix
```
JDBC_DATABASE_URL=postgresql://user:pass@host:5432/db  ❌
```

### ✅ CORRECT: Has `jdbc:` prefix
```
JDBC_DATABASE_URL=jdbc:postgresql://user:pass@host:5432/db  ✅
```

---

## 📋 Complete Checklist

Before your backend will work, verify:

- [ ] PostgreSQL database is created and running
- [ ] Backend service has **4 environment variables** set
- [ ] `JDBC_DATABASE_URL` starts with `jdbc:postgresql://`
- [ ] `JDBC_DATABASE_URL` includes username and password
- [ ] `SPRING_PROFILES_ACTIVE` is set to `prod`
- [ ] Backend service is set to **Docker** environment
- [ ] Root Directory is set to `backend`

---

## 🎯 Quick Test

After setting environment variables and redeploying:

1. Wait for deploy to complete (5 minutes)
2. Visit: `https://YOUR-BACKEND-URL.onrender.com/api/auth/test` (or any endpoint)
3. Should see response (not connection error)

---

## 💡 Still Not Working?

### Check Database Status:
1. Go to PostgreSQL database page
2. Status should show: **Available** (green dot)
3. If "Suspended", click to resume

### Check Backend Logs:
1. Go to backend service
2. Click "Logs" tab
3. Look for specific error messages
4. Share the error if still having issues

---

## 📞 Need More Help?

Common issues solved in:
- `DEPLOY_CHEATSHEET.txt` - Quick reference
- `QUICK_DEPLOY.md` - Step-by-step guide
- `FREE_DEPLOYMENT_GUIDE.md` - Detailed walkthrough

