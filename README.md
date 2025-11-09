# Task Tracker Application

A full-stack task management application built with Spring Boot and Angular, featuring JWT authentication, CRUD operations, and an analytics dashboard.

[![Live Demo](https://img.shields.io/badge/demo-live-success)](https://tasktracker-frontend-z3dz.onrender.com/)

## Live Demo

- **Application:** https://tasktracker-frontend-z3dz.onrender.com/
- **API Documentation:** https://task-tracker-app-1uok.onrender.com/swagger-ui.html

### Demo Credentials

```
Username: demo
Password: demo123
```

---

## Features

### Authentication & Security
- JWT-based authentication
- Secure password hashing with BCrypt
- Protected routes with auth guards
- Session management

### Task Management
- Create, Read, Update, Delete tasks
- Task status tracking (TODO, IN_PROGRESS, DONE)
- Priority levels (LOW, MEDIUM, HIGH)
- Filter tasks by status and priority
- Automatic completion time tracking

### Analytics Dashboard
- Total task count
- Completed vs pending tasks
- Average completion time
- Visual charts with Chart.js
- Task distribution by status

### Performance & Caching
- In-memory caching with Caffeine
- Optimized task retrieval and statistics
- Configurable cache TTL and sizes
- Automatic cache invalidation on updates

### API Documentation
- Interactive Swagger UI
- OpenAPI 3.0 specification
- JWT authentication support in Swagger
- Try-it-out functionality

---

## Architecture

### Tech Stack

#### Backend
- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Database:** PostgreSQL 15 (Production) / H2 (Development)
- **Security:** Spring Security + JWT
- **Caching:** Caffeine (High-performance in-memory cache)
- **API Documentation:** Springdoc OpenAPI (Swagger)
- **Build Tool:** Maven

#### Frontend
- **Framework:** Angular 18
- **Language:** TypeScript
- **UI Components:** Custom CSS with gradients
- **Charts:** Chart.js
- **HTTP Client:** Angular HttpClient
- **Routing:** Angular Router

#### DevOps
- **Containerization:** Docker + Docker Compose
- **Deployment:** Render.com
- **Database:** PostgreSQL on Render
- **CI/CD:** GitHub → Render auto-deploy

### Architecture Diagram

```
┌──────────────────────────────────────┐
│         Frontend Layer               │
│  ┌────────────────────────────────┐  │
│  │       Angular 18 SPA           │  │
│  │  - TypeScript                  │  │
│  │  - RxJS Observables            │  │
│  │  - Chart.js Analytics          │  │
│  │  - Auth Guard & Interceptor    │  │
│  └────────────────────────────────┘  │
└──────────────┬───────────────────────┘
               │
               │ HTTP/REST API
               │ Authorization: Bearer JWT
               │ JSON Payload
               │
┌──────────────▼───────────────────────┐
│         Backend Layer                │
│  ┌────────────────────────────────┐  │
│  │      Spring Boot 3.2           │  │
│  │  - Spring Security + JWT       │  │
│  │  - Spring Data JPA             │  │
│  │  - REST Controllers            │  │
│  │  - Swagger/OpenAPI Docs        │  │
│  │  - Actuator Monitoring         │  │
│  └────────────────────────────────┘  │
│  ┌────────────────────────────────┐  │
│  │    Caffeine Cache Layer        │  │
│  │  - tasksByUser (45s TTL)       │  │
│  │  - taskById (60s TTL)          │  │
│  │  - taskStats (20s TTL)         │  │
│  │  - Auto-invalidation           │  │
│  └────────────────────────────────┘  │
└──────────────┬───────────────────────┘
               │
               │ JPA/Hibernate
               │ JDBC Connection Pool (HikariCP)
               │
┌──────────────▼───────────────────────┐
│       Persistence Layer              │
│  ┌────────────────────────────────┐  │
│  │      PostgreSQL 15             │  │
│  │  - users table                 │  │
│  │  - tasks table                 │  │
│  │  - Indexed queries             │  │
│  │  - Foreign key constraints     │  │
│  └────────────────────────────────┘  │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│         DevOps Layer                 │
│  - Docker Containerization           │
│  - Docker Compose (local dev)        │
│  - Render.com Deployment             │
│  - GitHub CI/CD                      │
└──────────────────────────────────────┘
```

### Data Flow

1. **User Authentication:**
   - User logs in via Angular → Backend validates credentials
   - Backend generates JWT token (24h expiration)
   - Frontend stores token in localStorage
   - All subsequent requests include JWT in Authorization header

2. **Task Operations (with Caching):**
   - **Cache Hit:** Request → Cache → Response (2-5ms)
   - **Cache Miss:** Request → Database → Cache Store → Response (50-100ms)
   - **Mutations:** Create/Update/Delete → Cache Invalidation → Database

3. **Security:**
   - JWT Filter validates token on every request
   - SecurityContext stores authenticated user
   - Service layer enforces user isolation
   - Database queries filtered by user_id

4. **Analytics:**
   - Stats calculation cached per user
   - Chart.js renders visualizations
   - Real-time updates on data changes

### Project Structure

```
Task-Tracker-App/
├── backend/                    # Spring Boot Backend
│   ├── src/main/java/com/tasktracker/
│   │   ├── config/            # Security, CORS, Swagger config
│   │   ├── controller/        # REST Controllers
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── model/            # JPA Entities (Task, User)
│   │   ├── repository/       # Spring Data Repositories
│   │   ├── security/         # JWT Filter, UserDetailsService
│   │   └── service/          # Business Logic
│   ├── src/main/resources/
│   │   ├── application.properties           # Dev config
│   │   └── application-prod.properties      # Production config
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                  # Angular Frontend
│   ├── src/app/
│   │   ├── components/       # UI Components
│   │   │   ├── login/
│   │   │   ├── task-list/
│   │   │   ├── task-form/
│   │   │   └── analytics/
│   │   ├── guards/           # Auth Guard
│   │   ├── interceptors/     # HTTP Interceptor
│   │   ├── models/           # TypeScript Models
│   │   ├── services/         # API Services
│   │   └── environments/     # Environment configs
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── angular.json
│   └── package.json
│
└── docker-compose.yml         # Local development setup
```

---

## API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/login` | User login | ❌ |
| GET | `/api/auth/test` | Test auth endpoint | ❌ |

### Tasks

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/tasks` | Get all tasks | ✅ |
| GET | `/api/tasks/{id}` | Get task by ID | ✅ |
| POST | `/api/tasks` | Create new task | ✅ |
| PUT | `/api/tasks/{id}` | Update task | ✅ |
| DELETE | `/api/tasks/{id}` | Delete task | ✅ |
| GET | `/api/tasks/stats` | Get task statistics | ✅ |

### Query Parameters

- `?status=TODO|IN_PROGRESS|DONE` - Filter by status
- `?priority=LOW|MEDIUM|HIGH` - Filter by priority

### Example Requests

#### Login
```bash
curl -X POST https://task-tracker-app-1uok.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}'
```

#### Get All Tasks
```bash
curl -X GET https://task-tracker-app-1uok.onrender.com/api/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Create Task
```bash
curl -X POST https://task-tracker-app-1uok.onrender.com/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Complete project",
    "description": "Finish the task tracker",
    "status": "TODO",
    "priority": "HIGH"
  }'
```

---

## Setup Instructions

### Prerequisites

- **Java 17+** (for backend)
- **Node.js 18+** (for frontend)
- **Docker & Docker Compose** (for containerized setup)
- **PostgreSQL 15** (if running without Docker)
- **Maven** (for backend build)

### Option 1: Run with Docker (Recommended)

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/Task-Tracker-App.git
cd Task-Tracker-App

# Start all services (PostgreSQL, Backend, Frontend)
docker-compose up -d

# Access the application
# Frontend: http://localhost
# Backend: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

**Login with:**
- Username: `demo`
- Password: `demo123`

### Option 2: Run Backend & Frontend Separately

#### Backend Setup

```bash
cd backend

# Development mode (H2 in-memory database)
./mvnw spring-boot:run

# Production mode (PostgreSQL required)
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://localhost:5432/tasktracker
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
./mvnw spring-boot:run
```

Backend runs on: http://localhost:8080

#### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Run development server
npm start
```

Frontend runs on: http://localhost:4200

### Option 3: Production Build

#### Backend
```bash
cd backend
./mvnw clean package
java -jar target/task-tracker-backend-1.0.0.jar
```

#### Frontend
```bash
cd frontend
npm run build
# Output in dist/frontend/browser/
```

---

## Database Schema

### User Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
```

### Task Table
```sql
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    user_id BIGINT REFERENCES users(id)
);
```

---

## Environment Variables

### Backend (Production)

```env
# Application
SPRING_PROFILES_ACTIVE=prod

# Database
DATABASE_URL=jdbc:postgresql://host:5432/database
DB_USERNAME=username
DB_PASSWORD=password

# JWT
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000

# CORS
CORS_ORIGINS=https://your-frontend-url.com
```

### Frontend (Build Time)

Environment files are configured in `src/environments/`:
- `environment.ts` - Development (localhost:8080)
- `environment.prod.ts` - Production (Render backend URL)

---

## Testing

### Using Swagger UI

1. Visit: http://localhost:8080/swagger-ui.html
2. Click **"POST /api/auth/login"**
3. Try it out with:
   ```json
   {
     "username": "demo",
     "password": "demo123"
   }
   ```
4. Copy the JWT token from response
5. Click **"Authorize"** button (🔒 icon)
6. Paste token and click **"Authorize"**
7. Test all protected endpoints!

### Manual Testing

```bash
# Login and save token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}' \
  | jq -r '.token')

# Get all tasks
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/tasks
```

---

## Deployment

### Deploy to Render.com

#### 1. Create PostgreSQL Database
- Dashboard → New → PostgreSQL
- Note the connection details

#### 2. Deploy Backend
- Dashboard → New → Web Service
- Connect GitHub repository
- Settings:
  - **Root Directory:** `backend`
  - **Environment:** Docker
  - **Add Environment Variables:**
    ```
    SPRING_PROFILES_ACTIVE=prod
    DATABASE_URL=jdbc:postgresql://...
    DB_USERNAME=...
    DB_PASSWORD=...
    JWT_SECRET=...
    CORS_ORIGINS=https://your-frontend.onrender.com
    ```

#### 3. Deploy Frontend
- Dashboard → New → Static Site
- Settings:
  - **Root Directory:** `frontend`
  - **Build Command:** `npm ci && npm run build -- --configuration production`
  - **Publish Directory:** `dist/frontend/browser`

#### 4. Update CORS
- Update backend `CORS_ORIGINS` with actual frontend URL
- Redeploy backend

---

## Additional Documentation

- **[API Documentation](https://task-tracker-app-1uok.onrender.com/swagger-ui.html)** - Interactive API explorer
- **[Swagger Guide](SWAGGER_GUIDE.md)** - How to use Swagger UI
- **[Docker Guide](DOCKER_GUIDE.md)** - Docker setup details
- **[IntelliJ Guide](INTELLIJ_GUIDE.md)** - Run in IntelliJ IDEA

---

## Development

### Backend Technologies
- Spring Boot 3.2.0
- Spring Security 6.1.1
- Spring Data JPA
- PostgreSQL Driver 42.6.0
- H2 Database (dev)
- JWT (io.jsonwebtoken 0.12.3)
- Caffeine Cache 3.1.8
- Lombok
- Springdoc OpenAPI 2.3.0
- Spring Boot Actuator

### Frontend Technologies
- Angular 18
- TypeScript 5.4+
- RxJS 7.8
- Chart.js 4.4
- Angular Router
- Angular Forms

---


## License

This project is licensed under the MIT License.

---


## Acknowledgments

- Spring Boot Documentation
- Angular Documentation
- Render.com for free hosting
- Chart.js for beautiful charts

---

**Built using Spring Boot and Angular**

