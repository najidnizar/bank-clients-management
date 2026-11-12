import {
  TranslateLoader,
  TranslationObject,
  provideTranslateLoader,
  provideTranslateService,
} from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import en from '../../public/i18n/en.json';
import fr from '../../public/i18n/fr.json';

/** Serves the real translation files synchronously, without HTTP. */
class StaticTranslateLoader implements TranslateLoader {
  getTranslation(lang: string): Observable<TranslationObject> {
    return of(lang === 'fr' ? fr : en);
  }
}

/** Translations for unit tests: the real texts, so tests read like the screen. */
export function provideTranslateTesting(lang: 'en' | 'fr' = 'en') {
  return provideTranslateService({
    loader: provideTranslateLoader(StaticTranslateLoader),
    fallbackLang: 'en',
    lang,
  });
}
