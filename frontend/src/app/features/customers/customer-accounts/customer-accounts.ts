import { CurrencyPipe } from '@angular/common';
import { Component, inject, input } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { AccountResponse, AccountsService } from '../../../api/account';
import { LanguageService } from '../../../core/i18n/language.service';
import { StatusBadge } from '../../../shared/status-badge/status-badge';
import {
  OpenAccountDialog,
  OpenAccountDialogData,
} from '../open-account-dialog/open-account-dialog';

@Component({
  selector: 'app-customer-accounts',
  imports: [
    CurrencyPipe,
    MatButtonModule,
    MatTableModule,
    MatProgressBarModule,
    TranslatePipe,
    StatusBadge,
  ],
  templateUrl: './customer-accounts.html',
  styleUrl: './customer-accounts.scss',
})
export class CustomerAccounts {
  private readonly accountsService = inject(AccountsService);
  protected readonly language = inject(LanguageService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly translate = inject(TranslateService);

  readonly canOpenAccount = input(false);
  readonly customerId = input.required<string>();

  protected readonly displayedColumns = ['accountNumber', 'accountType', 'status', 'balance'];

  // A customer only has a few accounts: a single page is enough.
  protected readonly accounts = rxResource({
    params: () => ({
      customerId: this.customerId(),
      pageable: { page: 0, size: 50, sort: ['createdAt,asc'] },
    }),
    stream: ({ params }) => this.accountsService.getAccountsByCustomerId(params),
  });

  protected openAccount(): void {
    this.dialog
      .open<OpenAccountDialog, OpenAccountDialogData, AccountResponse>(OpenAccountDialog, {
        data: { customerId: this.customerId() },
      })
      .afterClosed()
      .subscribe((account) => {
        if (account) {
          this.snackBar.open(
            this.translate.instant('accounts.open.success', { number: account.accountNumber }),
            undefined,
            { duration: 4000 },
          );
          this.accounts.reload();
        }
      });
  }
}
