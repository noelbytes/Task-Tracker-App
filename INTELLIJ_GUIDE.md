# 🚀 Running Task Tracker in IntelliJ IDEA

This guide will help you run both the Spring Boot backend and Angular frontend from IntelliJ IDEA.

---

## 📋 Prerequisites

### Required Software
1. **IntelliJ IDEA Ultimate** (recommended for full support) or **IntelliJ IDEA Community Edition** with plugins
2. **Java 17 or higher**
3. **Node.js 18+** and **npm**
4. **Maven** (usually bundled with IntelliJ)

### Required IntelliJ Plugins
- **Spring Boot** (for backend)
- **Angular and Angular CLI** (for frontend)
- **JavaScript and TypeScript** (usually pre-installed)

To install plugins:
1. Go to `File` → `Settings` (Windows/Linux) or `IntelliJ IDEA` → `Preferences` (macOS)
2. Navigate to `Plugins`
3. Search and install the required plugins
4. Restart IntelliJ IDEA

---

## 🔧 Project Setup in IntelliJ IDEA

### Step 1: Open the Project

1. **Open IntelliJ IDEA**
2. Click `File` → `Open`
3. Navigate to `/home/noelbytes/Projects/Task-Tracker-App`
4. Select the folder and click `OK`
5. IntelliJ will detect both the Maven (backend) and npm (frontend) projects

### Step 2: Import Project Structure

When IntelliJ opens the project:
1. It will show a popup to import Maven project - **Click "Import"**
2. It will detect package.json in frontend folder - **Enable JavaScript/TypeScript support**
3. Wait for IntelliJ to index the project (bottom right progress bar)

---

## 🎯 Running the Backend (Spring Boot)

### Method 1: Using Spring Boot Run Configuration (Easiest)

1. **Locate the Main Class**
   - Navigate to `backend/src/main/java/com/tasktracker/TaskTrackerApplication.java`
   - You should see a green ▶️ play icon in the gutter next to the class

2. **Run the Application**
   - Click the green ▶️ icon next to `public class TaskTrackerApplication`
   - Select `Run 'TaskTrackerApplication.main()'`
   - Or right-click the file → `Run 'TaskTrackerApplication.main()'`

3. **Verify Backend is Running**
   - Check the console output for: `Started TaskTrackerApplication`
   - Backend will start on `http://localhost:8080`
   - Test: Open browser to `http://localhost:8080/api/auth/test`

### Method 2: Using Maven Run Configuration

1. **Open Maven Tool Window**
   - Click `View` → `Tool Windows` → `Maven`
   - Or click the `Maven` tab on the right side

2. **Run Maven Goal**
   - Expand `task-tracker-backend` → `Plugins` → `spring-boot`
   - Double-click `spring-boot:run`

3. **Alternative: Create Custom Run Configuration**
   - Click `Run` → `Edit Configurations`
   - Click `+` → `Maven`
   - Name: `Backend - Spring Boot`
   - Working directory: `$PROJECT_DIR$/backend`
   - Command line: `spring-boot:run`
   - Click `Apply` and `OK`
   - Now you can run it from the Run menu

### Method 3: Using Terminal in IntelliJ

1. **Open Terminal**
   - Click `View` → `Tool Windows` → `Terminal`
   - Or press `Alt+F12` (Windows/Linux) or `Option+F12` (macOS)

2. **Run Backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

### Backend Environment Variables (Optional)

If you need to set environment variables:
1. Right-click on `TaskTrackerApplication.java`
2. Select `Modify Run Configuration...`
3. Expand `Environment` section
4. Add environment variables:
   ```
   SPRING_PROFILES_ACTIVE=dev
   ```
5. Click `Apply` and `OK`

---

## 🎨 Running the Frontend (Angular)

### Method 1: Using npm Scripts (Recommended)

1. **Open package.json**
   - Navigate to `frontend/package.json`

2. **Run npm install (First time only)**
   - Right-click on `package.json`
   - Select `Show npm Scripts`
   - Double-click `install` (or run in terminal: `cd frontend && npm install`)

3. **Start Angular Development Server**
   - In the npm scripts panel, double-click `start`
   - Or right-click `package.json` → `Show npm Scripts` → double-click `start`

4. **Verify Frontend is Running**
   - Frontend will start on `http://localhost:4200`
   - Browser should automatically open
   - If not, manually open `http://localhost:4200`

### Method 2: Using Terminal in IntelliJ

