import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { TranslatePipe } from '@ngx-translate/core';
import { CustomersService } from '../../../api/onboarding';

@Component({
  selector: 'app-customer-list',
  imports: [TranslatePipe],
  templateUrl: './customer-list.html',
  styleUrl: './customer-list.scss',
})
export class CustomerList {
  private readonly customersService = inject(CustomersService);

  // Calls GET /api/v1/customers?page=0&size=20 and exposes the result as signals.
  protected readonly customers = rxResource({
    stream: () => this.customersService.getCustomers({ page: 0, size: 20 }),
  });
}