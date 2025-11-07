# FREE AI on Render - Complete Deployment Guide

## 🎯 Overview

Your Task Tracker uses **FREE AI** in both development and production:
- **Local Development:** Ollama (runs on your machine)
- **Production (Render):** Groq (free cloud API)

Both are 100% FREE! No credit card required!

---

## 🚀 Quick Setup for Render

### Step 1: Get Free Groq API Key

1. Visit: **https://console.groq.com**
2. Click "Sign Up" (no credit card needed)
3. Verify your email
4. Go to "API Keys" section
5. Click "Create API Key"
6. Copy the key (starts with `gsk_...`)

**Free Tier Limits:**
- 30 requests per minute
- 14,400 requests per day
- More than enough for a task tracker!

---

### Step 2: Configure Render Environment Variables

Go to your backend service on Render:

1. Click on your **backend service**
2. Go to **Environment** tab
3. Add these environment variables:

```
GROQ_API_KEY=gsk_your_actual_key_here
SPRING_PROFILES_ACTIVE=prod
```

4. Click **Save Changes**
5. Render will automatically redeploy

---

### Step 3: Test AI Features

Once deployed, test the AI endpoints:

```bash
# Get your backend URL from Render
BACKEND_URL=https://your-backend.onrender.com

# Login first
TOKEN=$(curl -s -X POST $BACKEND_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}' | jq -r '.token')

# Test AI status
curl -s -X GET $BACKEND_URL/api/ai/status \
  -H "Authorization: Bearer $TOKEN" | jq .

# Should return:
# {
#   "available": true,
#   "provider": "Groq (Free Cloud API)",
#   "model": "llama3-8b-8192",
#   "cost": "$0.00 - Completely Free!"
# }
```

---

## 🏗️ How It Works

### Development vs Production

**Local Development (application.properties):**
```properties
# Uses Ollama - runs locally on your machine
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama2
```

**Production/Render (application-prod.properties):**
```properties
# Uses Groq - free cloud API
spring.ai.openai.base-url=https://api.groq.com/openai/v1
spring.ai.openai.api-key=${GROQ_API_KEY}
spring.ai.openai.chat.options.model=llama3-8b-8192
```

### Why This Works

- **Groq uses OpenAI-compatible API** - So we use `spring-ai-openai` with Groq's base URL
- **Both dependencies included** in `pom.xml`
- **Profile-based configuration** - Spring picks the right one automatically
- **Environment variable** - `GROQ_API_KEY` set on Render

---

## 📊 Groq Free Tier

### What You Get (FREE)

| Feature | Limit |
|---------|-------|
| Requests per minute | 30 RPM |
| Requests per day | 14,400 RPD |
| Tokens per minute | 60,000 TPM |
| Model | llama3-8b-8192 |
| Cost | $0.00 |

**Example Usage:**
- 10 users × 10 AI requests/day = 100 requests/day
- Well within the 14,400/day limit!

### Available Models (All Free)

```properties
# Fast, good quality (recommended)
spring.ai.openai.chat.options.model=llama3-8b-8192

# Smaller, faster
spring.ai.openai.chat.options.model=llama3-7b-8192

# Larger, better quality
spring.ai.openai.chat.options.model=mixtral-8x7b-32768

# Very fast
spring.ai.openai.chat.options.model=gemma-7b-it
```

To change model, update `application-prod.properties` or set environment variable:
```
GROQ_MODEL=mixtral-8x7b-32768
```

---

## 🔧 Complete Render Configuration

### Environment Variables to Set

| Variable | Value | Purpose |
|----------|-------|---------|
| `GROQ_API_KEY` | `gsk_your_key` | Groq API authentication |
| `SPRING_PROFILES_ACTIVE` | `prod` | Use production config |
| `DATABASE_URL` | (auto-set) | PostgreSQL connection |
| `JWT_SECRET` | (your secret) | JWT token signing |
| `CORS_ORIGINS` | `https://your-frontend.onrender.com` | CORS config |

