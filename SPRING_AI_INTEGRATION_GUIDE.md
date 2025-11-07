# Spring AI Integration Guide

## 🤖 AI Features Implemented

Your Task Tracker now has AI-powered features using Spring AI and OpenAI!

### Features Added:
1. **Natural Language Task Creation** - Create tasks from plain text
2. **Smart Priority Recommendations** - AI suggests priority based on content
3. **Task Suggestions** - Get task ideas based on your history
4. **Productivity Insights** - AI-generated tips on your work patterns

---

## 🚀 Setup Instructions

### 1. Get OpenAI API Key

1. Go to https://platform.openai.com/api-keys
2. Create an account or sign in
3. Click "Create new secret key"
4. Copy the key (starts with `sk-...`)

### 2. Configure API Key

#### Local Development:
```bash
export OPENAI_API_KEY=sk-your-key-here
```

Or add to `.env` file:
```
OPENAI_API_KEY=sk-your-key-here
```

#### Render Production:
1. Go to your backend service on Render
2. Environment → Add environment variable
3. Key: `OPENAI_API_KEY`
4. Value: `sk-your-key-here`
5. Save changes and redeploy

### 3. Install Dependencies

```bash
cd backend
./mvnw clean install
```

Spring AI dependencies will be downloaded automatically.

---

## 📡 API Endpoints

All endpoints require JWT authentication.

### 1. Parse Natural Language Task
**POST** `/api/ai/parse-task`

Convert plain text to structured task fields.

**Request:**
```json
{
  "text": "Remind me to buy groceries tomorrow at 5pm - very important!"
}
```

**Response:**
```json
{
  "title": "Buy groceries tomorrow at 5pm",
  "description": "Reminder to purchase groceries",
  "priority": "HIGH"
}
```

**Usage Example:**
```bash
curl -X POST http://localhost:8080/api/ai/parse-task \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"text": "Finish project report by Friday"}'
```

---

### 2. Get Task Suggestions
**GET** `/api/ai/suggestions`

Get AI-generated task suggestions based on your history.

**Response:**
```json
{
  "suggestions": [
    "Review completed project deliverables",
    "Schedule follow-up meeting",
    "Update project documentation"
  ],
  "insight": "You've been completing tasks consistently! Consider tackling high-priority items first thing in the morning for best results."
}
```

**Usage Example:**
```bash
curl -X GET http://localhost:8080/api/ai/suggestions \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 3. Recommend Priority
**GET** `/api/ai/recommend-priority`

AI analyzes task text and recommends priority level.

**Query Parameters:**
- `title` (required): Task title
- `description` (optional): Task description

**Response:**
```json
{
  "recommendedPriority": "HIGH",
  "title": "Fix critical bug in production"
}
```

**Usage Example:**
```bash
curl -X GET "http://localhost:8080/api/ai/recommend-priority?title=Fix%20critical%20bug&description=Production%20issue%20affecting%20users" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 4. Get Productivity Insight
**GET** `/api/ai/productivity-insight`

Get personalized productivity advice based on your task patterns.

**Response:**
```json
{
  "insight": "Great job maintaining a completion rate of 85%! You tend to complete high-priority tasks 20% faster than medium-priority ones. Consider batching similar tasks together to improve efficiency."
}
```

**Usage Example:**
```bash
curl -X GET http://localhost:8080/api/ai/productivity-insight \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 5. Check AI Status
**GET** `/api/ai/status`

Verify if AI service is available and configured.

**Response:**
```json
{
  "available": true,
  "provider": "OpenAI",
  "model": "gpt-3.5-turbo"
}
```

---

## 🏗️ Architecture

### Component Diagram
```
Frontend (Angular)
       ↓
AIController
       ↓
AITaskService ← → OpenAI API (via Spring AI)
       ↓
TaskService (for context)
       ↓
Database
```

### Data Flow Example: Natural Language Task Creation

```
1. User types: "Buy milk tomorrow"
          ↓
2. Frontend sends: POST /api/ai/parse-task
          ↓
3. AIController receives request
          ↓
4. AITaskService.parseNaturalLanguageTask()
          ↓
5. Spring AI sends prompt to OpenAI
          ↓
6. OpenAI responds with structured JSON
          ↓
7. AIController returns parsed task
          ↓
