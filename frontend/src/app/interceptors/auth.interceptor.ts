import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Authentication HTTP Interceptor
 *
 * Automatically adds JWT token to outgoing HTTP requests.
 * This is a functional interceptor (Angular 14+ style) instead of class-based.
 *
 * How it works:
 * 1. Intercepts every HTTP request made by the app
 * 2. Checks if user is authenticated (has token in localStorage)
 * 3. If authenticated, clones request and adds Authorization header
 * 4. Passes modified request to next handler
 *
 * Benefits:
 * - No need to manually add token to every API call
 * - Centralized authentication logic
 * - Follows DRY principle
 *
 * Request Flow:
 * Component → HttpClient → Interceptor (add token) → Backend API
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Retrieve current user from localStorage
  const currentUser = localStorage.getItem('currentUser');

  // If user is authenticated, add JWT token to request
  if (currentUser) {
    const user = JSON.parse(currentUser);

    // Clone the request to add new header
    // HTTP requests are immutable in Angular - must clone to modify
    const cloned = req.clone({
      setHeaders: {
        // Add Authorization header with Bearer token format
        // Backend expects: "Authorization: Bearer <jwt-token>"
        Authorization: `Bearer ${user.token}`
      }
    });

    // Pass modified request to next handler (could be another interceptor or HttpClient)
    return next(cloned);
  }

  // No user logged in - pass original request unchanged
  // Public endpoints (login) don't need Authorization header
  return next(req);
};

