# 🔧 Render Environment Variables - Correct Setup

## Your Database Connection Details

From your PostgreSQL database page on Render, you have:
- **Host:** `dpg-d46i7dhr0fns73fn6c50-a.ohio-postgres.render.com`
- **Port:** `5432`
- **Database:** `tasktracker_db_01j9`
- **Username:** `tasktracker_db_01j9_user`
- **Password:** `YmeGK9T6SfHMN4NyZSdT0r1VgdZs3hEy`

---

## ✅ Correct Environment Variables for Backend Service

### On Render Backend Service → Environment Tab:

Set these **5 variables EXACTLY**:

| Key | Value |
|-----|-------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DATABASE_URL` | `jdbc:postgresql://dpg-d46i7dhr0fns73fn6c50-a.ohio-postgres.render.com:5432/tasktracker_db_01j9` |
| `DB_USERNAME` | `tasktracker_db_01j9_user` |
| `DB_PASSWORD` | `YmeGK9T6SfHMN4NyZSdT0r1VgdZs3hEy` |
| `JWT_SECRET` | `9zTtC/gmpO5ROWyD+WClwfMBRoaz02xkgbBNuIt3+JvYiWNIbhOyO8PA3DqYU7Tvl4g++jRcz1bl9wqssi9lNg==` |
| `CORS_ORIGINS` | `https://tasktracker-frontend.onrender.com` |

---

## 🚨 Important Notes:

### 1. DATABASE_URL Format
**CORRECT:**
```
jdbc:postgresql://dpg-d46i7dhr0fns73fn6c50-a.ohio-postgres.render.com:5432/tasktracker_db_01j9
```

**Key points:**
- ✅ Starts with `jdbc:postgresql://`
- ✅ Has hostname ending in `.ohio-postgres.render.com`
- ✅ Has port `:5432`
- ✅ Database name at the end
- ❌ Does NOT include username:password in the URL (those go in separate variables)

### 2. Remove JDBC_DATABASE_URL
If you have `JDBC_DATABASE_URL` on Render, **DELETE IT**. We're now using:
- `DATABASE_URL` (for the connection string)
- `DB_USERNAME` (for username)
- `DB_PASSWORD` (for password)

This is more compatible with how Spring Boot parses PostgreSQL connections.

### 3. JWT_SECRET Format
**CORRECT (no quotes, single line):**
```
9zTtC/gmpO5ROWyD+WClwfMBRoaz02xkgbBNuIt3+JvYiWNIbhOyO8PA3DqYU7Tvl4g++jRcz1bl9wqssi9lNg==
```

**WRONG (has quotes and newline):**
```
"9zTtC/gmpO5ROWyD+WClwfMBRoaz02xkgbBNuIt3+JvYiWNIbhOyO8PA3DqYU7Tv\nl4g++jRcz1bl9wqssi9lNg=="
```

---

## 📋 Step-by-Step Instructions

### On Render Dashboard:

1. **Go to your Backend Web Service**
2. **Click "Environment" tab** on the left
3. **DELETE these if they exist:**
   - `JDBC_DATABASE_URL` (not needed anymore)
   
4. **UPDATE/ADD these variables:**

   Click "Add Environment Variable" for each one:

   **Variable 1:**
   - Key: `SPRING_PROFILES_ACTIVE`
   - Value: `prod`

   **Variable 2:**
   - Key: `DATABASE_URL`
   - Value: `jdbc:postgresql://dpg-d46i7dhr0fns73fn6c50-a.ohio-postgres.render.com:5432/tasktracker_db_01j9`

   **Variable 3:**
   - Key: `DB_USERNAME`
   - Value: `tasktracker_db_01j9_user`

   **Variable 4:**
   - Key: `DB_PASSWORD`
   - Value: `YmeGK9T6SfHMN4NyZSdT0r1VgdZs3hEy`

   **Variable 5:**
   - Key: `JWT_SECRET`
   - Value: `9zTtC/gmpO5ROWyD+WClwfMBRoaz02xkgbBNuIt3+JvYiWNIbhOyO8PA3DqYU7Tvl4g++jRcz1bl9wqssi9lNg==`
   
   **Variable 6:**
   - Key: `CORS_ORIGINS`
   - Value: `https://tasktracker-frontend.onrender.com`

5. **Click "Save Changes"**
6. **Wait for automatic redeploy** (3-5 minutes)

---

## ✅ Verification

After the deploy completes, check the **Logs** tab. You should see:

```
✅ HikariPool-1 - Starting...
✅ HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@...
✅ Started TaskTrackerApplication in X.XXX seconds
```

**Should NOT see:**
- ❌ "Connection to localhost:5432 refused"
- ❌ "UnknownHostException"
- ❌ "Connection attempt failed"

---

## 🎉 Success!

Once you see "Started TaskTrackerApplication" in the logs, your backend is running!

Test it by visiting:
```
https://your-backend-url.onrender.com/api/auth/test
```

Or test the full app at your frontend URL!

