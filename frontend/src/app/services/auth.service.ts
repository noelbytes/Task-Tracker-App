import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse, User } from '../models/auth.model';
import { environment } from '../../environments/environment';

/**
 * Authentication Service
 *
 * Handles user authentication, JWT token management, and user state.
 * Uses RxJS BehaviorSubject for reactive state management.
 *
 * Key Features:
 * - Login/logout functionality
 * - JWT token storage in localStorage
 * - Observable user state for reactive updates across components
 * - Token persistence across page refreshes
 */
@Injectable({
  providedIn: 'root'  // Singleton service available throughout the app
})
export class AuthService {
  // API base URL from environment configuration
  private apiUrl = environment.apiUrl;

  // BehaviorSubject holds current user state and emits to subscribers
  // BehaviorSubject vs Subject: always has a current value and emits immediately to new subscribers
  private currentUserSubject: BehaviorSubject<User | null>;

  // Observable stream of user changes - components can subscribe to this
  public currentUser: Observable<User | null>;

  /**
   * Constructor - initializes user state from localStorage if available.
   *
   * This ensures user remains logged in even after page refresh.
   *
   * @param http Angular HTTP client for API calls
   */
  constructor(private http: HttpClient) {
    // Try to restore user from localStorage (persists across page refreshes)
    const storedUser = localStorage.getItem('currentUser');
    this.currentUserSubject = new BehaviorSubject<User | null>(
      storedUser ? JSON.parse(storedUser) : null
    );
    // Expose as Observable (read-only) to prevent direct manipulation from components
    this.currentUser = this.currentUserSubject.asObservable();
  }

  /**
   * Gets the current user value synchronously.
   *
   * Useful when you need immediate access without subscribing to Observable.
   *
   * @returns Current user or null if not authenticated
   */
  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Authenticates user with backend API.
   *
   * Process:
   * 1. Send credentials to backend
   * 2. Receive JWT token on success
   * 3. Store user and token in localStorage
   * 4. Update currentUser observable (notifies all subscribers)
   *
   * Uses RxJS tap operator to perform side effects without changing the stream.
   *
   * @param credentials Username and password
   * @returns Observable of login response
   */
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(
        // tap allows us to perform side effects without modifying the observable
        tap(response => {
          // Create user object with token
          const user: User = {
            username: response.username,
            email: response.email,
            token: response.token
          };
          // Persist to localStorage for session persistence
          localStorage.setItem('currentUser', JSON.stringify(user));
          // Emit new user state to all subscribers (e.g., navbar, auth guard)
          this.currentUserSubject.next(user);
        })
      );
  }

  /**
   * Logs out current user.
   *
   * Clears user data from localStorage and resets currentUser state.
   * Components subscribed to currentUser will be notified of the change.
   */
  logout(): void {
    // Remove from persistent storage
    localStorage.removeItem('currentUser');
    // Emit null to all subscribers (triggers navigation to login in auth guard)
    this.currentUserSubject.next(null);
  }

  /**
   * Checks if user is currently authenticated.
   *
   * Uses double negation (!!) to convert to boolean:
   * - null/undefined → false
   * - object → true
   *
   * @returns true if user is logged in
   */
  isAuthenticated(): boolean {
    return !!this.currentUserValue;
  }

  /**
   * Retrieves JWT token for API requests.
   *
   * Used by AuthInterceptor to automatically add Authorization header.
   *
   * @returns JWT token string or null if not authenticated
   */
  getToken(): string | null {
    return this.currentUserValue?.token || null;  // Optional chaining (?.) returns undefined if null
  }
}

