# 🔧 FIX YOUR RENDER ENVIRONMENT VARIABLES

## ❌ Current Problem

Your Render backend environment variables have TWO critical issues:

1. **DATABASE_URL has embedded credentials** (causes UnknownHostException)
2. **JWT_SECRET has a newline character `\n`** (causes parsing errors)

---

## ✅ SOLUTION: Update These on Render

Go to: **Render Dashboard → Your Backend Service → Environment Tab**

### Click on each variable and update to these EXACT values:

```
SPRING_PROFILES_ACTIVE=prod

DATABASE_URL=jdbc:postgresql://dpg-d46i7dhr0fns73fn6c50-a.ohio-postgres.render.com:5432/tasktracker_db_01j9

DB_USERNAME=tasktracker_db_01j9_user

DB_PASSWORD=YmeGK9T6SfHMN4NyZSdT0r1VgdZs3hEy

JWT_SECRET=9zTtC/gmpO5ROWyD+WClwfMBRoaz02xkgbBNuIt3+JvYiWNIbhOyO8PA3DqYU7Tvl4g++jRcz1bl9wqssi9lNg==

CORS_ORIGINS=https://tasktracker-frontend.onrender.com
```

---

## 🎯 What Changed from Your Current Setup

### DATABASE_URL
**WRONG (your current):**
```
jdbc:postgresql://tasktracker_db_01j9_user:YmeGK9T6SfHMN4NyZSdT0r1VgdZs3hEy@dpg-d46i7dhr0fns73fn6c50-a.ohio-postgres.render.com/tasktracker_db_01j9
```
❌ Has username:password embedded in URL
❌ Driver tries to resolve "tasktracker_db_01j9_user:YmeGK...@dpg-..." as hostname = UnknownHostException

**CORRECT:**
```
jdbc:postgresql://dpg-d46i7dhr0fns73fn6c50-a.ohio-postgres.render.com:5432/tasktracker_db_01j9
```
✅ No credentials in URL
✅ Has full hostname with .ohio-postgres.render.com
✅ Has port :5432
✅ Credentials provided separately via DB_USERNAME and DB_PASSWORD

### JWT_SECRET
**WRONG (your current):**
```
9zTtC/gmpO5ROWyD+WClwfMBRoaz02xkgbBNuIt3+JvYiWNIbhOyO8PA3DqYU7Tv\nl4g++jRcz1bl9wqssi9lNg==
```
❌ Has `\n` newline character in the middle

**CORRECT:**
```
9zTtC/gmpO5ROWyD+WClwfMBRoaz02xkgbBNuIt3+JvYiWNIbhOyO8PA3DqYU7Tvl4g++jRcz1bl9wqssi9lNg==
```
✅ Single continuous line, no newline

---

## 📝 Step-by-Step Instructions

1. **Open Render Dashboard**
   - Go to: https://dashboard.render.com

2. **Select Your Backend Service**
   - Click on your backend web service (e.g., `tasktracker-backend`)

3. **Go to Environment Tab**
   - Click "Environment" in the left sidebar

4. **Update DATABASE_URL**
   - Click on the `DATABASE_URL` variable
   - Delete the current value completely
   - Paste: `jdbc:postgresql://dpg-d46i7dhr0fns73fn6c50-a.ohio-postgres.render.com:5432/tasktracker_db_01j9`
   - Make sure there are NO quotes, NO spaces at the beginning or end

5. **Update JWT_SECRET**
   - Click on the `JWT_SECRET` variable
   - Delete the current value completely
   - Paste: `9zTtC/gmpO5ROWyD+WClwfMBRoaz02xkgbBNuIt3+JvYiWNIbhOyO8PA3DqYU7Tvl4g++jRcz1bl9wqssi9lNg==`
   - Make sure it's ONE LINE with NO newline character

6. **Verify Other Variables**
   - `SPRING_PROFILES_ACTIVE` = `prod`
   - `DB_USERNAME` = `tasktracker_db_01j9_user`
   - `DB_PASSWORD` = `YmeGK9T6SfHMN4NyZSdT0r1VgdZs3hEy`
   - `CORS_ORIGINS` = `https://tasktracker-frontend.onrender.com`

7. **Save Changes**
   - Click "Save Changes" button
   - Render will automatically trigger a redeploy

8. **Wait for Redeploy**
   - Takes 3-5 minutes
   - Watch the "Events" or "Logs" tab

---

## ✅ Success Signs in Logs

After the redeploy completes, check the **Logs** tab. You should see:

```
✅ HikariPool-1 - Starting...
✅ HikariPool-1 - Start completed.
✅ HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@XXXXXXXX
✅ Started TaskTrackerApplication in X.XXX seconds (process running for X.XXX)
```

**Should NOT see:**
- ❌ "Connection to localhost:5432 refused"
- ❌ "UnknownHostException"
- ❌ "The connection attempt failed"
- ❌ Any mention of "tasktracker_db_01j9_user:YmeGK..." in error messages

---

## 🧪 Optional: Test Locally First

Before updating Render, you can test locally to confirm the correct config works:

```bash
./run-with-render-db.sh
```

This script already has the CORRECT environment variables. If your backend starts successfully locally, you know the fix will work on Render too.

---

## 🆘 If It Still Fails

1. **Double-check DATABASE_URL has NO credentials embedded**
   - Should start with: `jdbc:postgresql://dpg-`
   - Should NOT contain: `tasktracker_db_01j9_user:YmeGK...@`

2. **Double-check JWT_SECRET is one line**
   - Copy it into a text editor
   - Make sure there's NO line break in the middle
   - Should be 88 characters long

3. **Verify all 6 variables are set**
   - Count them in the Environment tab
   - Should have exactly 6 variables

4. **Check database is running**
   - Go to your PostgreSQL database page
   - Status should show "Available" with green dot

---

## 📊 Environment Variables Checklist

Before saving on Render, verify:

- [ ] `SPRING_PROFILES_ACTIVE` = `prod` (4 characters)
- [ ] `DATABASE_URL` = starts with `jdbc:postgresql://dpg-` (NO user:pass in it)
- [ ] `DATABASE_URL` = ends with `:5432/tasktracker_db_01j9`
- [ ] `DB_USERNAME` = `tasktracker_db_01j9_user` (24 characters)
- [ ] `DB_PASSWORD` = `YmeGK9T6SfHMN4NyZSdT0r1VgdZs3hEy` (32 characters)
- [ ] `JWT_SECRET` = 88 characters, NO newline, NO quotes
- [ ] `CORS_ORIGINS` = `https://tasktracker-frontend.onrender.com`

---

## 🎉 After Success

Once the backend starts successfully:

1. Visit your frontend URL: `https://tasktracker-frontend.onrender.com`
2. Login with: `demo` / `demo123`
3. Test creating, updating, and deleting tasks
4. Check analytics page

Your app should now be fully deployed and working! 🚀