### Build Command (Already Configured)

```bash
./mvnw clean install -DskipTests
```

### Start Command (Already Configured)

```bash
java -jar target/task-tracker-backend-1.0.0.jar
```

---

## 🧪 Testing After Deployment

### 1. Check AI Service Status

```bash
curl https://your-backend.onrender.com/api/ai/status
```

**Expected Response:**
```json
{
  "available": true,
  "provider": "Groq (Free Cloud API)",
  "model": "llama3-8b-8192",
  "cost": "$0.00 - Completely Free!"
}
```

### 2. Test Natural Language Parsing

```bash
# Login
TOKEN=$(curl -s -X POST https://your-backend.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}' | jq -r '.token')

# Parse task
curl -X POST https://your-backend.onrender.com/api/ai/parse-task \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"text": "Finish project report by Friday - urgent!"}' | jq .
```

**Expected Response:**
```json
{
  "title": "Finish project report by Friday",
  "description": "Complete and submit the project report",
  "priority": "HIGH"
}
```

### 3. Test Task Suggestions

```bash
curl -X GET https://your-backend.onrender.com/api/ai/suggestions \
  -H "Authorization: Bearer $TOKEN" | jq .
```

---

## 🆚 Local vs Production Comparison

| Aspect | Local (Ollama) | Production (Groq) |
|--------|----------------|-------------------|
| **Cost** | $0.00 | $0.00 |
| **Setup** | Install Ollama | Get API key |
| **Speed** | Depends on hardware | Very fast |
| **Privacy** | Complete (local) | Data sent to Groq |
| **Internet** | Not needed | Required |
| **Resource Usage** | High (4-13GB RAM) | None (cloud) |
| **Best For** | Development | Production |

---

## 🔒 Security Best Practices

### 1. Protect Your Groq API Key

✅ **DO:**
- Store in Render environment variables
- Never commit to Git
- Rotate periodically

❌ **DON'T:**
- Hardcode in source code
- Share publicly
- Commit to GitHub

### 2. Rate Limiting

Groq has built-in rate limits:
- 30 requests/minute per API key
- If exceeded, returns 429 error
- Your app handles this gracefully (fallback response)

### 3. Monitor Usage

Check usage at: https://console.groq.com/usage

---

## 🐛 Troubleshooting

### "AI service unavailable" on Render

**Check:**
1. Environment variable `GROQ_API_KEY` is set
2. API key is valid (test at console.groq.com)
3. Check Render logs for errors

**View Logs:**
```bash
# In Render dashboard
Logs tab → Look for "AI" or "Groq" mentions
```

### "Invalid API key" Error

**Problem:** Wrong or expired API key

**Solution:**
1. Generate new key at console.groq.com
2. Update `GROQ_API_KEY` on Render
3. Redeploy (Render does this automatically)

### AI Features Work Locally But Not on Render

**Check:**
1. `SPRING_PROFILES_ACTIVE=prod` is set on Render
2. `GROQ_API_KEY` environment variable exists
3. Backend logs show "Using production profile"

**Debug:**
```bash
# Check which profile is active
curl https://your-backend.onrender.com/actuator/env | jq .activeProfiles
```

### Rate Limit Exceeded (429 Error)

**Problem:** Too many AI requests

**Solution:**
- Wait 1 minute (limit resets)
- Implement caching for repeated queries
- Upgrade Groq plan (still has free tier)

---

## 💰 Cost Breakdown

### Groq Free Tier Comparison

| Usage | Requests/Day | Cost with OpenAI | Cost with Groq |
|-------|--------------|------------------|----------------|
| Light (10 users) | 100 | $0.02 | $0.00 |
| Medium (50 users) | 500 | $0.10 | $0.00 |
| Heavy (100 users) | 1,000 | $0.20 | $0.00 |
| Max Free Tier | 14,400 | $2.88 | $0.00 |

