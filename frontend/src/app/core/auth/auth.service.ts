import { Injectable, inject } from '@angular/core';
import Keycloak from 'keycloak-js';
import { ROLES, Role } from './role';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly keycloak = inject(Keycloak);

  username(): string {
    return this.keycloak.tokenParsed?.['preferred_username'] ?? '';
  }

  // Ignores Keycloak's technical roles (offline_access, default-roles-bank).
  roles(): Role[] {
    const realmRoles = this.keycloak.realmAccess?.roles ?? [];
    return ROLES.filter((role) => realmRoles.includes(role));
  }

  // UI only: authorization is enforced by the back-end.
  hasRole(role: Role): boolean {
    return this.roles().includes(role);
  }

  logout(): Promise<void> {
    return this.keycloak.logout({ redirectUri: window.location.origin + '/' });
  }
}
