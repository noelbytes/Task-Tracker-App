# Pre-Interview Testing Checklist

## ✅ Complete This 10 Minutes Before Interview

### 1. Live Demo Testing

#### Frontend Tests
- [ ] Navigate to: https://tasktracker-frontend-z3dz.onrender.com/
- [ ] Page loads without errors
- [ ] Login form is visible
- [ ] Enter: Username: `demo`, Password: `demo123`
- [ ] Click "Login" button
- [ ] Successfully redirected to task list
- [ ] Tasks are displayed (if any exist)

#### Task Management Tests
- [ ] Click "Add Task" or "New Task" button
- [ ] Fill in task form:
  - Title: "Interview Test Task"
  - Description: "Testing before interview"
  - Status: "TODO"
  - Priority: "HIGH"
- [ ] Submit form
- [ ] New task appears in list
- [ ] Click task to edit
- [ ] Change status to "IN_PROGRESS"
- [ ] Save changes
- [ ] Verify task updated

#### Filter Tests
- [ ] Filter by status: "TODO"
- [ ] Only TODO tasks shown
- [ ] Filter by status: "IN_PROGRESS"
- [ ] Task you just created shows up
- [ ] Filter by status: "DONE"
- [ ] Completed tasks shown (if any)
- [ ] Filter by priority: "HIGH"
- [ ] Your test task appears
- [ ] Clear filters - all tasks show

#### Analytics Tests
- [ ] Navigate to Analytics page/section
- [ ] Charts are visible (pie chart and bar chart)
- [ ] Statistics show:
  - Total tasks count
  - Completed count
  - Pending count
  - Average completion time (if tasks completed)
- [ ] Charts render correctly (no errors)

#### Delete Test
- [ ] Find "Interview Test Task"
- [ ] Click delete button
- [ ] Confirm deletion
- [ ] Task removed from list

#### Logout Test (if implemented)
- [ ] Click logout (if button exists)
- [ ] Redirected to login page
- [ ] Can't access tasks without logging in again

---

### 2. API Documentation Testing

#### Swagger UI Tests
- [ ] Navigate to: https://task-tracker-app-1uok.onrender.com/swagger-ui.html
- [ ] Swagger UI loads correctly
- [ ] See all endpoints listed:
  - Auth Controller (login, register)
  - Task Controller (CRUD + stats)
- [ ] Click "Authorize" button
- [ ] Login to get JWT token:
  ```json
  {
    "username": "demo",
    "password": "demo123"
  }
  ```
- [ ] Copy token from response
- [ ] Click "Authorize", paste token
- [ ] Try "GET /api/tasks" endpoint
- [ ] Execute request
- [ ] See tasks in response (200 OK)
- [ ] Try "GET /api/tasks/stats"
- [ ] See statistics in response

---

### 3. Local Development Testing (Optional)

If you have time and want to show local setup:

```bash
# Terminal 1 - Backend
cd backend
mvn clean install
mvn spring-boot:run

# Terminal 2 - Frontend  
cd frontend
npm install
ng serve
```

- [ ] Backend starts on http://localhost:8080
- [ ] Frontend starts on http://localhost:4200
- [ ] Can login and use app locally

---

### 4. GitHub Repository Check

- [ ] Navigate to your GitHub repo
- [ ] README.md displays correctly
- [ ] All code is pushed (latest commits visible)
- [ ] No sensitive data exposed (passwords, secrets)
- [ ] License file present
- [ ] .gitignore working (no node_modules, target folders)

---

### 5. Documentation Review

- [ ] Open README.md
- [ ] Verify live demo links work
- [ ] Demo credentials are correct
- [ ] Setup instructions are clear
- [ ] Architecture diagram displays
- [ ] API endpoints listed correctly

---

### 6. Your Environment Setup

#### IDE Preparation
- [ ] Project open in IntelliJ IDEA / VS Code
- [ ] Can quickly navigate to:
  - `TaskController.java`
  - `TaskService.java` 
  - `Task.java` (model)
  - `SecurityConfig.java`
  - `JwtUtil.java`
  - Frontend: `task-list.component.ts`
  - Frontend: `auth.service.ts`
- [ ] Code is formatted and clean
- [ ] No obvious errors in IDE

