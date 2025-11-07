# FREE AI with Ollama - Setup Guide

## 🎉 Good News - Completely FREE AI!

You don't need OpenAI or any paid API! I've switched your project to use **Ollama**, which is:
- ✅ **100% FREE** - No API keys, no costs
- ✅ **Runs Locally** - On your own machine
- ✅ **Private** - Your data never leaves your computer
- ✅ **No Internet Required** - Works offline once installed

---

## 🚀 Quick Setup (5 Minutes)

### Step 1: Install Ollama

#### Linux (Ubuntu/Debian):
```bash
curl -fsSL https://ollama.com/install.sh | sh
```

#### macOS:
```bash
# Download from: https://ollama.com/download
# Or use Homebrew:
brew install ollama
```

#### Windows:
1. Download installer from: https://ollama.com/download
2. Run the installer
3. Ollama will start automatically

### Step 2: Start Ollama Service

```bash
# Start Ollama (runs in background)
ollama serve
```

This starts a local server on `http://localhost:11434`

### Step 3: Download AI Model (llama2)

```bash
# Download the llama2 model (free, ~4GB)
ollama pull llama2
```

**Alternative models (all free):**
```bash
# Faster, smaller model (1.5GB)
ollama pull llama2:7b

# Better quality, larger model (13GB)
ollama pull llama2:13b

# Coding-focused model
ollama pull codellama

# Smaller, fastest model (1.1GB)
ollama pull mistral
```

### Step 4: Verify Installation

```bash
# Test Ollama is working
ollama run llama2 "Hello, how are you?"
```

You should see a response from the AI!

---

## 🏗️ Your Project Configuration

### Already Done ✅
I've updated your project to use Ollama:

**pom.xml:**
- ✅ Removed `spring-ai-openai-spring-boot-starter`
- ✅ Added `spring-ai-ollama-spring-boot-starter`

**application.properties:**
```properties
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama2
spring.ai.ollama.chat.options.temperature=0.7
```

**AIController.java:**
- ✅ Updated to show "Ollama (Free Local LLM)"
- ✅ Displays "$0.00 - Completely Free!"

---

## 🎯 How to Run

### 1. Start Ollama (keep this running)
```bash
ollama serve
```

### 2. Build and Run Backend
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

### 3. Test AI Features
```bash
# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}' | jq -r '.token')

# Test AI parsing
curl -X POST http://localhost:8080/api/ai/parse-task \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"text": "Finish project report by Friday - urgent!"}' | jq .

# Check AI status
curl -X GET http://localhost:8080/api/ai/status \
  -H "Authorization: Bearer $TOKEN" | jq .
```

---

## 💡 Available Models

### Recommended for Task Management:

| Model | Size | Speed | Quality | Command |
|-------|------|-------|---------|---------|
| **llama2** | 4GB | Medium | Good | `ollama pull llama2` |
| **mistral** | 4GB | Fast | Good | `ollama pull mistral` |
| **llama2:7b** | 1.5GB | Fast | Medium | `ollama pull llama2:7b` |
| **codellama** | 4GB | Medium | Best for code | `ollama pull codellama` |

### To Change Model:
Edit `application.properties`:
```properties
spring.ai.ollama.chat.options.model=mistral
```

Or use environment variable:
```bash
export OLLAMA_MODEL=mistral
```

---

## 🆚 Ollama vs OpenAI Comparison

### OpenAI (What you were using)
- ❌ Costs money (~$0.0002 per request)
- ❌ Requires API key
- ❌ Needs internet connection
- ❌ Data sent to external servers
- ✅ Very fast
- ✅ High quality responses

### Ollama (What you're using now)
- ✅ **100% FREE**
- ✅ No API key needed
- ✅ Works offline
- ✅ Data stays on your machine
- ✅ Privacy guaranteed
- ⚠️ Slightly slower (depending on hardware)
- ⚠️ Quality depends on model size

---

## 🔧 Troubleshooting

### "Connection refused" Error
**Problem:** Ollama service not running

**Solution:**
```bash
# Start Ollama service
ollama serve
```

### "Model not found" Error
**Problem:** Model not downloaded

**Solution:**
```bash
# Download the model
ollama pull llama2
```

### Slow Response Times
**Problem:** Large model on slow hardware

**Solution:** Use smaller model
```bash
ollama pull mistral
```
Update `application.properties`:
```properties
spring.ai.ollama.chat.options.model=mistral
```

### AI Features Not Working
**Check Ollama is running:**
```bash
curl http://localhost:11434/api/tags
```

Should return list of models.

---

## 📊 Performance Tips

### 1. Choose Right Model for Your Hardware

**Low RAM (8GB):**
```bash
ollama pull llama2:7b
```

