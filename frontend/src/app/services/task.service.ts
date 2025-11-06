import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskStats } from '../models/task.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = `${environment.apiUrl}/tasks`;

  constructor(private http: HttpClient) { }

  getAllTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.apiUrl);
  }

  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  getTasksByStatus(status: string): Observable<Task[]> {
    const params = new HttpParams().set('status', status);
    return this.http.get<Task[]>(this.apiUrl, { params });
  }

  getTasksByPriority(priority: string): Observable<Task[]> {
    const params = new HttpParams().set('priority', priority);
    return this.http.get<Task[]>(this.apiUrl, { params });
  }

  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task);
  }

  updateTask(id: number, task: Task): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task);
  }

  deleteTask(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  getTaskStats(): Observable<TaskStats> {
    return this.http.get<TaskStats>(`${this.apiUrl}/stats`);
  }
}

