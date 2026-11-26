import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, input } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatCardModule } from '@angular/material/card';
import { TranslatePipe } from '@ngx-translate/core';
import { KycCasesService } from '../../../api/kyc';
import { LanguageService } from '../../../core/i18n/language.service';
import { StatusBadge } from '../../../shared/status-badge/status-badge';

@Component({
  selector: 'app-customer-kyc',
  imports: [DatePipe, MatCardModule, TranslatePipe, StatusBadge],
  templateUrl: './customer-kyc.html',
  styleUrl: './customer-kyc.scss',
})
export class CustomerKyc {
  private readonly kycCasesService = inject(KycCasesService);
  protected readonly language = inject(LanguageService);

  readonly customerId = input.required<string>();

  protected readonly kycCase = rxResource({
    params: () => ({ customerId: this.customerId() }),
    stream: ({ params }) => this.kycCasesService.getKycCaseByCustomerId(params),
  });

  // The case is created asynchronously from a Kafka event: a 404 just means "not yet".
  protected readonly notCreatedYet = computed(
    () => (this.kycCase.error() as HttpErrorResponse | undefined)?.status === 404,
  );
}