# 🐳 Docker Quick Start Guide - Task Tracker App

## ✅ What I Fixed

I've successfully fixed several issues to get your app running with Docker:

1. **JWT Authentication Code** - Updated `JwtUtil.java` to work with jjwt 0.12.3 API
2. **Frontend Build Configuration** - Added missing `index` and `outputPath` to `angular.json`
3. **CSS/HTML Issue** - Removed HTML code that was accidentally in the CSS file
4. **Docker Build** - Configured Dockerfile to copy from correct Angular output directory (`/browser`)

The app is now fully working with Docker!

## 🚀 How to Run the App with Docker

### Prerequisites
- Docker installed ✅
- Docker Compose installed ✅
- Port 80 available (stop Apache if running: `sudo systemctl stop apache2`)

### Quick Start Commands

**1. Build and start all services (PostgreSQL, Backend, Frontend):**
```bash
cd /home/noelbytes/Projects/Task-Tracker-App
sudo docker compose up --build -d
```

**2. Check if containers are running:**
```bash
sudo docker compose ps
```

You should see:
- `tasktracker-postgres` - Database (port 5432)
- `tasktracker-backend` - Spring Boot API (port 8080)
- `tasktracker-frontend` - Angular app with Nginx (port 80)

**3. View logs:**
```bash
# All services
sudo docker compose logs -f

# Specific service
sudo docker compose logs -f backend
sudo docker compose logs -f frontend
sudo docker compose logs -f postgres
```

**4. Stop all services:**
```bash
sudo docker compose down
```

**5. Stop and remove all data (including database):**
```bash
sudo docker compose down -v
```

## 🌐 Access the Application

Once running, access:
- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080
- **PostgreSQL**: localhost:5432

## 👤 Demo Login Credentials

The app automatically creates a demo user:
- **Username**: `demo`
- **Password**: `demo123`

## 🔧 Troubleshooting

### Port 80 already in use
If you see "address already in use" for port 80:
```bash
# Stop Apache or other web server
sudo systemctl stop apache2
sudo systemctl stop httpd

# Or check what's using port 80
sudo lsof -i :80

# Then restart containers
sudo docker compose up -d
```

### Permission denied (Docker socket)
If you see permission denied errors:
```bash
# Add your user to docker group
sudo usermod -aG docker $USER

# Then log out and back in, or use:
newgrp docker

# Or just use sudo
sudo docker compose up -d
```

### View backend logs
```bash
sudo docker compose logs backend
```

### Rebuild after code changes
```bash
sudo docker compose up --build -d
```

### Reset everything
```bash
# Stop and remove containers, networks, volumes
sudo docker compose down -v

# Rebuild from scratch
sudo docker compose up --build -d
```

## 📝 What's Running

### PostgreSQL Database
- **Container**: tasktracker-postgres
- **Port**: 5432
- **Database**: tasktracker
- **User**: postgres
- **Password**: postgres
- **Volume**: postgres_data (persists data)

### Spring Boot Backend
- **Container**: tasktracker-backend
- **Port**: 8080
- **Profile**: prod
- **JWT Secret**: Configured via environment
- **Auto-initialized**: Creates demo user and sample tasks

### Angular Frontend
- **Container**: tasktracker-frontend
- **Port**: 80
- **Server**: Nginx
- **API Proxy**: Configured to connect to backend

## 🔄 Development Workflow

For development, you might want to run services separately:

```bash
# Just start database
sudo docker compose up postgres -d

# Then run backend and frontend locally in IntelliJ
# (see INTELLIJ_GUIDE.md)
```

## 📊 Monitoring

Check container status:
```bash
sudo docker compose ps
```

Check resource usage:
```bash
sudo docker stats
```

Check networks:
```bash
sudo docker network ls | grep tasktracker
```

Check volumes:
```bash
sudo docker volume ls | grep tasktracker
```

## 🎉 Success!

Your Task Tracker App should now be running at:
- **http://localhost** (Frontend)
- **http://localhost:8080** (Backend API)

Login with `demo`/`demo123` and start tracking tasks!

