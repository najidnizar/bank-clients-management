import { Component, inject, signal, linkedSignal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { CustomersService, PageCustomerResponse } from '../../../api/onboarding';
import { MatSortModule, Sort, SortDirection } from '@angular/material/sort';

@Component({
  selector: 'app-customer-list',
  imports: [MatTableModule, MatPaginatorModule, MatProgressBarModule, TranslatePipe, MatSortModule],
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

  // Reloads automatically whenever pageIndex or pageSize changes (they are read in `params`).
  protected readonly customers = rxResource({
    params: () => ({
      page: this.pageIndex(),
      size: this.pageSize(),
      // Clicked column first, then customer number as a unique tie-breaker (stable pages).
      sort: [`${this.sortActive()},${this.sortDirection()}`, 'customerNumber,asc'],
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
}