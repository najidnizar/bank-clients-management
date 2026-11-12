import { TestBed } from '@angular/core/testing';
import Keycloak from 'keycloak-js';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  // Fake keycloak-js instance: only the fields the service reads.
  function setup(username: string, realmRoles: string[]) {
    const keycloak = {
      tokenParsed: { preferred_username: username },
      realmAccess: { roles: realmRoles },
      logout: vi.fn().mockResolvedValue(undefined),
    };
    TestBed.configureTestingModule({ providers: [{ provide: Keycloak, useValue: keycloak }] });
    return { service: TestBed.inject(AuthService), keycloak };
  }

  it('returns the username from the access token', () => {
    const { service } = setup('advisor', ['ADVISOR']);
    expect(service.username()).toBe('advisor');
  });

  it('keeps only the application roles', () => {
    const { service } = setup('admin', ['default-roles-bank', 'offline_access', 'ADMIN']);
    expect(service.roles()).toEqual(['ADMIN']);
  });

  it('tells whether the user has a role', () => {
    const { service } = setup('compliance', ['COMPLIANCE_OFFICER']);
    expect(service.hasRole('COMPLIANCE_OFFICER')).toBe(true);
    expect(service.hasRole('ADMIN')).toBe(false);
  });

  it('logs out and comes back to the application', async () => {
    const { service, keycloak } = setup('advisor', ['ADVISOR']);
    await service.logout();
    expect(keycloak.logout).toHaveBeenCalledWith({ redirectUri: window.location.origin + '/' });
  });
});
