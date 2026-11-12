import { DOCUMENT, Injectable, computed, inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { DEFAULT_LANGUAGE, Language, isLanguage } from './language';

const STORAGE_KEY = 'bank-clients.language';

/** Chooses, switches and remembers the interface language. */
@Injectable({ providedIn: 'root' })
export class LanguageService {
  private readonly translate = inject(TranslateService);
  private readonly document = inject(DOCUMENT);

  /** Current language, as a signal: templates that read it update when it changes. */
  readonly current = computed<Language>(() => {
    const language = this.translate.currentLang();
    return isLanguage(language) ? language : DEFAULT_LANGUAGE;
  });

  /** At startup: the language chosen earlier, otherwise the browser's, otherwise English. */
  init(): Observable<unknown> {
    return this.use(this.savedLanguage() ?? this.browserLanguage());
  }

  /** From the language menu. */
  switchTo(language: Language): void {
    this.use(language).subscribe();
  }

  private use(language: Language): Observable<unknown> {
    this.save(language);
    // Tells screen readers and the browser (spell-check, hyphenation) which language the page is in.
    this.document.documentElement.lang = language;
    // Downloads public/i18n/<language>.json if needed, then switches every translated text.
    return this.translate.use(language);
  }

  private browserLanguage(): Language {
    const language = this.translate.getBrowserLang();
    return isLanguage(language) ? language : DEFAULT_LANGUAGE;
  }

  // localStorage can be unavailable (private browsing, blocked storage): the choice is then simply not kept.
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
      // Not kept for the next visit, nothing else to do.
    }
  }
}
