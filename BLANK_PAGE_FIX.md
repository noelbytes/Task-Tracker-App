# 🐛 Blank Page Fix - Task Tracker App

## Issue Diagnosed

You're seeing a **blank page** at `http://localhost/login` because the Angular application is **failing to bootstrap** due to an invalid provider in the configuration.

## Root Cause

**File**: `frontend/src/app/app.config.ts`

**Problem**: The code was importing and using `provideBrowserGlobalErrorListeners()` which **does not exist** in Angular. This causes the entire Angular app to fail during initialization, resulting in a blank page.

```typescript
// ❌ BEFORE (BROKEN)
import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),  // ❌ This doesn't exist!
    provideZoneChangeDetection({ eventCoalescing: true }),
    // ... other providers
  ]
};
```

## Fix Applied

I've removed the invalid `provideBrowserGlobalErrorListeners` import and provider:

```typescript
// ✅ AFTER (FIXED)
import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideCharts(withDefaultRegisterables())
  ]
};
```

## How to Apply the Fix

### Option 1: Run the Rebuild Script (Recommended)

```bash
cd /home/noelbytes/Projects/Task-Tracker-App
chmod +x rebuild-frontend.sh
sudo ./rebuild-frontend.sh
```

### Option 2: Manual Rebuild

```bash
cd /home/noelbytes/Projects/Task-Tracker-App

# Rebuild frontend with the fix
sudo docker compose build frontend

# Restart the container
sudo docker compose up -d frontend

# Verify it's running
sudo docker compose ps
```

### Option 3: Full Rebuild (if needed)

```bash
cd /home/noelbytes/Projects/Task-Tracker-App

# Stop all containers
sudo docker compose down

# Rebuild and start everything
sudo docker compose up --build -d

# Check status
sudo docker compose ps
```

## Verification

After rebuilding, the app should work correctly:

1. **Open** http://localhost or http://localhost/login
2. **You should see** the login form (not a blank page)
3. **Login with**:
   - Username: `demo`
   - Password: `demo123`

## Debugging Tips

If you still see a blank page after rebuilding:

### 1. Check Browser Console

Open DevTools (F12) → Console tab
- Look for any JavaScript errors
- Common issues: "Cannot find module" or "Failed to fetch"

### 2. Check Container Logs

```bash
# Frontend logs
sudo docker compose logs frontend

# Backend logs (if needed)
sudo docker compose logs backend
```

### 3. Verify Files Are Served

```bash
# Check if JavaScript file loads
curl -I http://localhost/main-JSYLQVKN.js

# Should return: HTTP 200 OK
# Content-Type: application/javascript
```

### 4. Hard Refresh Browser

- **Chrome/Firefox**: Ctrl + Shift + R (or Cmd + Shift + R on Mac)
- **Safari**: Cmd + Option + R
- This clears the cache and reloads everything

### 5. Check Backend Is Running

```bash
sudo docker compose ps

# You should see:
# - tasktracker-postgres (healthy)
# - tasktracker-backend (Up)
# - tasktracker-frontend (Up)
```

If backend is missing:

```bash
sudo docker compose up -d backend
```

## Files Modified

1. ✅ `frontend/src/app/app.config.ts` - Removed invalid provider
2. ✅ `rebuild-frontend.sh` - Created rebuild script

## Summary

The blank page was caused by:
1. ❌ Invalid `provideBrowserGlobalErrorListeners()` in app.config.ts
2. ✅ Fixed by removing the non-existent provider
3. 🔄 Rebuild required to apply the fix

**After rebuilding**, your Task Tracker app should display the login page correctly! 🎉

## Still Having Issues?

If the problem persists after rebuilding:

1. Check the browser console for specific JavaScript errors
2. Verify all containers are running: `sudo docker compose ps`
3. Try accessing http://localhost directly (not just /login)
4. Clear your browser cache completely
5. Check logs: `sudo docker compose logs -f`

---

**Last Updated**: November 6, 2025

