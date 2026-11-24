import { CurrencyPipe } from '@angular/common';
import { Component, inject, input } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { AccountsService } from '../../../api/account';
import { LanguageService } from '../../../core/i18n/language.service';
import { StatusBadge } from '../../../shared/status-badge/status-badge';

@Component({
  selector: 'app-customer-accounts',
  imports: [CurrencyPipe, MatTableModule, MatProgressBarModule, TranslatePipe, StatusBadge],
  templateUrl: './customer-accounts.html',
  styleUrl: './customer-accounts.scss',
})
export class CustomerAccounts {
  private readonly accountsService = inject(AccountsService);
  protected readonly language = inject(LanguageService);

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
}
