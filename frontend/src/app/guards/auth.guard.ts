import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Authentication Route Guard
 *
 * Protects routes from unauthorized access.
 * Implements CanActivateFn (functional guard - Angular 14+ style).
 *
 * How it works:
 * 1. Angular router calls this function before activating a route
 * 2. Function checks if user is authenticated
 * 3. If authenticated, returns true (allow navigation)
 * 4. If not authenticated, redirects to login and returns false
 *
 * Used in app.routes.ts to protect routes like:
 * { path: 'tasks', component: TaskListComponent, canActivate: [authGuard] }
 *
 * Benefits:
 * - Prevents unauthorized access to protected routes
 * - Automatic redirect to login page
 * - Centralized authorization logic
 *
 * @param route Activated route snapshot
 * @param state Router state snapshot
 * @returns true if user can access route, false otherwise
 */
export const authGuard: CanActivateFn = (route, state) => {
  // Inject dependencies using Angular's inject() function
  // This is the functional style alternative to constructor injection
  const authService = inject(AuthService);
  const router = inject(Router);

  // Check if user has valid authentication
  if (authService.isAuthenticated()) {
    // User is logged in - allow access to route
    return true;
  }

  // User not authenticated - redirect to login page
  router.navigate(['/login']);
  // Return false to prevent navigation to protected route
  return false;
};