**Savings:** 100% - Everything is FREE!

---

## 🎓 Understanding the Setup

### Why Two Spring AI Dependencies?

```xml
<!-- For local development (Ollama) -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
</dependency>

<!-- For production (Groq via OpenAI-compatible API) -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
</dependency>
```

**Reason:** Spring profiles select the right one:
- `default` profile → Uses Ollama config
- `prod` profile → Uses Groq config

### How Groq Uses OpenAI Dependency

Groq provides an OpenAI-compatible API:
- Same request/response format
- Same SDK works
- Just different base URL

```properties
# Normal OpenAI
spring.ai.openai.base-url=https://api.openai.com/v1

# Groq (OpenAI-compatible)
spring.ai.openai.base-url=https://api.groq.com/openai/v1
```

---

## 📈 Monitoring & Optimization

### 1. Monitor Groq Usage

Dashboard: https://console.groq.com/usage

Track:
- Requests per day
- Tokens used
- Response times
- Error rates

### 2. Optimize for Performance

**Cache AI Responses:**
```java
// Add simple caching for repeated queries
private Map<String, String> cache = new HashMap<>();

public String parseTask(String text) {
    if (cache.containsKey(text)) {
        return cache.get(text);
    }
    String result = aiService.parse(text);
    cache.put(text, result);
    return result;
}
```

**Batch Similar Requests:**
- Group multiple task parsing calls
- Reduce API calls

**Use Appropriate Model:**
- `llama3-8b-8192` - Good balance (default)
- `gemma-7b-it` - Faster, lighter
- `mixtral-8x7b-32768` - Better quality

---

## 🚀 Deployment Checklist

### Before Deploying

- [x] Spring AI dependencies added (Ollama + OpenAI)
- [x] Production config set for Groq
- [x] Graceful fallbacks implemented
- [ ] Get Groq API key from console.groq.com
- [ ] Set `GROQ_API_KEY` on Render
- [ ] Set `SPRING_PROFILES_ACTIVE=prod` on Render
- [ ] Test locally with both Ollama and Groq

### After Deploying

- [ ] Check `/api/ai/status` endpoint
- [ ] Test natural language parsing
- [ ] Test task suggestions
- [ ] Monitor Groq usage dashboard
- [ ] Verify no errors in Render logs

---

## 🎉 You're Ready!

Your Task Tracker now has **FREE AI** in both development and production!

**Development:**
```bash
# Terminal 1: Start Ollama
ollama serve

# Terminal 2: Start Backend
./mvnw spring-boot:run

# AI runs locally - FREE!
```

**Production (Render):**
- Groq API handles all AI requests
- No server resources needed
- Completely FREE with generous limits
- Just set `GROQ_API_KEY` environment variable

---

## 📚 Additional Resources

- **Groq Console:** https://console.groq.com
- **Groq Docs:** https://console.groq.com/docs
- **Groq Models:** https://console.groq.com/docs/models
- **Spring AI Docs:** https://docs.spring.io/spring-ai/reference/
- **Render Docs:** https://render.com/docs

---

## 💡 Pro Tips

1. **Monitor Your Usage** - Check Groq dashboard weekly
2. **Use Caching** - Cache AI responses for common queries
3. **Set Timeouts** - Configure request timeouts (5-10 seconds)
4. **Handle Errors** - Always have fallback responses
5. **Test Before Deploy** - Test locally with both Ollama and Groq

---

## ✅ Summary

**Local Development:**
- Install Ollama: `curl -fsSL https://ollama.com/install.sh | sh`
- Start Ollama: `ollama serve`
- Pull model: `ollama pull llama2`
- Run backend: `./mvnw spring-boot:run`

**Production (Render):**
- Get Groq API key: https://console.groq.com
- Set `GROQ_API_KEY` on Render
- Set `SPRING_PROFILES_ACTIVE=prod`
- Deploy and enjoy FREE AI!

**Both are 100% FREE! 🎉**

