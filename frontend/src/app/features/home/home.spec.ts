import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Home } from './home';
import { AuthService } from '../../core/auth/auth.service';
import { Role } from '../../core/auth/role';
import { provideTranslateTesting } from '../../../testing/translate-testing';

describe('Home', () => {
  let httpMock: HttpTestingController;

  function create(roles: Role[]) {
    TestBed.configureTestingModule({
      imports: [Home],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(), // HTTP calls are captured instead of being sent
        provideTranslateTesting(),
        {
          provide: AuthService,
          useValue: { username: () => 'user', hasRole: (role: Role) => roles.includes(role) },
        },
      ],
    });
    httpMock = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(Home);
    TestBed.tick(); // runs change detection, which starts the httpResource request
    return fixture;
  }

  function text(fixture: { nativeElement: HTMLElement }): string {
    return fixture.nativeElement.textContent ?? '';
  }

  afterEach(() => httpMock.verify()); // no unexpected request left

  it('shows only the actions allowed by the roles', async () => {
    const fixture = create(['COMPLIANCE_OFFICER']);
    httpMock.expectOne('/api/v1/customers/ping').flush('running');
    await fixture.whenStable();

    expect(text(fixture)).toContain('Review KYC cases');
    expect(text(fixture)).not.toContain('Register and update customers');
    expect(text(fixture)).not.toContain('Block, unblock and close customers');
  });

  it('shows the API as reachable when the ping succeeds', async () => {
    const fixture = create(['ADVISOR']);
    httpMock.expectOne('/api/v1/customers/ping').flush('running');
    await fixture.whenStable();

    expect(text(fixture)).toContain('API reachable');
  });

  it('shows the API as unreachable when the ping fails', async () => {
    const fixture = create(['ADVISOR']);
    httpMock
      .expectOne('/api/v1/customers/ping')
      .flush('error', { status: 502, statusText: 'Bad Gateway' });
    await fixture.whenStable();

    expect(text(fixture)).toContain('API unreachable');
  });
});