1. **Open New Terminal Tab**
   - In Terminal window, click `+` to open a new tab
   - Or press `Ctrl+Shift+T` (Windows/Linux) or `Cmd+Shift+T` (macOS)

2. **Navigate and Start**
   ```bash
   cd frontend
   npm install  # First time only
   npm start
   ```

3. **Alternative Commands**
   ```bash
   # Or use ng directly (if you have Angular CLI installed globally)
   cd frontend
   ng serve
   
   # With specific port
   ng serve --port 4200
   ```

### Method 3: Using Run Configuration

1. **Create npm Run Configuration**
   - Click `Run` → `Edit Configurations`
   - Click `+` → `npm`
   - Name: `Frontend - Angular Dev Server`
   - Package.json: Select `frontend/package.json`
   - Command: `run`
   - Scripts: `start`
   - Click `Apply` and `OK`

2. **Run the Configuration**
   - Select `Frontend - Angular Dev Server` from the dropdown at the top
   - Click the green ▶️ play button

---

## 🔄 Running Both Frontend and Backend Together

### Method 1: Using Compound Run Configuration (Best Way)

1. **Create Compound Configuration**
   - Click `Run` → `Edit Configurations`
   - Click `+` → `Compound`
   - Name: `Full Stack - Task Tracker`

2. **Add Configurations**
   - Click `+` under "Store as project file"
   - Add `TaskTrackerApplication` (backend)
   - Add `Frontend - Angular Dev Server` (frontend)
   - Click `Apply` and `OK`

3. **Run Everything**
   - Select `Full Stack - Task Tracker` from dropdown
   - Click green ▶️ play button
   - Both backend and frontend will start together!

### Method 2: Using Multiple Terminal Tabs

1. **Terminal 1 - Backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **Terminal 2 - Frontend** (Open new terminal tab with `+`)
   ```bash
   cd frontend
   npm start
   ```

### Method 3: Using Services Tool Window

1. **Enable Services Tool Window**
   - Click `View` → `Tool Windows` → `Services`
   - Or press `Alt+8` (Windows/Linux) or `Cmd+8` (macOS)

2. **Add Spring Boot Service**
   - Services will automatically detect your Spring Boot application
   - You can start/stop/restart from here

---

## 🐛 Debugging

### Debug Backend

1. **Set Breakpoints**
   - Click in the gutter (left margin) next to line numbers to set breakpoints
   - A red dot will appear

2. **Start Debug Mode**
   - Click the bug icon 🐛 next to `TaskTrackerApplication`
   - Or right-click → `Debug 'TaskTrackerApplication.main()'`
   - Or use `Shift+F9`

3. **Debug Controls**
   - Step Over: `F8`
   - Step Into: `F7`
   - Resume: `F9`
   - Evaluate Expression: `Alt+F8`

### Debug Frontend

1. **Using Chrome DevTools**
   - Open Chrome Developer Tools (`F12`)
   - Go to Sources tab
   - Find TypeScript files and set breakpoints

2. **Using IntelliJ Debugger (Ultimate Edition)**
   - Install `JavaScript Debugger` plugin (usually pre-installed)
   - Create JavaScript Debug configuration
   - Set URL: `http://localhost:4200`
   - Set breakpoints in TypeScript files
   - Click debug icon

---

## 📊 Monitoring Running Applications

### Check Running Processes

**In IntelliJ:**
1. Look at the `Run` tool window (bottom)
2. You'll see tabs for each running process
3. Click tabs to switch between console outputs

**Check Ports:**
- Backend: `http://localhost:8080`
- Frontend: `http://localhost:4200`
- H2 Console: `http://localhost:8080/h2-console`

### Stop Applications

**Stop Single Application:**
- Click the red stop button ⏹️ in the Run tool window
- Or press `Ctrl+F2` (Windows/Linux) or `Cmd+F2` (macOS)

**Stop All:**
- Click `Run` → `Stop All`
- Or press `Ctrl+Shift+F2` (Windows/Linux) or `Cmd+Shift+F2` (macOS)

---

## 🔧 Troubleshooting

### Backend Issues

**Issue: Port 8080 already in use**
```bash
# Find and kill process on port 8080
# Linux/Mac:
lsof -ti:8080 | xargs kill -9

# Windows (PowerShell):
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process
```

**Issue: Maven dependencies not resolved**
1. Right-click on `backend/pom.xml`
2. Select `Maven` → `Reload Project`
3. Or open Maven tool window → click Refresh icon

