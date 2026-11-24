import { DOCUMENT, Injectable, computed, inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { DEFAULT_LANGUAGE, Language, isLanguage } from './language';

const STORAGE_KEY = 'bank-clients.language';

@Injectable({ providedIn: 'root' })
export class LanguageService {
  private readonly translate = inject(TranslateService);
  private readonly document = inject(DOCUMENT);

  readonly current = computed<Language>(() => {
    const language = this.translate.currentLang();
    return isLanguage(language) ? language : DEFAULT_LANGUAGE;
  });

  // Saved choice, then browser language, then English.
  init(): Observable<unknown> {
    return this.use(this.savedLanguage() ?? this.browserLanguage());
  }

  switchTo(language: Language): void {
    this.use(language).subscribe();
  }

  private use(language: Language): Observable<unknown> {
    this.save(language);
    // Screen readers rely on the lang attribute.
    this.document.documentElement.lang = language;
    return this.translate.use(language);
  }

  private browserLanguage(): Language {
    const language = this.translate.getBrowserLang();
    return isLanguage(language) ? language : DEFAULT_LANGUAGE;
  }

  // localStorage may be unavailable (private browsing, blocked storage).
  private savedLanguage(): Language | undefined {
    try {
      const language = localStorage.getItem(STORAGE_KEY);
      return isLanguage(language) ? language : undefined;
    } catch {
      return undefined;
    }
  }

  private save(language: Language): void {
    try {
      localStorage.setItem(STORAGE_KEY, language);
    } catch {
      // The choice is just not remembered.
    }
  }
}
