import { Injectable, inject } from '@angular/core';
import Keycloak from 'keycloak-js';
import { ROLES, Role } from './role';

/**
 * Single entry point to the authenticated user for the rest of the application.
 * Components never use keycloak-js directly: easier to test, and to replace the library.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly keycloak = inject(Keycloak);

  username(): string {
    return this.keycloak.tokenParsed?.['preferred_username'] ?? '';
  }

  /** Application roles of the user; Keycloak technical roles (offline_access...) are ignored. */
  roles(): Role[] {
    const realmRoles = this.keycloak.realmAccess?.roles ?? [];
    return ROLES.filter((role) => realmRoles.includes(role));
  }

  /** For display only: the back-end enforces the real authorization rules. */
  hasRole(role: Role): boolean {
    return this.roles().includes(role);
  }

  logout(): Promise<void> {
    return this.keycloak.logout({ redirectUri: window.location.origin + '/' });
  }
}
