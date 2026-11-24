import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { AccountResponse, AccountsService, CreateAccountRequest } from '../../../api/account';

export interface OpenAccountDialogData {
  customerId: string;
}

@Component({
  selector: 'app-open-account-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatButtonModule, MatSelectModule, TranslatePipe],
  templateUrl: './open-account-dialog.html',
  styleUrl: './open-account-dialog.scss',
})
export class OpenAccountDialog {
  private readonly accountsService = inject(AccountsService);
  private readonly dialogRef = inject<MatDialogRef<OpenAccountDialog, AccountResponse>>(MatDialogRef);
  private readonly data = inject<OpenAccountDialogData>(MAT_DIALOG_DATA);

  protected readonly accountTypes = Object.values(CreateAccountRequest.AccountTypeEnum);
  protected readonly currencies = ['EUR', 'MAD', 'USD'];
  protected readonly saving = signal(false);
  protected readonly errorKey = signal<string | undefined>(undefined);

  protected readonly form = inject(NonNullableFormBuilder).group({
    accountType: [CreateAccountRequest.AccountTypeEnum.Current, Validators.required],
    currencyCode: ['EUR', Validators.required],
  });

  protected submit(): void {
    this.saving.set(true);
    this.errorKey.set(undefined);

    this.accountsService
      .createAccount({
        createAccountRequest: { customerId: this.data.customerId, ...this.form.getRawValue() },
      })
      .subscribe({
        next: (account) => this.dialogRef.close(account),
        error: (error: HttpErrorResponse) => {
          this.saving.set(false);
          this.errorKey.set(
            error.status === 503 ? 'accounts.open.unavailable' : 'accounts.open.error',
          );
        },
      });
  }
}