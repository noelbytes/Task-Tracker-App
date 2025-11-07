# 🚨 Backend 503 Error - Troubleshooting

## ❌ Current Issue

**Error:** 503 Service Unavailable  
**URL:** https://task-tracker-app-1uok.onrender.com/api/auth/login  
**Method:** OPTIONS (CORS preflight)

---

## 🔍 What This Means

A **503 error** means the backend service is unavailable. Common causes:

1. **Service is sleeping** (Render free tier sleeps after 15 minutes of inactivity)
2. **Service crashed or failed to start**
3. **Service is redeploying**
4. **Out of memory or resources**

---

## ✅ IMMEDIATE FIXES

### Fix 1: Wake Up the Service (If Sleeping)

Render free tier services sleep after 15 minutes of inactivity. First request takes 30-60 seconds to wake up.

**Action:**
1. Visit your backend URL directly: `https://task-tracker-app-1uok.onrender.com`
2. Wait 30-60 seconds
3. Refresh the page
4. Then try your frontend again

---

### Fix 2: Check Backend Service Status

1. Go to https://dashboard.render.com
2. Click on your **backend service** (task-tracker-app-1uok)
3. Check the status at the top:
   - 🟢 **Live** - Service is running
   - 🔵 **Building** - Service is deploying
   - 🔴 **Failed** - Service crashed
   - ⚫ **Sleeping** - Service needs to wake up

---

### Fix 3: Check the Logs

1. Go to your backend service on Render
2. Click **"Logs"** tab
3. Look for errors at the bottom

**What to look for:**
- ✅ `Started TaskTrackerApplication in X seconds` - Service is running
- ❌ `Error creating bean` - Configuration issue
- ❌ `Connection refused` - Database connection issue
- ❌ `Out of memory` - Service ran out of memory

---

## 🔧 Common Causes & Solutions

### Cause 1: Service is Sleeping (Most Common)
**Solution:** Wait 30-60 seconds for first request to wake it up

### Cause 2: Database Connection Failed
**Symptoms:** Logs show "Connection to localhost:5432 refused" or similar
**Solution:** 
- Verify DATABASE_URL environment variable is correct
- Check format: `jdbc:postgresql://<host>:5432/<database>`
- Verify DB_USERNAME and DB_PASSWORD are set

### Cause 3: Out of Memory
**Symptoms:** Service stops responding, logs show "OutOfMemoryError"
**Solution:** 
- Render free tier has limited memory
- Service will restart automatically
- Consider upgrading if this happens frequently

### Cause 4: Port Binding Issue
**Symptoms:** Logs show "Address already in use" or "Failed to bind"
**Solution:**
- Render expects app to use PORT environment variable
- Your Dockerfile should expose port 8080 (already configured correctly)

### Cause 5: Environment Variables Missing
**Symptoms:** Logs show "Unable to start web server" or bean creation errors
**Solution:**
- Verify all required environment variables are set:
  - SPRING_PROFILES_ACTIVE=prod
  - DATABASE_URL
  - DB_USERNAME
  - DB_PASSWORD
  - JWT_SECRET
  - CORS_ORIGINS

---

## 📋 Step-by-Step Troubleshooting

### Step 1: Wake the Service
```
Visit: https://task-tracker-app-1uok.onrender.com
Wait: 30-60 seconds
Refresh
```

### Step 2: Check Status
```
Dashboard → Backend Service → Check status indicator
```

### Step 3: Read Recent Logs
```
Dashboard → Backend Service → Logs → Scroll to bottom
```

### Step 4: Look for Startup Confirmation
Should see:
```
✅ HikariPool-1 - Starting...
✅ HikariPool-1 - Start completed
✅ Started TaskTrackerApplication in X seconds
```

### Step 5: Test Backend Directly
```
Visit: https://task-tracker-app-1uok.onrender.com/api/tasks
Should: Either get data or 401 Unauthorized (both mean it's running)
Should NOT: Get 503 or timeout
```

---

## 🚀 Quick Commands to Test Backend

### Test 1: Basic Health Check
```bash
curl https://task-tracker-app-1uok.onrender.com
```
**Expected:** Some response (not 503)

### Test 2: Login Endpoint
```bash
curl -X POST https://task-tracker-app-1uok.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}'
```
**Expected:** JWT token response (not 503)

---

## 🔄 If Service Crashed - Force Restart

1. Go to your backend service on Render
2. Click **"Manual Deploy"** button
3. Select **"Deploy latest commit"**
4. Wait 3-5 minutes for redeploy
5. Check logs to verify startup

---

## ⏰ Free Tier Limitations

**Sleep after inactivity:** 15 minutes  
**Wake-up time:** 30-60 seconds  
**Monthly hours:** 750 hours (about 31 days)

**Best Practices:**
- First request after sleep will be slow
- Consider a keep-alive ping service (check Render ToS)
- Or just wait patiently for wake-up

---

## 💡 How to Check If It's Just Sleeping

Try this:
1. Visit backend URL
2. If you see "loading" for 30-60 seconds, then it works → **It was sleeping**
3. If you get 503 immediately → **Service has issues**

---

## 🆘 If Nothing Works

1. Check Render Status: https://status.render.com
2. Verify environment variables are all set correctly
3. Redeploy the service
4. Check database is "Available" (not suspended)
5. Review logs for specific error messages

---

## 📞 Next Steps

Based on what you find:

**If service is sleeping:** Just wait 30-60 seconds  
**If service crashed:** Check logs, verify env vars, redeploy  
**If database connection failed:** Fix DATABASE_URL/credentials  
**If still stuck:** Share the error from logs for specific help

---

*The 503 error is most likely just the service sleeping on Render's free tier.*  
*Try visiting the backend URL directly and waiting 30-60 seconds!*

