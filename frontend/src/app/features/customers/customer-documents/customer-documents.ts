import { DatePipe, DecimalPipe, DOCUMENT } from '@angular/common';
import { Component, inject, input } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { DocumentResponse, DocumentsService } from '../../../api/document';
import { LanguageService } from '../../../core/i18n/language.service';
import { StatusBadge } from '../../../shared/status-badge/status-badge';

@Component({
  selector: 'app-customer-documents',
  imports: [
    DatePipe,
    DecimalPipe,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatTableModule,
    TranslatePipe,
    StatusBadge,
  ],
  templateUrl: './customer-documents.html',
  styleUrl: './customer-documents.scss',
})
export class CustomerDocuments {
  private readonly documentsService = inject(DocumentsService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly translate = inject(TranslateService);
  private readonly document = inject(DOCUMENT);
  protected readonly language = inject(LanguageService);

  readonly customerId = input.required<string>();

  protected readonly displayedColumns = [
    'documentType',
    'fileName',
    'size',
    'createdAt',
    'status',
    'actions',
  ];

  protected readonly documents = rxResource({
    params: () => ({ customerId: this.customerId() }),
    stream: ({ params }) => this.documentsService.getDocumentsByCustomerId(params),
  });

  // The download needs the bearer token, so a plain <a href> cannot be used:
  // the file is fetched through HttpClient, then handed to the browser.
  protected download(file: DocumentResponse): void {
    this.documentsService.downloadDocument({ id: file.id! }).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const link = this.document.createElement('a');
        link.href = url;
        link.download = file.fileName ?? 'document';
        link.click();
        URL.revokeObjectURL(url);
      },
      error: () =>
        this.snackBar.open(this.translate.instant('documents.download.error'), undefined, {
          duration: 6000,
        }),
    });
  }
}
