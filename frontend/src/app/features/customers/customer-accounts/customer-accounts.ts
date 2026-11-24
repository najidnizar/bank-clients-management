import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, input } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { AccountResponse, AccountsService } from '../../../api/account';
import { LanguageService } from '../../../core/i18n/language.service';
import { ConfirmDialog, ConfirmDialogData } from '../../../shared/confirm-dialog/confirm-dialog';
import { StatusBadge } from '../../../shared/status-badge/status-badge';
import {
  OpenAccountDialog,
  OpenAccountDialogData,
} from '../open-account-dialog/open-account-dialog';

type AccountStatus = AccountResponse.StatusEnum;

const NEXT_STATUSES: Record<AccountStatus, AccountStatus[]> = {
  PENDING: ['ACTIVE'],
  ACTIVE: ['BLOCKED', 'CLOSED'],
  BLOCKED: ['ACTIVE', 'CLOSED'],
  CLOSED: [],
};

@Component({
  selector: 'app-customer-accounts',
  imports: [
    CurrencyPipe,
    MatButtonModule,
    MatTableModule,
    MatProgressBarModule,
    TranslatePipe,
    StatusBadge,
    MatIconModule,
    MatMenuModule,
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
  readonly canManageAccounts = input(false);
  readonly customerId = input.required<string>();

  protected readonly displayedColumns = computed(() =>
    this.canManageAccounts()
      ? ['accountNumber', 'accountType', 'status', 'balance', 'actions']
      : ['accountNumber', 'accountType', 'status', 'balance'],
  );

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

  protected nextStatuses(account: AccountResponse): AccountStatus[] {
    return account.status ? NEXT_STATUSES[account.status] : [];
  }

  protected changeStatus(account: AccountResponse, status: AccountStatus): void {
    this.dialog
      .open<ConfirmDialog, ConfirmDialogData, boolean>(ConfirmDialog, {
        data: {
          title: 'accounts.manage.title',
          message: 'accounts.manage.message',
          confirm: 'accounts.manage.confirm',
          params: {
            number: account.accountNumber ?? '',
            status: this.translate.instant(`accounts.status.${status}`),
          },
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (confirmed) {
          this.saveStatus(account.id!, status);
        }
      });
  }

  private saveStatus(id: string, status: AccountStatus): void {
    this.accountsService.updateAccountStatus({ id, status }).subscribe({
      next: () => {
        this.snackBar.open(this.translate.instant('accounts.manage.done'), undefined, {
          duration: 4000,
        });
        this.accounts.reload();
      },
      error: (error: HttpErrorResponse) => {
        const key = error.status === 409 ? 'accounts.manage.notAllowed' : 'accounts.manage.error';
        this.snackBar.open(this.translate.instant(key), undefined, { duration: 6000 });
      },
    });
  }
}
