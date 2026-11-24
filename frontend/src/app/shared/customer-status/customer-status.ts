import { Component, input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { CustomerResponse } from '../../api/onboarding';

@Component({
  selector: 'app-customer-status',
  imports: [TranslatePipe],
  template: `
    @if (status(); as status) {
      <span class="badge" [class]="status.toLowerCase()">
        {{ 'customers.status.' + status | translate }}
      </span>
    }
  `,
  styles: `
    .badge {
      padding: 2px 10px;
      border-radius: 12px;
      font: var(--mat-sys-label-medium);
    }
    .active {
      background: var(--mat-sys-primary-container);
      color: var(--mat-sys-on-primary-container);
    }
    .blocked {
      background: var(--mat-sys-error-container);
      color: var(--mat-sys-on-error-container);
    }
    .closed {
      background: var(--mat-sys-surface-variant);
      color: var(--mat-sys-on-surface-variant);
    }
  `,
})
export class CustomerStatus {
  readonly status = input<CustomerResponse.StatusEnum>();
}
