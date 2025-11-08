# Technical Deep Dive - Code-Specific Interview Answers

## 🔍 When They Ask: "Show Me The Code"

---

## Backend Implementation Details

### 1. Task Entity Explained

**File:** `backend/src/main/java/com/tasktracker/model/Task.java`

```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Auto-increment primary key
    
    @Column(nullable = false)
    private String title;  // Required field
    
    @Enumerated(EnumType.STRING)  // Store as "TODO" not "0"
    private TaskStatus status = TaskStatus.TODO;  // Default value
    
    @CreationTimestamp  // Hibernate sets this automatically
    @Column(updatable = false)  // Can't be changed after creation
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)  // Don't load user unless needed
    @JoinColumn(name = "user_id")  // Foreign key column
    private User user;
}
```

**Key Points to Mention:**
- `@GeneratedValue(IDENTITY)`: Uses database auto-increment
- `@Enumerated(STRING)`: Stores "TODO" instead of ordinal (0, 1, 2)
- `@CreationTimestamp`: Hibernate plugin, sets timestamp automatically
- `updatable = false`: Prevents accidental modification
- `FetchType.LAZY`: Performance optimization, user loaded only when accessed
- Default values: `status = TODO`, `priority = MEDIUM`

**Why These Choices:**
- STRING enums are more readable in database
- Lazy loading prevents N+1 query problem
- CreationTimestamp ensures consistency
- Nullable completedAt allows tracking incomplete tasks

---

### 2. JWT Authentication Implementation

**File:** `backend/src/main/java/com/tasktracker/security/JwtUtil.java`

#### Token Generation
```java
public String generateToken(String username) {
    return Jwts.builder()
        .setSubject(username)                    // Who the token is for
        .setIssuedAt(new Date())                 // When created
        .setExpiration(new Date(
            System.currentTimeMillis() + jwtExpiration  // When expires (24hr)
        ))
        .signWith(SignatureAlgorithm.HS256, secretKey)  // Sign with secret
        .compact();                              // Build the token
}
```

**Explain:**
- `setSubject(username)`: JWT "sub" claim, identifies user
- `setExpiration()`: Token expires after 24 hours (86400000 ms)
- `signWith(HS256, secret)`: HMAC SHA-256 signature prevents tampering
- Secret key stored in environment variable (not hardcoded)

#### Token Validation
```java
public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) 
            && !isTokenExpired(token));
}

private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
}
```

**Explain:**
- Extracts username from token
- Compares with UserDetails from database
- Checks expiration timestamp
- Returns false if expired or username mismatch

#### Filter Chain
```java
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain chain) {
        
        // 1. Extract token from Authorization header
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);  // Remove "Bearer "
            username = jwtUtil.extractUsername(jwtToken);
        }
        
        // 2. Validate and set authentication
        if (username != null && SecurityContextHolder.getContext()
                                .getAuthentication() == null) {
            
            UserDetails userDetails = userDetailsService
                .loadUserByUsername(username);
            
            if (jwtUtil.validateToken(jwtToken, userDetails)) {
                // 3. Create authentication object
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                    );
                
                // 4. Set in SecurityContext
                SecurityContextHolder.getContext()
                    .setAuthentication(authToken);
            }
        }
        
        chain.doFilter(request, response);  // Continue to controller
    }
}
```

**Flow Explanation:**
1. Request comes in with `Authorization: Bearer <token>`
2. Filter extracts token, removes "Bearer " prefix
3. Extracts username from token
4. Loads user from database
5. Validates token (signature + expiration)
6. Creates Authentication object
7. Sets in SecurityContext (thread-local storage)
8. Controller can access via `SecurityContextHolder`

---

### 3. Task Service - Business Logic

**File:** `backend/src/main/java/com/tasktracker/service/TaskService.java`

#### getCurrentUser() - Security Foundation
```java
private User getCurrentUser() {
    String username = SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName();
    return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
}
```

**Explain:**
- Gets username from SecurityContext (set by JwtRequestFilter)
- Fetches full User entity from database
- Used in EVERY service method
- Ensures users can only access their own data

