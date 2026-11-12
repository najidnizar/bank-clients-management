import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { TitleStrategy, provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';
import { MAT_ICON_DEFAULT_OPTIONS } from '@angular/material/icon';
import {
  AutoRefreshTokenService,
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  IncludeBearerTokenCondition,
  UserActivityService,
  createInterceptorCondition,
  includeBearerTokenInterceptor,
  provideKeycloak,
  withAutoRefreshToken,
} from 'keycloak-angular';
import { environment } from '../environments/environment';
import { routes } from './app.routes';
import { DEFAULT_LANGUAGE } from './core/i18n/language';
import { LanguageService } from './core/i18n/language.service';
import { TranslatedTitleStrategy } from './core/i18n/translated-title.strategy';

// The access token is only added to calls to our own API (/api/...), never to other domains.
const apiCondition = createInterceptorCondition<IncludeBearerTokenCondition>({
  urlPattern: /^\/api\//,
});

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideKeycloak({
      config: environment.keycloak,
      initOptions: {
        onLoad: 'login-required', // internal banking tool: no public page, login first
        pkceMethod: 'S256',
        checkLoginIframe: false, // relies on third-party cookies, blocked by modern browsers
      },
      features: [
        // Refresh the token while the user is active; log out after 15 minutes of inactivity.
        withAutoRefreshToken({
          onInactivityTimeout: 'logout',
          sessionTimeout: 15 * 60 * 1000,
          logoutOptions: { redirectUri: window.location.origin + '/' },
        }),
      ],
      providers: [AutoRefreshTokenService, UserActivityService],
    }),
    { provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG, useValue: [apiCondition] },
    provideHttpClient(withInterceptors([includeBearerTokenInterceptor])),
    provideRouter(routes),
    { provide: TitleStrategy, useClass: TranslatedTitleStrategy },
    // Translations: public/i18n/<language>.json, downloaded when a language is used.
    provideTranslateService({
      loader: provideTranslateHttpLoader({ prefix: '/i18n/', suffix: '.json' }),
      fallbackLang: DEFAULT_LANGUAGE, // a key missing in French is shown in English
    }),
    // Load the language before the first screen is displayed (no flash of untranslated keys).
    provideAppInitializer(() => inject(LanguageService).init()),
    // index.html loads the "Material Symbols Outlined" icon font: make it the default for <mat-icon>.
    { provide: MAT_ICON_DEFAULT_OPTIONS, useValue: { fontSet: 'material-symbols-outlined' } },
  ],
};
