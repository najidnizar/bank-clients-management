import { Injectable, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Title } from '@angular/platform-browser';
import { RouterStateSnapshot, TitleStrategy } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

/**
 * Browser tab title: route `title` values are translation keys ("titles.home"),
 * translated and suffixed with the application name, and updated when the language changes.
 */
@Injectable({ providedIn: 'root' })
export class TranslatedTitleStrategy extends TitleStrategy {
  private readonly title = inject(Title);
  private readonly translate = inject(TranslateService);
  private titleKey: string | undefined;

  constructor() {
    super();
    this.translate.onLangChange.pipe(takeUntilDestroyed()).subscribe(() => this.applyTitle());
  }

  override updateTitle(snapshot: RouterStateSnapshot): void {
    this.titleKey = this.buildTitle(snapshot);
    this.applyTitle();
  }

  private applyTitle(): void {
    const appName = this.translate.instant('app.name');
    this.title.setTitle(
      this.titleKey ? `${this.translate.instant(this.titleKey)} | ${appName}` : appName,
    );
  }
}