#### Create Task with Auto-Assignment
```java
public TaskDTO createTask(TaskRequest request) {
    User user = getCurrentUser();  // Get authenticated user
    
    Task task = new Task();
    task.setTitle(request.getTitle());
    task.setDescription(request.getDescription());
    task.setStatus(request.getStatus());
    task.setPriority(request.getPriority());
    task.setUser(user);  // Automatically assign to current user
    
    Task savedTask = taskRepository.save(task);
    return convertToDTO(savedTask);
}
```

**Key Points:**
- User never specified in request (security!)
- Automatically assigned from JWT token
- Can't create tasks for other users
- Returns DTO, not entity (don't expose User relationship)

#### Update Task with Completion Tracking
```java
public TaskDTO updateTask(Long id, TaskRequest request) {
    User user = getCurrentUser();
    Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
    
    // Security check
    if (!task.getUser().getId().equals(user.getId())) {
        throw new RuntimeException("Unauthorized");
    }
    
    // Auto-set completion time when status changes to DONE
    if (request.getStatus() == TaskStatus.DONE 
        && task.getStatus() != TaskStatus.DONE) {
        task.setCompletedAt(LocalDateTime.now());
    } else if (request.getStatus() != TaskStatus.DONE 
               && task.getStatus() == TaskStatus.DONE) {
        task.setCompletedAt(null);  // Clear if moved back to TODO/IN_PROGRESS
    }
    
    task.setStatus(request.getStatus());
    // ... update other fields
    
    return convertToDTO(taskRepository.save(task));
}
```

**Explain:**
- Authorization check: verify task belongs to user
- Smart completion tracking:
  - Set `completedAt` when status changes TO done
  - Clear `completedAt` when status changes FROM done
  - Enables accurate time tracking for analytics

#### Statistics Calculation
```java
public TaskStatsDTO getTaskStats() {
    User user = getCurrentUser();
    List<Task> allTasks = taskRepository.findByUser(user);
    
    long totalTasks = allTasks.size();
    
    long completedTasks = allTasks.stream()
        .filter(task -> task.getStatus() == TaskStatus.DONE)
        .count();
    
    long pendingTasks = totalTasks - completedTasks;
    
    double avgCompletionTime = allTasks.stream()
        .filter(task -> task.getCompletedAt() != null)
        .mapToLong(task -> Duration.between(
            task.getCreatedAt(), 
            task.getCompletedAt()
        ).toHours())
        .average()
        .orElse(0.0);
    
    return new TaskStatsDTO(totalTasks, completedTasks, 
                           pendingTasks, avgCompletionTime);
}
```

**Technical Concepts:**
- **Java Streams API**: Functional programming for collections
- **filter()**: Keep only completed tasks
- **count()**: Terminal operation, counts elements
- **mapToLong()**: Transform Task → long (duration in hours)
- **Duration.between()**: Calculate time difference
- **average()**: Returns OptionalDouble
- **orElse(0.0)**: Default if no completed tasks

**Why This Approach:**
- Declarative, readable code
- Single database query (fetch all tasks once)
- Efficient in-memory calculation
- For scale: Move to database aggregation query

---

### 4. Spring Data JPA Magic

**File:** `backend/src/main/java/com/tasktracker/repository/TaskRepository.java`

```java
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    List<Task> findByUserAndStatus(User user, TaskStatus status);
    List<Task> findByUserAndPriority(User user, TaskPriority priority);
}
```

**How Spring Parses Method Names:**

| Method Name | Generated SQL |
|-------------|---------------|
| `findByUser` | `SELECT * FROM tasks WHERE user_id = ?` |
| `findByUserAndStatus` | `SELECT * FROM tasks WHERE user_id = ? AND status = ?` |
| `findByUserAndPriority` | `SELECT * FROM tasks WHERE user_id = ? AND priority = ?` |

**Method Name Keywords:**
- `findBy`: SELECT query
- `And`: SQL AND operator
- `Or`: SQL OR operator
- `OrderBy`: SQL ORDER BY
- `Top`, `First`: LIMIT
- `Containing`: SQL LIKE %value%
- `IgnoreCase`: Case-insensitive

**Example Extensions:**
```java
// Find by title containing text (case-insensitive)
List<Task> findByUserAndTitleContainingIgnoreCase(User user, String title);
// SQL: SELECT * FROM tasks WHERE user_id = ? AND LOWER(title) LIKE LOWER('%' + ? + '%')

// Find and sort
List<Task> findByUserOrderByCreatedAtDesc(User user);
// SQL: SELECT * FROM tasks WHERE user_id = ? ORDER BY created_at DESC
```

---

### 5. Security Configuration

**File:** `backend/src/main/java/com/tasktracker/config/SecurityConfig.java`

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf().disable()  // Disable CSRF for stateless JWT
        .cors()            // Enable CORS
        .and()
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()    // Allow login/register
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Swagger
            .anyRequest().authenticated()                    // Everything else requires auth
        )
        .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // No sessions
        .and()
        .addFilterBefore(jwtRequestFilter, 
                        UsernamePasswordAuthenticationFilter.class);  // Add JWT filter
    
    return http.build();
}
```

**Explain Each Part:**

1. **CSRF Disabled:**
   - CSRF tokens needed for session-based auth
   - JWT is stateless, no sessions
   - Safe to disable for API-only backend

2. **CORS Enabled:**
   - Frontend on different domain
   - Configured separately in CorsConfig

3. **Request Matchers:**
   - `/api/auth/**`: Public (login, register)
   - `/swagger-ui/**`: Public (API docs)
   - Everything else: Must be authenticated

4. **Stateless Sessions:**
   - No session cookies
   - Every request must have JWT
   - Enables horizontal scaling

5. **Filter Order:**
   - JwtRequestFilter runs BEFORE Spring's auth filter
   - Sets authentication in SecurityContext
   - Controller receives authenticated user

---

## Frontend Implementation Details

### 6. Angular Authentication Flow

**File:** `frontend/src/app/services/auth.service.ts`

```typescript
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          // Store token in localStorage
          localStorage.setItem('token', response.token);
          localStorage.setItem('username', response.username);
        })
      );
  }
  
  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');  // !! converts to boolean
  }
  
  getToken(): string | null {
    return localStorage.getItem('token');
  }
  
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
  }
}
```

**Key Concepts:**

- **Injectable:** Makes service available for dependency injection
- **providedIn: 'root':** Singleton service (one instance app-wide)
- **Observable:** RxJS async stream (like Promise but more powerful)
- **pipe()**: Chain RxJS operators
- **tap()**: Side effect operator (doesn't modify data, just performs action)
- **localStorage**: Browser storage (persists across refreshes)

**Why localStorage:**
- Survives page refresh
- Simple API
- Alternative: sessionStorage (cleared on tab close)
- Note: Vulnerable to XSS (should use httpOnly cookies in production)

---

### 7. HTTP Interceptor - Auto Token Attachment

**File:** `frontend/src/app/interceptors/auth.interceptor.ts`

```typescript
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  
  if (token) {
    // Clone request and add Authorization header
    const cloned = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(cloned);
  }
  
  return next(req);
};
```

**How It Works:**
1. Intercepts EVERY HTTP request
2. Checks if token exists in localStorage
3. Clones request (HTTP requests are immutable)
4. Adds `Authorization: Bearer <token>` header
5. Passes modified request to backend

**Why Clone:**
- HTTP requests are immutable in Angular
- Can't modify original request
- Must create new request with added header

**Benefit:**
- Don't need to manually add token to every HTTP call
- DRY principle (Don't Repeat Yourself)
- Centralized authentication logic

---

### 8. Route Guard - Protect Routes

**File:** `frontend/src/app/guards/auth.guard.ts`

```typescript
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  if (authService.isAuthenticated()) {
    return true;  // Allow navigation
  } else {
    router.navigate(['/login']);  // Redirect to login
    return false;  // Block navigation
  }
};
```

**Usage in Routes:**
```typescript
export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { 
    path: 'tasks', 
    component: TaskListComponent,
    canActivate: [authGuard]  // Guard applied here
  }
];
```

**Flow:**
1. User tries to navigate to `/tasks`
2. Guard runs before route activates
3. Checks if token exists (isAuthenticated())
4. If yes: Allow navigation (return true)
5. If no: Redirect to login, block navigation (return false)

---

### 9. Task Service - API Communication

**File:** `frontend/src/app/services/task.service.ts`

```typescript
@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = `${environment.apiUrl}/tasks`;
  
  constructor(private http: HttpClient) {}
  
  getTasks(status?: string, priority?: string): Observable<Task[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    if (priority) params = params.set('priority', priority);
    
    return this.http.get<Task[]>(this.apiUrl, { params });
  }
  
  getStats(): Observable<TaskStats> {
    return this.http.get<TaskStats>(`${this.apiUrl}/stats`);
  }
  
  createTask(task: TaskRequest): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task);
  }
  
  updateTask(id: number, task: TaskRequest): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task);
  }
  
  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
```

**Technical Details:**

- **HttpParams:** Type-safe query parameters
- **Observable<T>:** Typed async response
- **Generic Types:** `get<Task[]>` tells TypeScript what to expect
- **Template Literals:** `` `${this.apiUrl}/stats` ``
- **Optional Parameters:** `status?:` means parameter is optional

**Example API Calls:**
```typescript
// GET /api/tasks
this.taskService.getTasks().subscribe(...)

// GET /api/tasks?status=DONE
this.taskService.getTasks('DONE').subscribe(...)

// POST /api/tasks with body
this.taskService.createTask({ title: 'Test', ... }).subscribe(...)
```

---

### 10. Component - Putting It Together

**File:** `frontend/src/app/components/task-list/task-list.component.ts`

```typescript
export class TaskListComponent implements OnInit {
  tasks: Task[] = [];
  errorMessage: string = '';
  
  constructor(
    private taskService: TaskService,  // Dependency injection
    private router: Router
  ) {}
  
  ngOnInit(): void {
    this.loadTasks();  // Called when component initializes
  }
  
  loadTasks(): void {
    this.taskService.getTasks().subscribe({
      next: (tasks) => {
        this.tasks = tasks;  // Success: update tasks array
      },
      error: (error) => {
        this.errorMessage = 'Failed to load tasks';  // Error handling
        console.error(error);
      }
    });
  }
  
  filterByStatus(status: string): void {
    this.taskService.getTasks(status).subscribe({
      next: (tasks) => this.tasks = tasks,
      error: (err) => console.error(err)
    });
  }
  
  deleteTask(id: number): void {
    if (confirm('Are you sure?')) {
      this.taskService.deleteTask(id).subscribe({
        next: () => {
          // Remove from local array (optimistic update)
          this.tasks = this.tasks.filter(t => t.id !== id);
        },
        error: (err) => console.error(err)
      });
    }
  }
}
```

**Lifecycle Hooks:**
- `ngOnInit()`: Runs once after component creation
- Perfect for initial data loading
- Similar to `componentDidMount()` in React

**Observable Subscription:**
```typescript
observable.subscribe({
  next: (data) => { /* handle success */ },
  error: (err) => { /* handle error */ },
  complete: () => { /* optional: called when observable completes */ }
});
```

**Data Binding:**
```html
<!-- Component property → Template -->
<div *ngFor="let task of tasks">
  {{ task.title }}