8. Frontend populates task form with AI results
```

---

## 💡 Prompt Engineering

### Example Prompts Used

**Task Parsing:**
```
Parse the following task description into a structured format.
Input: "Remind me to buy groceries tomorrow"

Extract:
- title: Short task title (max 50 characters)
- description: Detailed description
- priority: LOW, MEDIUM, or HIGH based on urgency keywords

Return ONLY valid JSON in this format:
{"title": "...", "description": "...", "priority": "MEDIUM"}
```

**Priority Recommendation:**
```
Analyze this task and recommend a priority level.
Title: "Fix critical bug in production"
Description: "Users cannot login"

Consider:
- Urgency keywords (urgent, ASAP, critical, important)
- Time sensitivity (today, tomorrow, deadline)
- Exclamation marks or strong language

Respond with ONLY one word: LOW, MEDIUM, or HIGH
```

---

## ⚙️ Configuration

### application.properties
```properties
# OpenAI Configuration
spring.ai.openai.api-key=${OPENAI_API_KEY:your-api-key-here}
spring.ai.openai.chat.options.model=gpt-3.5-turbo
spring.ai.openai.chat.options.temperature=0.7
spring.ai.openai.chat.options.max-tokens=500
```

### Configuration Explained

**Model:** `gpt-3.5-turbo`
- Fast and cost-effective
- Good balance of capability and price
- Suitable for task management domain

**Temperature:** `0.7`
- Controls randomness (0.0 = deterministic, 1.0 = creative)
- 0.7 provides good variety while maintaining consistency
- Lower values (0.3-0.5) for more predictable responses

**Max Tokens:** `500`
- Limits response length
- Reduces cost per request
- Sufficient for task descriptions

---

## 💰 Cost Considerations

### OpenAI Pricing (as of 2025)
- **GPT-3.5-Turbo:** ~$0.001 per 1K tokens
- Average task parsing: ~200 tokens
- **Cost per request:** ~$0.0002 (0.02 cents)

### Estimated Usage Costs
| Feature | Tokens/Request | Cost/Request | 1000 Requests |
|---------|----------------|--------------|---------------|
| Parse Task | 200 | $0.0002 | $0.20 |
| Suggest Tasks | 300 | $0.0003 | $0.30 |
| Priority Recommendation | 150 | $0.0001 | $0.15 |
| Productivity Insight | 250 | $0.0002 | $0.25 |

**Total for 1000 mixed requests:** ~$0.90

### Cost Optimization Tips
1. **Cache AI responses** for identical inputs
2. **Set max_tokens** to minimum required
3. **Use cheaper models** for simple tasks
4. **Implement rate limiting** per user
5. **Fallback to rule-based** for simple operations

---

## 🛡️ Error Handling

### Graceful Degradation

AI features are optional - app works without them:

```java
try {
    // Try AI-powered feature
    String result = aiTaskService.parseNaturalLanguageTask(input);
    return result;
} catch (Exception e) {
    // Fallback to basic implementation
    return fallbackResponse;
}
```

### Error Scenarios

1. **API Key Missing** → Returns default values
2. **API Rate Limit** → Falls back to basic logic
3. **Network Timeout** → Returns cached/default response
4. **Invalid Response** → Uses rule-based parsing

---

## 🧪 Testing

### Manual Testing

#### 1. Test Natural Language Parsing
```bash
# Login first
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}' | jq -r '.token')

# Test AI parsing
curl -X POST http://localhost:8080/api/ai/parse-task \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"text": "Finish project report by Friday - urgent!"}' | jq .
```

#### 2. Test Task Suggestions
```bash
curl -X GET http://localhost:8080/api/ai/suggestions \
  -H "Authorization: Bearer $TOKEN" | jq .
```

#### 3. Test Priority Recommendation
```bash
curl -X GET "http://localhost:8080/api/ai/recommend-priority?title=Critical%20bug&description=Production%20down" \
  -H "Authorization: Bearer $TOKEN" | jq .
