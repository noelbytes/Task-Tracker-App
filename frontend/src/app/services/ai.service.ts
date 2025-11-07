import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

/**
 * AI Service - handles AI-powered features.
 *
 * Provides:
 * - Natural language task parsing
 * - Smart task suggestions
 * - Priority recommendations
 * - Productivity insights
 *
 * All endpoints require JWT authentication (handled by AuthInterceptor).
 */
@Injectable({
  providedIn: 'root'
})
export class AIService {
  private apiUrl = `${environment.apiUrl}/ai`;

  constructor(private http: HttpClient) { }

  /**
   * Parses natural language text into structured task fields.
   *
   * @param text Plain text task description
   * @returns Parsed task with title, description, priority
   */
  parseNaturalLanguageTask(text: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/parse-task`, { text });
  }

  /**
   * Gets AI-generated task suggestions based on user history.
   *
   * @returns Array of suggested tasks and productivity insight
   */
  getTaskSuggestions(): Observable<{ suggestions: string[], insight: string }> {
    return this.http.get<{ suggestions: string[], insight: string }>(`${this.apiUrl}/suggestions`);
  }

  /**
   * Gets AI-recommended priority for a task.
   *
   * @param title Task title
   * @param description Task description
   * @returns Recommended priority (LOW, MEDIUM, HIGH)
   */
  recommendPriority(title: string, description?: string): Observable<{ recommendedPriority: string, title: string }> {
    let url = `${this.apiUrl}/recommend-priority?title=${encodeURIComponent(title)}`;
    if (description) {
      url += `&description=${encodeURIComponent(description)}`;
    }
    return this.http.get<{ recommendedPriority: string, title: string }>(url);
  }

  /**
   * Gets productivity insight based on task history.
   *
   * @returns AI-generated productivity advice
   */
  getProductivityInsight(): Observable<{ insight: string }> {
    return this.http.get<{ insight: string }>(`${this.apiUrl}/productivity-insight`);
  }

  /**
   * Checks if AI service is available.
   *
   * @returns AI service status and provider info
   */
  getAIStatus(): Observable<{ available: boolean, provider: string, model: string, cost: string }> {
    return this.http.get<{ available: boolean, provider: string, model: string, cost: string }>(`${this.apiUrl}/status`);
  }
}

