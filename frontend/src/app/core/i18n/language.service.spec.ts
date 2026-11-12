import { TestBed } from '@angular/core/testing';
import { TranslateService } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';
import { provideTranslateTesting } from '../../../testing/translate-testing';
import { LanguageService } from './language.service';

describe('LanguageService', () => {
  let service: LanguageService;
  let translate: TranslateService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({ providers: [provideTranslateTesting()] });
    service = TestBed.inject(LanguageService);
    translate = TestBed.inject(TranslateService);
  });

  it('uses the browser language at the first visit', async () => {
    vi.spyOn(translate, 'getBrowserLang').mockReturnValue('fr');
    await firstValueFrom(service.init());
    expect(service.current()).toBe('fr');
  });

  it('falls back to English for an unsupported browser language', async () => {
    vi.spyOn(translate, 'getBrowserLang').mockReturnValue('de');
    await firstValueFrom(service.init());
    expect(service.current()).toBe('en');
  });

  it('keeps the language chosen at a previous visit', async () => {
    localStorage.setItem('bank-clients.language', 'fr');
    vi.spyOn(translate, 'getBrowserLang').mockReturnValue('en');
    await firstValueFrom(service.init());
    expect(service.current()).toBe('fr');
  });

  it('switches the language, remembers it and sets the page language', () => {
    service.switchTo('fr');
    expect(service.current()).toBe('fr');
    expect(localStorage.getItem('bank-clients.language')).toBe('fr');
    expect(document.documentElement.lang).toBe('fr');
    expect(translate.instant('shell.logout')).toBe('Se déconnecter');
  });
});
