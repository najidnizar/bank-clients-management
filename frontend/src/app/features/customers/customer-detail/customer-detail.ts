import { Component, inject, input } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslatePipe } from '@ngx-translate/core';
import { CustomersService } from '../../../api/onboarding';
import { CustomerStatus } from '../../../shared/customer-status/customer-status';

@Component({
  selector: 'app-customer-detail',
  imports: [
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressBarModule,
    TranslatePipe,
    CustomerStatus,
  ],
  templateUrl: './customer-detail.html',
  styleUrl: './customer-detail.scss',
})
export class CustomerDetail {
  private readonly customersService = inject(CustomersService);

  /** Filled by the router from the :id URL parameter (withComponentInputBinding). */
  readonly id = input.required<string>();

  protected readonly customer = rxResource({
    params: () => ({ id: this.id() }),
    stream: ({ params }) => this.customersService.getCustomerById(params),
  });
}