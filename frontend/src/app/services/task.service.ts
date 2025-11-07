import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskStats } from '../models/task.model';
import { environment } from '../../environments/environment';

/**
 * Task Service - handles all task-related HTTP requests.
 *
 * Communicates with backend REST API for CRUD operations and filtering.
 * Returns RxJS Observables for asynchronous data handling.
 *
 * Features:
 * - Full CRUD operations (Create, Read, Update, Delete)
 * - Filtering by status and priority
 * - Task statistics retrieval
 * - Automatic JWT token injection via AuthInterceptor
 *
 * HTTP Flow:
 * Component → TaskService → HttpClient → AuthInterceptor (add JWT) → Backend API
 */
@Injectable({
  providedIn: 'root'  // Singleton service available throughout the app
})
export class TaskService {
  // API base URL from environment configuration (changes per environment)
  private apiUrl = `${environment.apiUrl}/tasks`;

  /**
   * Constructor with HttpClient dependency injection.
   *
   * @param http Angular HTTP client for making API requests
   */
  constructor(private http: HttpClient) { }

  /**
   * Retrieves all tasks for the authenticated user.
   *
   * Makes GET request to /api/tasks
   * JWT token automatically added by AuthInterceptor.
   *
   * @returns Observable of Task array
   */
  getAllTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.apiUrl);
  }

  /**
   * Retrieves a specific task by ID.
   *
   * Makes GET request to /api/tasks/{id}
   * Returns 404 if task not found or doesn't belong to user.
   *
   * @param id Task ID to retrieve
   * @returns Observable of single Task
   */
  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  /**
   * Retrieves tasks filtered by status.
   *
   * Makes GET request to /api/tasks?status=TODO|IN_PROGRESS|DONE
   * Uses HttpParams to build query string safely.
   *
   * @param status Task status to filter by (TODO, IN_PROGRESS, DONE)
   * @returns Observable of filtered Task array
   */
  getTasksByStatus(status: string): Observable<Task[]> {
    // HttpParams safely encodes query parameters
    const params = new HttpParams().set('status', status);
    return this.http.get<Task[]>(this.apiUrl, { params });
  }

  /**
   * Retrieves tasks filtered by priority.
   *
   * Makes GET request to /api/tasks?priority=LOW|MEDIUM|HIGH
   *
   * @param priority Task priority to filter by (LOW, MEDIUM, HIGH)
   * @returns Observable of filtered Task array
   */
  getTasksByPriority(priority: string): Observable<Task[]> {
    const params = new HttpParams().set('priority', priority);
    return this.http.get<Task[]>(this.apiUrl, { params });
  }

  /**
   * Creates a new task.
   *
   * Makes POST request to /api/tasks with task data in body.
   * Automatically associates task with authenticated user on backend.
   * Returns created task with generated ID and timestamps.
   *
   * @param task Task object to create (without ID)
   * @returns Observable of created Task with ID
   */
  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task);
  }

  /**
   * Updates an existing task.
   *
   * Makes PUT request to /api/tasks/{id} with updated task data.
   * Backend validates that task belongs to authenticated user.
   * Automatically sets completedAt timestamp if status changes to DONE.
   *
   * @param id Task ID to update
   * @param task Updated task data
   * @returns Observable of updated Task
   */
  updateTask(id: number, task: Task): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task);
  }

  /**
   * Deletes a task.
   *
   * Makes DELETE request to /api/tasks/{id}
   * Backend validates that task belongs to authenticated user.
   * Permanent deletion - cannot be undone.
   *
   * @param id Task ID to delete
   * @returns Observable of delete response
   */
  deleteTask(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  /**
   * Retrieves task statistics for the authenticated user.
   *
   * Makes GET request to /api/tasks/stats
   * Returns aggregated statistics:
   * - Total task count
   * - Completed vs pending tasks
   * - Average completion time
   * - Breakdown by status
   *
   * @returns Observable of TaskStats object
   */
  getTaskStats(): Observable<TaskStats> {
    return this.http.get<TaskStats>(`${this.apiUrl}/stats`);
  }
}