#### Browser Setup
- [ ] Live demo tab open
- [ ] Swagger UI tab open
- [ ] GitHub repo tab open
- [ ] This checklist open
- [ ] Interview guide open
- [ ] Close unnecessary tabs

#### Video Call Setup
- [ ] Camera working
- [ ] Microphone working
- [ ] Headphones connected (avoid echo)
- [ ] Good lighting
- [ ] Clean background
- [ ] Close unnecessary applications
- [ ] Silence phone notifications
- [ ] "Do Not Disturb" mode on computer

---

### 7. Knowledge Check

Quick self-quiz (answer out loud):

- [ ] **What framework did you use for backend?** 
  → Spring Boot 3.2 with Java 17
  
- [ ] **What database are you using?**
  → PostgreSQL for production, H2 for development
  
- [ ] **How does authentication work?**
  → JWT tokens with BCrypt password hashing
  
- [ ] **What are the task statuses?**
  → TODO, IN_PROGRESS, DONE
  
- [ ] **What are the task priorities?**
  → LOW, MEDIUM, HIGH
  
- [ ] **What does the /stats endpoint return?**
  → Total tasks, completed count, pending count, average completion time
  
- [ ] **How is data secured between users?**
  → Service layer filters by getCurrentUser(), authorization checks
  
- [ ] **What charts did you use?**
  → Chart.js - Pie chart for status distribution, Bar chart for completed vs pending
  
- [ ] **How is the app deployed?**
  → Docker containers deployed on Render.com with GitHub CI/CD
  
- [ ] **Demo credentials?**
  → Username: demo, Password: demo123

---

### 8. Interview Materials Ready

Physical/Digital Items:
- [ ] Notebook and pen (for notes)
- [ ] Water nearby
- [ ] This preparation guide open
- [ ] Resume handy (in case they reference it)
- [ ] Questions you want to ask them written down

---

### 9. Mental Preparation

- [ ] Take 3 deep breaths
- [ ] Review your elevator pitch (30 seconds)
- [ ] Review top 10 critical answers
- [ ] Remember: You've built a complete, working application
- [ ] Confidence check: Rate yourself 1-10 (aim for 7+)

---

### 10. Final Verification (1 minute before)

Quick 60-second test:
1. Load demo site → (10 sec)
2. Login → (5 sec)
3. See tasks → (5 sec)
4. Navigate to analytics → (5 sec)
5. Charts visible → (5 sec)
6. Open Swagger → (10 sec)
7. See endpoints → (5 sec)
8. Deep breath → (15 sec)

---

## Emergency Backup Plan

### If Live Demo is Down:
1. **Stay calm** - mention Render free tier may have cold start
2. **Show Swagger** - might load faster
3. **Show local version** - if you have it running
4. **Show GitHub code** - walk through the code
5. **Show screenshots** - if you have any

### If You Freeze on a Question:
1. "That's a great question, let me think for a moment..."
2. "Could you rephrase that?"
3. "I haven't implemented that specifically, but here's how I would approach it..."
4. "Let me show you what I did implement that's related..."

---

## Demo Script (Practice This)

**When they say: "Show me your application"**

Say: "Sure! Let me walk you through it..."

1. **Navigate to demo:** "Here's the live application on Render..."
2. **Login:** "I'll login with the demo credentials..."
3. **Show list:** "This is the task list with filtering options..."
4. **Create task:** "Let me create a new task..."
5. **Show filters:** "I can filter by status or priority..."
6. **Show analytics:** "Here's the analytics dashboard with visualizations..."
7. **Show Swagger:** "For API testing, I have Swagger documentation..."
8. **Show code:** "Would you like to see the implementation?"

**Practice this 2-3 times before the interview!**

---

## Post-Test Actions

If everything works:
- ✅ Check this box: [ ] All systems ready!
- ✅ You're prepared!
- ✅ Join the call 2-3 minutes early
- ✅ Smile and be confident!

If something doesn't work:
- 🔧 Fix it now (you have time)
- 🔧 Prepare explanation ("Render free tier cold start")
- 🔧 Have backup plan ready

---

## Time Check

Current time: __________

Interview time: __________

Time remaining: __________

**If less than 10 minutes:** Focus on items 1, 2, 6, 7

**If less than 5 minutes:** Just test the live demo (item 1)

---

## Good Luck! 🍀

Remember:
- You built this
- You know this
- You've got this!

**Now go ace that interview! 🚀**

