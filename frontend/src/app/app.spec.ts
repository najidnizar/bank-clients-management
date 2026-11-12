import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateTesting } from '../testing/translate-testing';
import { App } from './app';
import { AuthService } from './core/auth/auth.service';
import { LanguageService } from './core/i18n/language.service';

describe('App', () => {
  const auth = {
    username: () => 'advisor',
    roles: () => ['ADVISOR'],
    logout: vi.fn(),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideRouter([]),
        provideTranslateTesting(),
        { provide: AuthService, useValue: auth },
      ],
    });
  });

  function toolbarText(fixture: { nativeElement: HTMLElement }): string {
    return fixture.nativeElement.querySelector('mat-toolbar')?.textContent ?? '';
  }

  it('shows the current user and their roles', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    expect(toolbarText(fixture)).toContain('advisor');
    expect(toolbarText(fixture)).toContain('Advisor');
  });

  it('translates the interface when the language changes', async () => {
    const fixture = TestBed.createComponent(App);
    TestBed.inject(LanguageService).switchTo('fr');
    await fixture.whenStable();
    expect(toolbarText(fixture)).toContain('Conseiller');
    expect(toolbarText(fixture)).toContain('Se déconnecter');
  });

  it('logs out when the button is clicked', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const button = fixture.nativeElement.querySelector('[data-testid="logout"]') as HTMLElement;
    button.click();
    expect(auth.logout).toHaveBeenCalled();
  });
});
