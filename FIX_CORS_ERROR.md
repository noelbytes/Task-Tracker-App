# 🔧 CORS Error Fix - Frontend Calling localhost:8080

## ❌ Problem

Your frontend on Render is calling `http://localhost:8080/api/auth/login` instead of your live backend URL `https://task-tracker-app-1uok.onrender.com/api/auth/login`.

This happens because the production build wasn't using the production environment file.

---

## ✅ Solution Applied

I've fixed the Angular configuration to properly use the production environment during build:

### Changes Made:

1. **Updated `angular.json`** - Added `fileReplacements` to swap environment files during production build
2. **Updated all deployment guides** - Changed build command to explicitly use production configuration

---

## 🚀 What You Need to Do on Render

Since your frontend static site is already deployed, you need to **trigger a rebuild** with the new configuration:

### Option 1: Manual Redeploy (Recommended)

1. Go to your **frontend static site** on Render
2. Click **"Manual Deploy"** button (top right)
3. Select **"Clear build cache & deploy"**
4. Wait 3-5 minutes for rebuild

### Option 2: Push a Change

The changes are already pushed to GitHub, so Render should automatically redeploy. If not:

1. Go to your frontend static site
2. Check if it's rebuilding automatically
3. If not, use Option 1 above

---

## 🔍 Verify the Fix

After the frontend rebuilds:

1. **Visit your frontend URL** (e.g., `https://tasktracker-frontend-XXXX.onrender.com`)
2. **Open browser DevTools** (F12)
3. **Go to Network tab**
4. **Try to login** with demo/demo123
5. **Check the request URL** - it should now be:
   ```
   https://task-tracker-app-1uok.onrender.com/api/auth/login
   ```
   NOT `http://localhost:8080/api/auth/login`

---

## 📝 Technical Details

### What Was Wrong:

The `angular.json` was missing the `fileReplacements` configuration:

```json
"production": {
  "budgets": [...],
  "outputHashing": "all"
}
```

### What's Fixed:

Now it has proper file replacement:

```json
"production": {
  "fileReplacements": [
    {
      "replace": "src/environments/environment.ts",
      "with": "src/environments/environment.prod.ts"
    }
  ],
  "budgets": [...],
  "outputHashing": "all"
}
```

This ensures that when you build with `--configuration production`, it uses:
- `environment.prod.ts` → `apiUrl: 'https://task-tracker-app-1uok.onrender.com/api'`

Instead of:
- `environment.ts` → `apiUrl: 'http://localhost:8080/api'`

---

## 🎯 Build Command Update

The build command on Render should be:

```bash
npm ci && npm run build -- --configuration production
```

This explicitly tells Angular to use the production configuration.

---

## ⚠️ Important: Update Build Command on Render

If you created the static site before this fix, you need to update the build command:

1. Go to your **frontend static site** on Render
2. Click **Settings** (left sidebar)
3. Find **Build Command**
4. Update to: `npm ci && npm run build -- --configuration production`
5. Click **Save Changes**
6. Trigger a manual redeploy

---

## ✅ After Fix - Expected Behavior

### Browser Network Tab Should Show:

```
✅ Request URL: https://task-tracker-app-1uok.onrender.com/api/auth/login
✅ Status: 200 OK
✅ Response: { token: "...", username: "demo" }
```

### CORS Error Should Be Gone:

- ✅ No more "localhost:8080" in requests
- ✅ No more CORS errors
- ✅ Login works successfully
- ✅ All API calls use the correct backend URL

---

## 🆘 If CORS Error Persists

After the frontend rebuilds with the correct configuration, if you still see CORS errors:

1. **Check CORS_ORIGINS on backend:**
   - Go to backend service → Environment
   - Verify `CORS_ORIGINS` matches your frontend URL exactly
   - Example: `https://tasktracker-frontend-abc123.onrender.com`
   - Save if you need to update

2. **Check browser console:**
   - Should show requests going to `task-tracker-app-1uok.onrender.com`
   - NOT to `localhost:8080`

3. **Clear browser cache:**
   - Hard refresh: `Ctrl+Shift+R` (Windows/Linux) or `Cmd+Shift+R` (Mac)
   - Or clear browser cache completely

---

## 📋 Checklist

Before testing:

- [ ] Angular configuration has `fileReplacements` (✅ Done)
- [ ] Changes pushed to GitHub (✅ Done)
- [ ] Frontend static site rebuilt on Render
- [ ] Build command includes `--configuration production`
- [ ] Backend CORS_ORIGINS set to frontend URL
- [ ] Browser cache cleared

After testing:

- [ ] Login request goes to `https://task-tracker-app-1uok.onrender.com`
- [ ] No CORS errors in console
- [ ] Login successful
- [ ] Can create/edit/delete tasks

---

## 🎉 Summary

**Root Cause:** Angular wasn't swapping environment files during production build

**Fix:** Added `fileReplacements` to `angular.json` + updated build command

**Action Required:** Trigger a rebuild of your frontend on Render

After the rebuild, your frontend will correctly call the live backend URL! 🚀

