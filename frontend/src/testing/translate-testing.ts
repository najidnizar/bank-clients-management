import {
  TranslateLoader,
  TranslationObject,
  provideTranslateLoader,
  provideTranslateService,
} from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import en from '../../public/i18n/en.json';
import fr from '../../public/i18n/fr.json';

class StaticTranslateLoader implements TranslateLoader {
  getTranslation(lang: string): Observable<TranslationObject> {
    return of(lang === 'fr' ? fr : en);
  }
}

// Real translation files, served without HTTP.
export function provideTranslateTesting(lang: 'en' | 'fr' = 'en') {
  return provideTranslateService({
    loader: provideTranslateLoader(StaticTranslateLoader),
    fallbackLang: 'en',
    lang,
  });
}