</div>
```

---

### 11. Analytics Component - Chart.js Integration

**File:** `frontend/src/app/components/analytics/analytics.component.ts`

```typescript
export class AnalyticsComponent implements OnInit {
  stats: TaskStats | null = null;
  
  // Pie Chart Configuration
  public pieChartData: ChartConfiguration<'pie'>['data'] = {
    labels: ['To Do', 'In Progress', 'Done'],
    datasets: [{
      data: [0, 0, 0],  // Will be updated from API
      backgroundColor: ['#f5f5f5', '#2196f3', '#4caf50']
    }]
  };
  
  ngOnInit(): void {
    this.loadStats();
  }
  
  loadStats(): void {
    this.taskService.getStats().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.updateCharts(stats);  // Update chart data
      }
    });
  }
  
  updateCharts(stats: TaskStats): void {
    // Update pie chart data
    this.pieChartData.datasets[0].data = [
      stats.todoCount,
      stats.inProgressCount,
      stats.completedCount
    ];
    
    // Update bar chart data
    this.barChartData.datasets[0].data = [
      stats.pendingCount,
      stats.completedCount
    ];
  }
}
```

**Chart.js Integration:**
- Import `BaseChartDirective` from ng2-charts
- Configure chart type, data, options
- Update data dynamically when API response arrives
- Charts automatically re-render on data change

---

## Database Implementation

### 12. Schema Design

**PostgreSQL Tables:**

```sql
-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL  -- BCrypt hash
);

