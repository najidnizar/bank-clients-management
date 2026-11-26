import { Component, input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

// Usage: <app-status-badge [status]="customer.status" labelPrefix="customers.status." />
@Component({
  selector: 'app-status-badge',
  imports: [TranslatePipe],
  template: `
    @if (status(); as status) {
      <span class="badge" [class]="status.toLowerCase()">
        {{ labelPrefix() + status | translate }}
      </span>
    }
  `,
  styles: `
    .badge {
      padding: 2px 10px;
      border-radius: 12px;
      font: var(--mat-sys-label-medium);
    }
    .active,
    .approved {
      background: var(--mat-sys-primary-container);
      color: var(--mat-sys-on-primary-container);
    }
    .pending,
    .in_progress,
    .pending_review {
      background: var(--mat-sys-tertiary-container);
      color: var(--mat-sys-on-tertiary-container);
    }
    .blocked,
    .rejected {
      background: var(--mat-sys-error-container);
      color: var(--mat-sys-on-error-container);
    }
    .closed,
    .expired,
    .not_started {
      background: var(--mat-sys-surface-variant);
      color: var(--mat-sys-on-surface-variant);
    }
  `,
})
export class StatusBadge {
  readonly status = input<string>();
  readonly labelPrefix = input.required<string>();
}
