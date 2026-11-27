import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { DocumentResponse, DocumentsService } from '../../../api/document';

export interface UploadDocumentDialogData {
  customerId: string;
}

// Same limit as spring.servlet.multipart.max-file-size in the Document service.
const MAX_FILE_SIZE = 10 * 1024 * 1024;

@Component({
  selector: 'app-upload-document-dialog',
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    TranslatePipe,
  ],
  templateUrl: './upload-document-dialog.html',
  styleUrl: './upload-document-dialog.scss',
})
export class UploadDocumentDialog {
  private readonly documentsService = inject(DocumentsService);
  private readonly dialogRef =
    inject<MatDialogRef<UploadDocumentDialog, DocumentResponse>>(MatDialogRef);
  private readonly data = inject<UploadDocumentDialogData>(MAT_DIALOG_DATA);

  protected readonly documentTypes = Object.values(DocumentResponse.DocumentTypeEnum);
  protected readonly file = signal<File | undefined>(undefined);
  protected readonly tooLarge = computed(() => (this.file()?.size ?? 0) > MAX_FILE_SIZE);
  protected readonly saving = signal(false);
  protected readonly errorKey = signal<string | undefined>(undefined);

  protected readonly form = inject(NonNullableFormBuilder).group({
    documentType: [DocumentResponse.DocumentTypeEnum.Passport, Validators.required],
  });

  protected selectFile(event: Event): void {
    this.file.set((event.target as HTMLInputElement).files?.[0]);
    this.errorKey.set(undefined);
  }

  protected submit(): void {
    const file = this.file();
    if (!file || this.tooLarge()) {
      return;
    }
    this.saving.set(true);
    this.errorKey.set(undefined);

    this.documentsService
      .uploadDocument({
        customerId: this.data.customerId,
        documentType: this.form.getRawValue().documentType,
        file,
      })
      .subscribe({
        next: (document) => this.dialogRef.close(document),
        error: (error: HttpErrorResponse) => {
          this.saving.set(false);
          this.errorKey.set(
            error.status === 413 ? 'documents.upload.tooLarge' : 'documents.upload.error',
          );
        },
      });
  }
}
