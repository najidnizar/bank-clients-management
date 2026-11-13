import { Injectable, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { TranslateService } from '@ngx-translate/core';

/** Texts of every <mat-paginator>, translated and updated when the language changes. */
@Injectable()
export class TranslatedPaginatorIntl extends MatPaginatorIntl {
  private readonly translate = inject(TranslateService);

  constructor() {
    super();
    this.updateLabels();
    this.translate.onLangChange.pipe(takeUntilDestroyed()).subscribe(() => this.updateLabels());
  }

  // "11 – 20 of 30"
  override getRangeLabel = (page: number, pageSize: number, length: number): string => {
    if (length === 0) {
      return this.translate.instant('paginator.empty');
    }
    const start = page * pageSize + 1;
    const end = Math.min((page + 1) * pageSize, length);
    return this.translate.instant('paginator.range', { start, end, length });
  };

  private updateLabels(): void {
    this.itemsPerPageLabel = this.translate.instant('paginator.itemsPerPage');
    this.nextPageLabel = this.translate.instant('paginator.nextPage');
    this.previousPageLabel = this.translate.instant('paginator.previousPage');
    this.firstPageLabel = this.translate.instant('paginator.firstPage');
    this.lastPageLabel = this.translate.instant('paginator.lastPage');
    this.changes.next(); // tells every paginator to re-render its texts
  }
}