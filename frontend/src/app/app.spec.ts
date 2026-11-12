import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { App } from './app';
import { AuthService } from './core/auth/auth.service';

describe('App', () => {
  const auth = {
    username: () => 'advisor',
    roles: () => ['ADVISOR'],
    logout: vi.fn(),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([]), { provide: AuthService, useValue: auth }],
    });
  });

  it('shows the current user and their roles', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const toolbar = (fixture.nativeElement as HTMLElement).querySelector('mat-toolbar')!;
    expect(toolbar.textContent).toContain('advisor');
    expect(toolbar.textContent).toContain('Advisor');
  });

  it('logs out when the button is clicked', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const button = (fixture.nativeElement as HTMLElement).querySelector('button')!;
    button.click();
    expect(auth.logout).toHaveBeenCalled();
  });
});
