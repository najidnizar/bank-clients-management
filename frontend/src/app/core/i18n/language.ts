/** Languages offered by the application; translation files live in public/i18n/<language>.json. */
export const LANGUAGES = ['en', 'fr'] as const;

export type Language = (typeof LANGUAGES)[number];

export const DEFAULT_LANGUAGE: Language = 'en';

export function isLanguage(value: unknown): value is Language {
  return LANGUAGES.includes(value as Language);
}
