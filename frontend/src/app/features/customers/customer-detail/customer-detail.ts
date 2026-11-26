import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, input, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RouterLink } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { CustomersService } from '../../../api/onboarding';
import { AuthService } from '../../../core/auth/auth.service';
import { LanguageService } from '../../../core/i18n/language.service';
import { ConfirmDialog, ConfirmDialogData } from '../../../shared/confirm-dialog/confirm-dialog';
import { StatusBadge } from '../../../shared/status-badge/status-badge';
import { CustomerAccounts } from '../customer-accounts/customer-accounts';
import { CustomerKyc } from '../customer-kyc/customer-kyc';

type CustomerAction = 'block' | 'unblock' | 'close';

@Component({
  selector: 'app-customer-detail',
  imports: [
    RouterLink,
    DatePipe,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressBarModule,
    TranslatePipe,
    StatusBadge,
    CustomerKyc,
    CustomerAccounts,
  ],
  templateUrl: './customer-detail.html',
  styleUrl: './customer-detail.scss',
})
export class CustomerDetail {
  private readonly customersService = inject(CustomersService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly translate = inject(TranslateService);
  protected readonly auth = inject(AuthService);
  protected readonly language = inject(LanguageService);

  protected readonly busy = signal(false);

  readonly id = input.required<string>();

  protected readonly customer = rxResource({
    params: () => ({ id: this.id() }),
    stream: ({ params }) => this.customersService.getCustomerById(params),
  });

  protected changeStatus(action: CustomerAction): void {
    const customer = this.customer.value();
    if (!customer) {
      return;
    }
    this.dialog
      .open<ConfirmDialog, ConfirmDialogData, boolean>(ConfirmDialog, {
        data: {
          title: `customers.actions.${action}.title`,
          message: `customers.actions.${action}.message`,
          confirm: `customers.actions.${action}.confirm`,
          params: { name: `${customer.firstName} ${customer.lastName}` },
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (confirmed) {
          this.applyStatusAction(action);
        }
      });
  }

  private applyStatusAction(action: CustomerAction): void {
    const request = { id: this.id() };
    const calls: Record<CustomerAction, Observable<unknown>> = {
      block: this.customersService.blockCustomer(request),
      unblock: this.customersService.unblockCustomer(request),
      close: this.customersService.closeCustomer(request),
    };

    this.busy.set(true);
    calls[action].subscribe({
      next: () => {
        this.busy.set(false);
        this.snackBar.open(this.translate.instant(`customers.actions.${action}.done`), undefined, {
          duration: 4000,
        });
        this.customer.reload();
      },
      error: (error: HttpErrorResponse) => {
        this.busy.set(false);
        const key =
          error.status === 409 ? 'customers.actions.notAllowed' : 'customers.actions.error';
        this.snackBar.open(this.translate.instant(key), undefined, { duration: 6000 });
      },
    });
  }
}
