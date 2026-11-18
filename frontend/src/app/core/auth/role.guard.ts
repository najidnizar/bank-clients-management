import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Role } from './role';

/** Lets the navigation through only if the user has the role; otherwise back to the customer list. */
export function roleGuard(role: Role): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    return auth.hasRole(role) ? true : router.createUrlTree(['/customers']);
  };
}