# 📡 API Documentation - Task Tracker

## Base URL
```
Development: http://localhost:8080/api
Production: <your-domain>/api
```

## Authentication

All endpoints except `/auth/login` and `/auth/test` require JWT authentication.

**Header Format:**
```
Authorization: Bearer <your-jwt-token>
```

---

## 🔐 Authentication Endpoints

### 1. Login

**POST** `/api/auth/login`

Authenticate user and receive JWT token.

**Request Body:**
```json
{
  "username": "demo",
  "password": "demo123"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "demo",
  "email": "demo@tasktracker.com",
  "message": "Login successful"
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Invalid username or password"
}
```

### 2. Test Endpoint

**GET** `/api/auth/test`

Test if authentication service is running.

**Success Response (200 OK):**
```
Auth endpoint is working!
```

---

## 📋 Task Endpoints

### 1. Get All Tasks

**GET** `/api/tasks`

Retrieve all tasks for authenticated user.

**Headers:**
```
Authorization: Bearer <token>
```

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Complete project documentation",
    "description": "Write comprehensive documentation for the project",
    "status": "IN_PROGRESS",
    "priority": "HIGH",
    "createdAt": "2025-11-06T10:30:00",
    "completedAt": null
  },
  {
    "id": 2,
    "title": "Review pull requests",
    "description": "Review and merge pending pull requests",
    "status": "TODO",
    "priority": "MEDIUM",
    "createdAt": "2025-11-06T09:15:00",
    "completedAt": null
  }
]
```

### 2. Get Task by ID

**GET** `/api/tasks/{id}`

Retrieve a specific task by ID.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (number, required) - Task ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "title": "Complete project documentation",
  "description": "Write comprehensive documentation for the project",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "createdAt": "2025-11-06T10:30:00",
  "completedAt": null
}
```

**Error Response (404 Not Found):**
```json
{
  "error": "Task not found"
}
```

### 3. Create Task

**POST** `/api/tasks`

Create a new task.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "New Task Title",
  "description": "Task description here",
  "status": "TODO",
  "priority": "MEDIUM"
}
```

**Field Validation:**
- `title` (string, required) - Task title
- `description` (string, optional) - Task description
- `status` (enum, required) - One of: `TODO`, `IN_PROGRESS`, `DONE`
- `priority` (enum, required) - One of: `LOW`, `MEDIUM`, `HIGH`

**Success Response (201 Created):**
```json
{
  "id": 3,
  "title": "New Task Title",
  "description": "Task description here",
  "status": "TODO",
  "priority": "MEDIUM",
  "createdAt": "2025-11-06T14:20:00",
  "completedAt": null
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Title is required"
}
```

### 4. Update Task

**PUT** `/api/tasks/{id}`

Update an existing task.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Path Parameters:**
- `id` (number, required) - Task ID

**Request Body:**
```json
{
  "title": "Updated Task Title",
  "description": "Updated description",
  "status": "DONE",
  "priority": "HIGH"
}
```

**Success Response (200 OK):**
```json
{
  "id": 3,
  "title": "Updated Task Title",
  "description": "Updated description",
  "status": "DONE",
  "priority": "HIGH",
  "createdAt": "2025-11-06T14:20:00",
  "completedAt": "2025-11-06T16:45:00"
}
```

**Notes:**
- When status changes to `DONE`, `completedAt` is automatically set
- When status changes from `DONE` to other status, `completedAt` is cleared

**Error Response (400 Bad Request):**
```json
{
  "error": "Unauthorized access to task"
}
```

### 5. Delete Task

**DELETE** `/api/tasks/{id}`

Delete a task.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (number, required) - Task ID

**Success Response (200 OK):**
```json
{
  "message": "Task deleted successfully"
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Task not found"
}
```

### 6. Filter Tasks by Status

**GET** `/api/tasks?status={status}`

Get tasks filtered by status.

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**
- `status` (string, required) - One of: `TODO`, `IN_PROGRESS`, `DONE`

**Example:**
```
GET /api/tasks?status=TODO
```

**Success Response (200 OK):**
```json
[
  {
    "id": 2,
    "title": "Review pull requests",
    "description": "Review and merge pending pull requests",
    "status": "TODO",
    "priority": "MEDIUM",
    "createdAt": "2025-11-06T09:15:00",
    "completedAt": null
  }
]
```

### 7. Filter Tasks by Priority

**GET** `/api/tasks?priority={priority}`

Get tasks filtered by priority.

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**
- `priority` (string, required) - One of: `LOW`, `MEDIUM`, `HIGH`

**Example:**
```
GET /api/tasks?priority=HIGH
```

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Complete project documentation",
    "description": "Write comprehensive documentation for the project",
    "status": "IN_PROGRESS",
    "priority": "HIGH",
    "createdAt": "2025-11-06T10:30:00",
    "completedAt": null
  }
]
```

