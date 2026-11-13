import { Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { CustomersService } from '../../../api/onboarding';

@Component({
  selector: 'app-customer-list',
  imports: [MatTableModule, MatPaginatorModule, MatProgressBarModule, TranslatePipe],
  templateUrl: './customer-list.html',
  styleUrl: './customer-list.scss',
})
export class CustomerList {
  private readonly customersService = inject(CustomersService);

  protected readonly displayedColumns = ['customerNumber', 'name', 'email', 'status'];
  protected readonly pageIndex = signal(0);
  protected readonly pageSize = signal(10);

  // Reloads automatically whenever pageIndex or pageSize changes (they are read in `params`).
  protected readonly customers = rxResource({
    params: () => ({
      page: this.pageIndex(),
      size: this.pageSize(),
      sort: ['customerNumber,asc'], // always sort when paginating: SQL order is not guaranteed otherwise
    }),
    stream: ({ params }) => this.customersService.getCustomers(params),
  });

  protected onPage(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
  }
}