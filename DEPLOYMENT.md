# 🚀 Deployment Guide - Task Tracker Application

This guide provides detailed instructions for deploying the Task Tracker application to various platforms.

## 📋 Table of Contents
- [Local Development](#local-development)
- [Docker Deployment](#docker-deployment)
- [Cloud Deployment Options](#cloud-deployment-options)
- [Environment Variables](#environment-variables)

## 🏠 Local Development

### Prerequisites
- Java 17+
- Maven 3.9+
- Node.js 18+
- npm

### Quick Start

1. **Run the setup script**
```bash
./setup-dev.sh
```

2. **Start Backend (Terminal 1)**
```bash
cd backend
mvn spring-boot:run
```

3. **Start Frontend (Terminal 2)**
```bash
cd frontend
npm start
```

4. **Access the application**
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:tasktracker)

## 🐳 Docker Deployment

### Prerequisites
- Docker 20+
- Docker Compose 2+

### Quick Start with Docker

```bash
# Build and start all services
docker-compose up --build

# Run in detached mode
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Access Points
- Frontend: http://localhost
- Backend API: http://localhost:8080
- PostgreSQL: localhost:5432

### Docker Services
- **postgres**: PostgreSQL 15 database
- **backend**: Spring Boot API
- **frontend**: Angular app with Nginx

## ☁️ Cloud Deployment Options

### Option 1: Deploy to Render

#### Backend (Spring Boot)

1. **Create New Web Service on Render**
   - Connect your GitHub repository
   - Choose "Web Service"
   - Build Command: `cd backend && mvn clean package -DskipTests`
   - Start Command: `java -jar backend/target/*.jar`
   - Environment: `Docker` or `Java`

2. **Add Environment Variables**
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=<your-postgres-url>
DB_USERNAME=<your-db-username>
DB_PASSWORD=<your-db-password>
JWT_SECRET=<generate-random-secret>
CORS_ORIGINS=<your-frontend-url>
```

3. **Create PostgreSQL Database**
   - Add PostgreSQL database on Render
   - Copy the Internal Database URL to `DATABASE_URL`

#### Frontend (Angular)

1. **Build for Production**
```bash
cd frontend
npm run build
```

2. **Deploy to Vercel/Netlify**
   - Connect GitHub repository
   - Build Command: `cd frontend && npm install && npm run build`
   - Publish Directory: `frontend/dist/frontend/browser`
   - Add Environment Variable: `API_URL=<your-backend-url>`

### Option 2: Deploy to Railway

1. **Create New Project**
   - Connect GitHub repository
   - Railway will auto-detect Spring Boot and Angular

2. **Backend Setup**
   - Add PostgreSQL service
   - Set environment variables
   - Deploy backend service

3. **Frontend Setup**
   - Configure build command
   - Set API URL environment variable
   - Deploy frontend service

### Option 3: Deploy to Azure Web App

#### Backend

1. **Create Azure Web App**
```bash
az webapp create \
  --resource-group <resource-group> \
  --plan <app-service-plan> \
  --name <app-name> \
  --runtime "JAVA:17-java17"
```

2. **Configure Environment Variables**
```bash
az webapp config appsettings set \
  --resource-group <resource-group> \
  --name <app-name> \
  --settings \
    SPRING_PROFILES_ACTIVE=prod \
    DATABASE_URL=<postgres-url> \
    JWT_SECRET=<secret>
```

3. **Deploy**
```bash
cd backend
mvn clean package -DskipTests
az webapp deploy \
  --resource-group <resource-group> \
  --name <app-name> \
  --src-path target/*.jar
```

#### Frontend

1. **Build Production Bundle**
```bash
cd frontend
npm run build
```

2. **Deploy to Azure Static Web Apps**
```bash
az staticwebapp create \
  --name <app-name> \
  --resource-group <resource-group> \
  --source <github-repo-url> \
  --location "centralus" \
  --branch main \
  --app-location "frontend" \
  --output-location "dist/frontend/browser"
```

### Option 4: Deploy with Docker to Any VPS

1. **Setup VPS (Ubuntu/Debian)**
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Install Docker Compose
sudo apt-get install docker-compose-plugin
```

2. **Clone Repository**
```bash
git clone <your-repo-url>
cd Task-Tracker-App
```

3. **Configure Environment**
```bash
# Edit docker-compose.yml with your settings
nano docker-compose.yml
```

4. **Deploy**
```bash
docker-compose up -d
```

5. **Setup Nginx Reverse Proxy (Optional)**
```nginx
server {
    listen 80;
    server_name yourdomain.com;

    location / {
        proxy_pass http://localhost:80;
    }

    location /api {
        proxy_pass http://localhost:8080;
    }
}
```

## 🔐 Environment Variables

### Backend Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SPRING_PROFILES_ACTIVE` | Active profile (dev/prod) | dev | No |
| `DATABASE_URL` | PostgreSQL connection URL | jdbc:h2:mem:tasktracker | Prod |
| `DB_USERNAME` | Database username | sa | Prod |
| `DB_PASSWORD` | Database password | - | Prod |
| `JWT_SECRET` | JWT signing secret | (default) | Yes |
| `JWT_EXPIRATION` | Token expiration (ms) | 86400000 | No |
| `CORS_ORIGINS` | Allowed CORS origins | localhost:4200 | Yes |

### Frontend Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `API_URL` | Backend API URL | http://localhost:8080/api | Yes |

## 📊 Database Setup

### PostgreSQL Connection String Format
```
DATABASE_URL=jdbc:postgresql://<host>:<port>/<database>?sslmode=require
```

### Example Connection Strings

**Local Development**
```
jdbc:h2:mem:tasktracker
```

**Render PostgreSQL**
```
jdbc:postgresql://dpg-xxx.oregon-postgres.render.com:5432/tasktracker_db
```

**Railway PostgreSQL**
```
jdbc:postgresql://containers-us-west-xxx.railway.app:5432/railway
```

## 🔒 Security Best Practices

1. **Generate Strong JWT Secret**
```bash
openssl rand -base64 32
```

2. **Use Environment Variables**
   - Never commit secrets to Git
   - Use platform-specific secret management

3. **Enable HTTPS**
   - Use Let's Encrypt for free SSL
   - Configure reverse proxy with SSL

4. **Database Security**
   - Use strong passwords
   - Enable SSL connections
   - Restrict network access

## 🧪 Testing Deployment

### Test Backend API
```bash
# Health check
curl http://your-backend-url/api/auth/test

# Login test
curl -X POST http://your-backend-url/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}'
```

### Test Frontend
- Visit frontend URL
- Login with demo credentials
- Create a test task
- Check analytics page

## 📝 Monitoring

### Application Logs

**Docker**
```bash
docker-compose logs -f backend
docker-compose logs -f frontend
```

**Render**
- View logs in Render dashboard

**Azure**
```bash
az webapp log tail --name <app-name> --resource-group <resource-group>
```

## 🆘 Troubleshooting

### Common Issues

**CORS Errors**
- Check `CORS_ORIGINS` environment variable
- Ensure frontend URL is in allowed origins

**Database Connection Failed**
- Verify DATABASE_URL format
- Check database credentials
- Ensure database is running

**JWT Token Issues**
- Check JWT_SECRET is set
- Verify token expiration settings
- Clear browser localStorage

**Build Failures**
- Check Java/Node versions
- Clear build caches
- Verify dependencies

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Angular Documentation](https://angular.io/docs)
- [Docker Documentation](https://docs.docker.com/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

**Need Help?** Open an issue on GitHub or check the main README.md