```

### Expected Responses

**Natural Language Parsing:**
- Title extracted and shortened if needed
- Description provides context
- Priority based on urgency keywords

**Task Suggestions:**
- Related to existing task patterns
- Logical follow-up tasks
- Domain-appropriate suggestions

**Priority Recommendation:**
- HIGH for urgent/critical keywords
- LOW for routine/later keywords
- MEDIUM as default

---

## 🎯 Use Cases

### 1. Quick Task Entry
**User:** "Meeting with client tomorrow at 2pm"
**AI Parses:**
```json
{
  "title": "Client meeting at 2pm",
  "description": "Scheduled meeting with client",
  "priority": "MEDIUM"
}
```

### 2. Smart Prioritization
**User:** Enters "Fix login bug - users can't access app!"
**AI Recommends:** HIGH priority
**Reason:** Critical keyword + exclamation + user impact

### 3. Task Patterns
**User History:**
- "Write blog post"
- "Edit blog post"
- "Publish blog post"

**AI Suggests:**
- "Promote blog post on social media"
- "Respond to blog comments"
- "Plan next blog topic"

### 4. Productivity Coaching
**User Stats:** 15 tasks, 12 completed, avg 2.5 hours
**AI Insight:** "Excellent completion rate of 80%! Your average task duration is 2.5 hours. Consider breaking larger tasks into smaller chunks for even better progress tracking."

---

## 🔧 Customization

### Adjusting AI Behavior

**More Creative Responses:**
```properties
spring.ai.openai.chat.options.temperature=0.9
```

**More Deterministic:**
```properties
spring.ai.openai.chat.options.temperature=0.3
```

**Longer Responses:**
```properties
spring.ai.openai.chat.options.max-tokens=1000
```

**Different Model:**
```properties
spring.ai.openai.chat.options.model=gpt-4
# Note: GPT-4 is more capable but 10-20x more expensive
```

### Adding Custom Prompts

Edit `AITaskService.java` methods to customize prompts:

```java
String prompt = String.format("""
    Your custom prompt here with context: %s
    
    Additional instructions...
    """, context);
```

---

## 📚 Spring AI Documentation

- **Official Docs:** https://docs.spring.io/spring-ai/reference/
- **OpenAI Integration:** https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html
- **Examples:** https://github.com/spring-projects/spring-ai-examples

---

## 🎓 Interview Points

### Technical Concepts
1. **Spring AI Framework** - Abstraction over multiple AI providers
2. **Prompt Engineering** - Crafting effective prompts for specific domains
3. **API Integration** - External service integration patterns
4. **Error Handling** - Graceful degradation strategies
5. **Cost Management** - Token limiting and caching

### Design Decisions
1. **Why GPT-3.5?** - Cost-effective for task management domain
2. **Why separate service layer?** - Testability and reusability
3. **Why graceful degradation?** - App works without AI
4. **Why token limits?** - Cost control
5. **Why async could help?** - Better UX for slower AI responses

### Future Enhancements
1. **Response Caching** - Redis cache for repeated queries
2. **User Preferences** - Custom AI behavior per user
3. **A/B Testing** - Compare AI vs non-AI task completion
4. **Analytics** - Track AI feature usage
5. **Fine-tuning** - Custom model for task domain

---

## ✅ Deployment Checklist

- [x] Spring AI dependencies added to pom.xml
- [x] OpenAI API key configured
- [x] AITaskService implemented
- [x] AIController with 5 endpoints created
- [x] Error handling and fallbacks implemented
- [x] Swagger documentation included
- [ ] Set OPENAI_API_KEY environment variable
- [ ] Test all AI endpoints
- [ ] Monitor API usage and costs
- [ ] Add frontend UI for AI features

---

## 🚀 Next Steps

1. **Get OpenAI API Key** from https://platform.openai.com
2. **Set environment variable** `OPENAI_API_KEY`
3. **Build and run:** `./mvnw spring-boot:run`
4. **Test endpoints** using Swagger UI at `/swagger-ui.html`
5. **Monitor costs** at https://platform.openai.com/usage

---

## 🎉 You're Ready!

Your Task Tracker now has intelligent AI features powered by Spring AI and OpenAI!

**Available AI Endpoints:**
- POST `/api/ai/parse-task` - Natural language parsing
- GET `/api/ai/suggestions` - Smart task suggestions
- GET `/api/ai/recommend-priority` - Priority recommendations
- GET `/api/ai/productivity-insight` - Productivity coaching
- GET `/api/ai/status` - AI service health check

**All endpoints documented in Swagger UI!**

