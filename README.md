# 📋 Task Tracker Web Application
A full-stack task management application built with Spring Boot (Backend) and Angular (Frontend). The app allows users to create, update, delete, and visualize tasks with comprehensive analytics.
![Task Tracker](https://img.shields.io/badge/Status-Complete-success)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![Angular](https://img.shields.io/badge/Angular-18-red)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
## 🚀 Features
### Backend (Spring Boot)
- ✅ RESTful API with Spring Boot 3.2
- ✅ JWT-based Authentication
- ✅ PostgreSQL Database (with H2 for development)
- ✅ Full CRUD operations for tasks
- ✅ Task statistics and analytics endpoint
- ✅ Task filtering by status and priority
- ✅ Automatic completion time tracking
- ✅ Spring Security configuration
- ✅ CORS configuration
### Frontend (Angular)
- ✅ Responsive Angular 18 application
- ✅ User authentication with JWT
- ✅ Task list with search and filtering
- ✅ Add/Edit task forms
- ✅ Analytics dashboard with Chart.js
- ✅ Modern UI with gradient designs
- ✅ Protected routes with auth guards
- ✅ HTTP interceptor for token management
### Task Features
- **Task Attributes**: ID, Title, Description, Status, Priority, Created At, Completed At
- **Status Options**: TODO, IN_PROGRESS, DONE
- **Priority Levels**: LOW, MEDIUM, HIGH
- **Analytics**: Total tasks, Completed vs Pending, Average completion time
## 📁 Project Structure
\`\`\`
Task-Tracker-App/
├── backend/                    # Spring Boot Backend
│   ├── src/
│   │   └── main/
│   │       ├── java/com/tasktracker/
│   │       │   ├── config/    # Security & CORS config
│   │       │   ├── controller/ # REST Controllers
│   │       │   ├── dto/       # Data Transfer Objects
│   │       │   ├── model/     # JPA Entities
│   │       │   ├── repository/ # Spring Data Repositories
│   │       │   ├── security/  # JWT & Security components
│   │       │   └── service/   # Business Logic
│   │       └── resources/
│   │           └── application.properties
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                   # Angular Frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/    # Angular Components
│   │   │   ├── guards/        # Auth Guards
│   │   │   ├── interceptors/  # HTTP Interceptors
│   │   │   ├── models/        # TypeScript Models
│   │   │   └── services/      # Angular Services
│   │   └── environments/      # Environment configs
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
├── docker-compose.yml          # Docker Compose configuration
├── render.yaml                 # Render.com deployment config
└── FREE_DEPLOYMENT_GUIDE.md    # Step-by-step deployment guide
│   ├── src/
│   │   └── app/
│   │       ├── components/    # UI Components
│   │       │   ├── login/
│   │       │   ├── task-list/
│   │       │   ├── task-form/
│   │       │   └── analytics/
│   │       ├── services/      # API Services
│   │       ├── models/        # TypeScript Models
│   │       ├── guards/        # Route Guards
│   │       └── interceptors/  # HTTP Interceptors
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
├── docker-compose.yml
└── README.md
\`\`\`
## 🛠️ Technology Stack
### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Security**: Spring Security + JWT (jjwt 0.12.3)
- **Database**: PostgreSQL 15 (Production), H2 (Development)
- **ORM**: Spring Data JPA
- **Build Tool**: Maven
### Frontend
- **Framework**: Angular 18
- **Language**: TypeScript
- **UI Components**: Custom CSS with gradient designs
- **Charts**: Chart.js + ng2-charts
- **HTTP Client**: Angular HttpClient
- **Routing**: Angular Router
### DevOps
- **Containerization**: Docker
- **Orchestration**: Docker Compose
- **Web Server**: Nginx (for Angular)
- **Database**: PostgreSQL
## 📋 Prerequisites
- Java 17 or higher
- Node.js 18+ and npm
- Docker and Docker Compose (for containerized deployment)
- Maven 3.9+
## 🚀 Setup Instructions
### Option 1: Running with Docker (Recommended)
1. **Clone the repository**
\`\`\`bash
git clone <repository-url>
cd Task-Tracker-App
\`\`\`
2. **Build and run with Docker Compose**
\`\`\`bash
docker-compose up --build
\`\`\`
3. **Access the application**
- Frontend: http://localhost
- Backend API: http://localhost:8080
- PostgreSQL: localhost:5432
### Option 2: Local Development Setup
#### Backend Setup
1. **Navigate to backend directory**
\`\`\`bash
cd backend
\`\`\`
2. **Build the project**
\`\`\`bash
mvn clean install
\`\`\`
3. **Run the application**
\`\`\`bash
mvn spring-boot:run
\`\`\`
The backend will start on \`http://localhost:8080\`
#### Frontend Setup
1. **Navigate to frontend directory**
\`\`\`bash
cd frontend
\`\`\`
2. **Install dependencies**
\`\`\`bash
npm install
\`\`\`
3. **Run the development server**
\`\`\`bash
npm start
\`\`\`
The frontend will start on \`http://localhost:4200\`
## 🔐 Demo Credentials
\`\`\`
Username: demo
Password: demo123
\`\`\`
## 📡 API Endpoints
### Authentication
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | \`/api/auth/login\` | User login | No |
| GET | \`/api/auth/test\` | Test endpoint | No |
### Tasks
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | \`/api/tasks\` | Get all tasks | Yes |
| GET | \`/api/tasks/{id}\` | Get task by ID | Yes |
| POST | \`/api/tasks\` | Create new task | Yes |
| PUT | \`/api/tasks/{id}\` | Update task | Yes |
| DELETE | \`/api/tasks/{id}\` | Delete task | Yes |
| GET | \`/api/tasks?status={status}\` | Filter by status | Yes |
| GET | \`/api/tasks?priority={priority}\` | Filter by priority | Yes |
| GET | \`/api/tasks/stats\` | Get task statistics | Yes |
### Request Examples
**Login Request**
\`\`\`json
POST /api/auth/login
{
  "username": "demo",
  "password": "demo123"
}
\`\`\`
**Create Task Request**
\`\`\`json
POST /api/tasks
Headers: Authorization: Bearer <token>
{
  "title": "Complete project documentation",
  "description": "Write comprehensive README",
  "status": "TODO",
  "priority": "HIGH"
}
\`\`\`
**Stats Response**
\`\`\`json
GET /api/tasks/stats
{
  "totalTasks": 10,
  "completedTasks": 5,
  "pendingTasks": 5,
  "averageCompletionTimeHours": 24.5,
  "todoTasks": 3,
  "inProgressTasks": 2
}
\`\`\`
## 🏗️ Architecture Summary
### Backend Architecture
\`\`\`
Client Request
    ↓
Security Filter (JWT)
    ↓
Controller Layer (REST API)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Database (PostgreSQL/H2)
\`\`\`
### Frontend Architecture
\`\`\`
User Interface (Components)
    ↓
Services (API Calls)
    ↓
HTTP Interceptor (JWT Token)
    ↓
Backend API
\`\`\`
### Security Flow
1. User logs in with credentials
2. Backend validates and returns JWT token
3. Token stored in localStorage
4. HTTP Interceptor adds token to all requests
5. Backend validates token on protected routes
## 🐳 Docker Deployment
The application uses a multi-container setup:
1. **PostgreSQL Container**: Database server
2. **Backend Container**: Spring Boot API
3. **Frontend Container**: Angular app with Nginx
### Docker Commands
\`\`\`bash
# Build and start all services
docker-compose up --build
# Start services in detached mode
docker-compose up -d
# Stop all services
docker-compose down
# View logs
docker-compose logs -f
# Rebuild specific service
docker-compose up --build backend
\`\`\`
## 🔐 Credentials
**Demo User**
- Username: \`demo\`
- Password: \`demo123\`
**Database (Docker)**
- Username: \`postgres\`
- Password: \`postgres\`
- Database: \`tasktracker\`
## 📊 Sample Data
The application automatically creates:
- 1 demo user
- 5 sample tasks with different statuses and priorities

## ☁️ Cloud Deployment (FREE)

### Deploy to Render.com (Recommended)

Deploy your entire stack for **$0/month** on Render:

**Quick Deploy Steps:**

1. **Push to GitHub**
   ```bash
   git add .
   git commit -m "Ready for deployment"
   git push origin main
   ```

2. **Create Render Account**
   - Sign up at https://render.com (free)
   - Connect your GitHub account

3. **Deploy Database**
   - New → PostgreSQL → Free tier
   - Note the connection details

4. **Deploy Backend**
   - New → Web Service → Connect repo
   - Root Directory: `backend`
   - Environment: Docker
   - Add environment variables (see below)

5. **Deploy Frontend**
   - New → Static Site → Connect repo
   - Root Directory: `frontend`
   - Build: `npm ci && npm run build`
   - Publish: `dist/frontend/browser`

**Required Environment Variables:**
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=<your-postgres-url>
DB_USERNAME=<your-db-username>
DB_PASSWORD=<your-db-password>
JWT_SECRET=<generate-secure-secret>
CORS_ORIGINS=<your-frontend-url>
```

**📚 Detailed Guides:**
- **QUICK_DEPLOY.md** - 5-minute deployment guide
- **FREE_DEPLOYMENT_GUIDE.md** - Complete guide with alternatives
- **prepare-deployment.sh** - Pre-deployment checklist

### Alternative Free Options
- **Railway.app** - $5 free credit/month
- **Vercel (Frontend)** + Render (Backend)
- **Fly.io** - Free tier available

See **FREE_DEPLOYMENT_GUIDE.md** for complete instructions!

## 📚 Documentation

- **API.md** - Complete API documentation
- **DEPLOYMENT.md** - Deployment options
- **FREE_DEPLOYMENT_GUIDE.md** - Free hosting step-by-step
- **QUICK_DEPLOY.md** - 5-minute deployment
- **DOCKER_GUIDE.md** - Docker usage guide
- **PROJECT_SUMMARY.md** - Project overview

## 🛠️ Tech Stack

**Backend:**
- Java 17
- Spring Boot 3.2
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL / H2
- Maven

**Frontend:**
- Angular 18
- TypeScript
- RxJS
- Chart.js (ng2-charts)
- Standalone Components

**DevOps:**
- Docker & Docker Compose
- Nginx (production)
- GitHub Actions ready

---

**Happy Task Tracking! 📋✨**

**Live Demo:** [Deploy yours for free!](https://render.com)
**Author:** Built with ❤️ using Spring Boot & Angular