-- Tasks table
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    status VARCHAR(50) NOT NULL,      -- 'TODO', 'IN_PROGRESS', 'DONE'
    priority VARCHAR(50) NOT NULL,    -- 'LOW', 'MEDIUM', 'HIGH'
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Indexes for performance
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_user_status ON tasks(user_id, status);
```

**Design Decisions:**
- BIGSERIAL: Auto-incrementing 64-bit integer
- VARCHAR vs TEXT: VARCHAR with limit for validation
- TIMESTAMP: PostgreSQL native type for dates
- Foreign key: Enforces referential integrity
- Indexes: Speed up WHERE clauses on user_id and status

---

## Docker Configuration

### 13. Multi-Stage Dockerfile (Backend)

**File:** `backend/Dockerfile`

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Why Multi-Stage:**
- Stage 1: Full Maven + JDK (build JAR)
- Stage 2: Only JRE (run JAR)
- Final image doesn't include Maven or build tools
- Smaller image size (JRE vs JDK)
- More secure (fewer attack surfaces)

**Image Size Comparison:**
- Single-stage: ~500 MB
- Multi-stage: ~200 MB

---

## Environment Configuration

### 14. Environment-Based Config

**File:** `backend/src/main/resources/application.properties`

```properties
# Development (default)
spring.datasource.url=jdbc:h2:mem:taskdb
spring.jpa.hibernate.ddl-auto=create-drop
```

**File:** `backend/src/main/resources/application-prod.properties`

```properties
# Production
spring.datasource.url=${DATABASE_URL}
spring.jpa.hibernate.ddl-auto=update
jwt.secret=${JWT_SECRET}
```

**How It Works:**
- `spring.profiles.active=prod` selects profile
- `${DATABASE_URL}` reads from environment variable
- Different config for dev vs prod
- Never commit secrets to Git

**Activation:**
```bash
# Development
mvn spring-boot:run