**Issue: Java version mismatch**
1. `File` → `Project Structure` → `Project`
2. Set `Project SDK` to Java 17 or higher
3. Set `Project language level` to 17

### Frontend Issues

**Issue: Port 4200 already in use**
```bash
# Kill process on port 4200
# Linux/Mac:
lsof -ti:4200 | xargs kill -9

# Windows (PowerShell):
Get-Process -Id (Get-NetTCPConnection -LocalPort 4200).OwningProcess | Stop-Process
```

**Issue: node_modules not installed**
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

**Issue: Angular CLI not found**
```bash
# Install Angular CLI globally
npm install -g @angular/cli

# Or use npx
cd frontend
npx ng serve
```

### Database Issues

**Issue: Cannot connect to database**
- Check if using H2 (in-memory) for development
- H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:tasktracker`
- Username: `sa`
- Password: (leave empty)

---

## ⚡ Quick Reference Commands

### Backend Commands
```bash
# Navigate to backend
cd backend

# Install dependencies
mvn clean install

# Run application
mvn spring-boot:run

# Build JAR
mvn clean package

# Skip tests
mvn clean install -DskipTests
```

### Frontend Commands
```bash
# Navigate to frontend
cd frontend

# Install dependencies
npm install

# Start dev server
npm start

# Build for production
npm run build

# Run tests
npm test

# Lint code
ng lint
```

---

## 🎯 Recommended Workflow

### Daily Development

1. **Start IntelliJ IDEA**
2. **Open Project** (`/home/noelbytes/Projects/Task-Tracker-App`)
3. **Start Backend**:
   - Click green ▶️ next to `TaskTrackerApplication.java`
   - Wait for "Started TaskTrackerApplication" in console
4. **Start Frontend**:
   - Open Terminal → `cd frontend` → `npm start`
   - Or use npm scripts in package.json
5. **Open Browser**: `http://localhost:4200`
6. **Login**: username: `demo`, password: `demo123`
7. **Code** → **Test** → **Repeat** 🔄

### Recommended IntelliJ Settings

**Enable Auto-Import:**
1. `File` → `Settings` → `Editor` → `General` → `Auto Import`
2. Enable `Add unambiguous imports on the fly`
3. Enable `Optimize imports on the fly`

**Enable Auto-Save:**
1. `File` → `Settings` → `Appearance & Behavior` → `System Settings`
2. Enable `Save files automatically`

**Format Code on Save:**
1. `File` → `Settings` → `Tools` → `Actions on Save`
2. Enable `Reformat code`
3. Enable `Optimize imports`

---

## 📝 Additional Tips

### Keyboard Shortcuts

**Running:**
- Run: `Shift+F10`
- Debug: `Shift+F9`
- Stop: `Ctrl+F2`

**Navigation:**
- Search Everywhere: `Double Shift`
- Find Class: `Ctrl+N`
- Find File: `Ctrl+Shift+N`
- Recent Files: `Ctrl+E`

**Editing:**
- Reformat Code: `Ctrl+Alt+L`
- Optimize Imports: `Ctrl+Alt+O`
- Show Intention Actions: `Alt+Enter`

### Live Reload

**Backend (Spring Boot DevTools):**
- Changes to Java files require rebuild: `Ctrl+F9`
- IntelliJ will automatically reload the application

**Frontend (Angular):**
- Changes are automatically detected and reloaded
- Browser will refresh automatically

---

## ✅ Verification Steps

After starting both applications:

1. **Backend Health Check**:
   - Open: `http://localhost:8080/api/auth/test`
   - Should show: "Auth endpoint is working!"

2. **H2 Console** (optional):
   - Open: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:tasktracker`
   - Username: `sa`, Password: (empty)

3. **Frontend Check**:
   - Open: `http://localhost:4200`
   - Should show login page

4. **Full Integration Test**:
   - Login with: `demo` / `demo123`
   - Create a new task
   - View analytics
   - Logout

---

## 🎉 Success!

If you see:
- ✅ Backend running on `http://localhost:8080`
- ✅ Frontend running on `http://localhost:4200`
- ✅ Can login with demo credentials
- ✅ Can create and manage tasks

**You're all set! Happy coding! 🚀**

---

For more help, check:
- **README.md** - Main documentation
- **API.md** - API reference
- **DEPLOYMENT.md** - Deployment guide

