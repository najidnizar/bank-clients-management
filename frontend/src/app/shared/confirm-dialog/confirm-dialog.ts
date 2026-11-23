import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';

/** What the confirmation dialog shows: translation keys, and the parameters of the message. */
export interface ConfirmDialogData {
  title: string;
  message: string;
  confirm: string;
  params?: Record<string, string>;
}

/** Generic "Are you sure?" dialog. Closes with true (confirmed) or false (cancelled). */
@Component({
  selector: 'app-confirm-dialog',
  imports: [MatDialogModule, MatButtonModule, TranslatePipe],
  template: `
    <h2 mat-dialog-title>{{ data.title | translate }}</h2>
    <mat-dialog-content>{{ data.message | translate: data.params }}</mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="false">{{ 'common.cancel' | translate }}</button>
      <button mat-flat-button [mat-dialog-close]="true">{{ data.confirm | translate }}</button>
    </mat-dialog-actions>
  `,
})
export class ConfirmDialog {
  protected readonly data = inject<ConfirmDialogData>(MAT_DIALOG_DATA);
}