**Medium RAM (16GB):**
```bash
ollama pull llama2
# or
ollama pull mistral
```

**High RAM (32GB+):**
```bash
ollama pull llama2:13b
```

### 2. GPU Acceleration (Optional)

Ollama automatically uses GPU if available:
- NVIDIA GPU (CUDA)
- AMD GPU (ROCm)
- Apple Silicon (Metal)

This makes responses much faster!

### 3. Adjust Temperature

**More Predictable (recommended for tasks):**
```properties
spring.ai.ollama.chat.options.temperature=0.3
```

**More Creative:**
```properties
spring.ai.ollama.chat.options.temperature=0.9
```

---

## 🌐 Production Deployment (Render)

### Option 1: Disable AI in Production (Simplest)
Just don't run Ollama on production. AI features will gracefully fallback.

### Option 2: Run Ollama on Render
Unfortunately, Render's free tier doesn't have enough resources for Ollama.

**Alternative:** Use a separate VPS for Ollama:
1. Get free VPS (Oracle Cloud has generous free tier)
2. Install Ollama on VPS
3. Set environment variable:
```
OLLAMA_BASE_URL=http://your-vps-ip:11434
```

### Option 3: Use Groq (Free API)
Groq offers free API with good limits:
```properties
# Switch to Groq if deploying to production
# Visit: https://console.groq.com for free API key
```

---

## 🎓 How Ollama Works

### Architecture
```
Your App (Spring Boot)
       ↓
Spring AI
       ↓
Ollama Server (localhost:11434)
       ↓
AI Model (llama2, mistral, etc.)
       ↓
Response back to your app
```

### Data Flow Example
```
1. User: "Buy milk tomorrow"
2. Your App → Spring AI → Ollama
3. Ollama processes with llama2 model
4. Returns: {"title": "Buy milk", "priority": "MEDIUM"}
5. Your App ← Response
```

---

## 🆓 Other Free Alternatives

If Ollama doesn't work for you, here are other free options:

### 1. Hugging Face Inference API
- Free tier: 30,000 characters/month
- https://huggingface.co/inference-api

### 2. Groq (Fast & Free)
- Free tier: 100 requests/day
- Very fast inference
- https://console.groq.com

### 3. Google Gemini
- Free tier available
- Good quality
- https://ai.google.dev

### 4. Together AI
- Free tier: $25 credit
- Multiple models
- https://together.ai

---

## 📝 Complete Example

### 1. Install Ollama
```bash
curl -fsSL https://ollama.com/install.sh | sh
```

### 2. Start Ollama
```bash
ollama serve
```

### 3. Download Model
```bash
ollama pull llama2
```

### 4. Test Ollama
```bash
ollama run llama2 "Parse this task: Buy groceries tomorrow"
```

### 5. Start Your App
```bash
cd backend
./mvnw spring-boot:run
```

### 6. Test AI Features
Visit: http://localhost:8080/swagger-ui.html
- Look for "AI Features" section
- Try `/api/ai/parse-task` endpoint

---

## ✅ Verification Checklist

- [ ] Ollama installed
- [ ] Ollama service running (`ollama serve`)
- [ ] llama2 model downloaded (`ollama pull llama2`)
- [ ] Backend builds successfully (`./mvnw clean install`)
- [ ] Backend starts without errors
- [ ] AI status endpoint returns available=true
- [ ] Can parse natural language tasks
- [ ] Can get task suggestions

---

## 🎉 You're All Set!

Your Task Tracker now has **FREE AI features** powered by Ollama!

**No API keys needed!**
**No costs!**
**Complete privacy!**

### Quick Start:
```bash
# Terminal 1: Start Ollama
ollama serve

# Terminal 2: Start Backend
cd backend
./mvnw spring-boot:run

# Terminal 3: Test
curl http://localhost:8080/api/ai/status
```

---

## 🚀 Next Steps

1. **Try different models** - `ollama pull mistral`
2. **Adjust temperature** - Edit application.properties
3. **Add frontend UI** - Show AI suggestions in Angular app
4. **Monitor performance** - Check response times
5. **Optimize prompts** - Improve AI accuracy

---

## 📚 Resources

- **Ollama Docs:** https://github.com/ollama/ollama
- **Available Models:** https://ollama.com/library
- **Spring AI Docs:** https://docs.spring.io/spring-ai/reference/
- **Ollama API:** https://github.com/ollama/ollama/blob/main/docs/api.md

---

## 💬 Support

If you run into issues:
1. Check Ollama is running: `curl http://localhost:11434`
2. Check model is downloaded: `ollama list`
3. Check application logs for errors
4. Try smaller model: `ollama pull mistral`

**Everything is FREE - no payment required! 🎉**