# Production
mvn spring-boot:run -Dspring.profiles.active=prod
```

---

## Common Interview Scenarios

### "Add a search feature right now"

```java
// Backend
@GetMapping("/search")
public ResponseEntity<List<TaskDTO>> searchTasks(@RequestParam String q) {
    return ResponseEntity.ok(taskService.searchTasks(q));
}

// Service
public List<TaskDTO> searchTasks(String query) {
    User user = getCurrentUser();
    return taskRepository
        .findByUserAndTitleContainingIgnoreCase(user, query)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
}

// Repository  
List<Task> findByUserAndTitleContainingIgnoreCase(User user, String title);
```

```typescript
// Frontend
searchTasks(query: string): Observable<Task[]> {
  return this.http.get<Task[]>(`${this.apiUrl}/search?q=${query}`);
}
```

---

### "How would you add pagination?"

```java
// Backend
@GetMapping
public ResponseEntity<Page<TaskDTO>> getAllTasks(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<Task> taskPage = taskRepository.findByUser(getCurrentUser(), pageable);
    Page<TaskDTO> dtoPage = taskPage.map(this::convertToDTO);
    return ResponseEntity.ok(dtoPage);
}

// Repository
Page<Task> findByUser(User user, Pageable pageable);
```

---

### "Explain a bug you'd fix"

**Potential Issues:**
1. **Token expiration handling:** Frontend doesn't handle 401, should redirect to login
2. **Error messages:** Too generic, should be more specific
3. **Loading states:** No loading spinners during API calls
4. **Validation:** Could add more robust input validation

---

## Quick Reference: Key Files

| File | Purpose |
|------|---------|
| `TaskController.java` | REST endpoints |
| `TaskService.java` | Business logic |
| `TaskRepository.java` | Database queries |
| `Task.java` | Database entity |
| `JwtUtil.java` | JWT generation/validation |
| `JwtRequestFilter.java` | JWT authentication filter |
| `SecurityConfig.java` | Security configuration |
| `auth.service.ts` | Frontend auth logic |
| `auth.interceptor.ts` | Auto-add JWT token |
| `auth.guard.ts` | Protect routes |
| `task.service.ts` | API calls |

---

**Remember:** You know this code because you wrote it. Be confident!