### 8. Get Task Statistics

**GET** `/api/tasks/stats`

Get comprehensive task statistics for authenticated user.

**Headers:**
```
Authorization: Bearer <token>
```

**Success Response (200 OK):**
```json
{
  "totalTasks": 10,
  "completedTasks": 5,
  "pendingTasks": 5,
  "averageCompletionTimeHours": 24.5,
  "todoTasks": 3,
  "inProgressTasks": 2
}
```

**Response Fields:**
- `totalTasks` - Total number of tasks
- `completedTasks` - Number of tasks with status DONE
- `pendingTasks` - Number of tasks not completed (TODO + IN_PROGRESS)
- `averageCompletionTimeHours` - Average time to complete tasks in hours
- `todoTasks` - Number of tasks with status TODO
- `inProgressTasks` - Number of tasks with status IN_PROGRESS

---

## 🔍 Example Usage with cURL

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo",
    "password": "demo123"
  }'
```

### Create Task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "New Task",
    "description": "Task description",
    "status": "TODO",
    "priority": "HIGH"
  }'
```

### Get All Tasks
```bash
curl -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Update Task
```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Task",
    "description": "Updated description",
    "status": "DONE",
    "priority": "HIGH"
  }'
```

### Delete Task
```bash
curl -X DELETE http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Statistics
```bash
curl -X GET http://localhost:8080/api/tasks/stats \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🔍 Example Usage with JavaScript (Fetch)

### Login
```javascript
const login = async () => {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      username: 'demo',
      password: 'demo123'
    })
  });
  
  const data = await response.json();
  localStorage.setItem('token', data.token);
  return data;
};
```

### Get All Tasks
```javascript
const getTasks = async () => {
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8080/api/tasks', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return await response.json();
};
```

### Create Task
```javascript
const createTask = async (task) => {
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8080/api/tasks', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(task)
  });
  
  return await response.json();
};
```

---

## ⚠️ Error Responses

### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token"
}
```

### 403 Forbidden
```json
{
  "error": "Forbidden",
  "message": "Access denied"
}
```

### 404 Not Found
```json
{
  "error": "Not Found",
  "message": "Resource not found"
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```

---

## 📝 Notes

1. **JWT Token Expiration**: Tokens expire after 24 hours (configurable)
2. **CORS**: Configure allowed origins in backend configuration
3. **Rate Limiting**: Not implemented (consider adding for production)
4. **Pagination**: Not implemented (returns all tasks)
5. **Sorting**: Not implemented (returns tasks in creation order)

---

## 🔐 Security Considerations

1. Always use HTTPS in production
2. Store JWT tokens securely (httpOnly cookies recommended)
3. Implement refresh token mechanism for better security
4. Add rate limiting to prevent abuse
5. Validate all inputs on both client and server side
6. Use strong JWT secrets (at least 256 bits)

---

**Last Updated**: November 6, 2025

