import { Component, inject, signal, linkedSignal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { CustomersService, PageCustomerResponse, CustomerResponse } from '../../../api/onboarding';
import { MatSortModule, Sort, SortDirection } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { AuthService } from '../../../core/auth/auth.service';
import { RouterLink } from '@angular/router';
import { CustomerStatus } from '../../../shared/customer-status/customer-status'
 
@Component({
  selector: 'app-customer-list',
  imports: [MatTableModule, MatPaginatorModule, MatProgressBarModule, TranslatePipe, MatSortModule, MatButtonModule, MatInputModule, MatSelectModule, RouterLink, CustomerStatus ],
  templateUrl: './customer-list.html',
  styleUrl: './customer-list.scss',
})
export class CustomerList {
  private readonly customersService = inject(CustomersService);

  protected readonly displayedColumns = ['customerNumber', 'name', 'email', 'status'];
  protected readonly pageIndex = signal(0);
  protected readonly pageSize = signal(10);
  protected readonly sortActive = signal('customerNumber');
  protected readonly sortDirection = signal<SortDirection>('asc');
  protected readonly lastNameFilter = signal('');
  protected readonly statusFilter = signal<CustomerResponse.StatusEnum | ''>('');
  protected readonly statuses = Object.values(CustomerResponse.StatusEnum); // ['ACTIVE', 'BLOCKED', 'CLOSED']
  protected readonly auth = inject(AuthService);

  // Reloads automatically whenever pageIndex or pageSize changes (they are read in `params`).
  protected readonly customers = rxResource({
    params: () => ({
      pageable: {
        page: this.pageIndex(),
        size: this.pageSize(),
        sort: [`${this.sortActive()},${this.sortDirection()}`, 'customerNumber,asc'],
      },
      lastName: this.lastNameFilter() || undefined, // '' → undefined: the parameter is not sent
      status: this.statusFilter() || undefined,
    }),
    stream: ({ params }) => this.customersService.getCustomers(params),
  });

  protected readonly page = linkedSignal<PageCustomerResponse | undefined, PageCustomerResponse | undefined>({
    source: () => (this.customers.hasValue() ? this.customers.value() : undefined),
    computation: (current, previous) => current ?? previous?.value, 
  });

  protected onPage(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
  }

  protected onSort(sort: Sort): void {
    this.sortActive.set(sort.active);
    this.sortDirection.set(sort.direction);
    this.pageIndex.set(0); // a new order makes the current page meaningless
  }

  protected search(lastName: string): void {
    this.lastNameFilter.set(lastName.trim());
    this.pageIndex.set(0);
  }

  protected filterByStatus(status: CustomerResponse.StatusEnum | ''): void {
    this.statusFilter.set(status);
    this.pageIndex.set(0);
  }
}