import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, input } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { KycCaseResponse, KycCasesService } from '../../../api/kyc';
import { LanguageService } from '../../../core/i18n/language.service';
import { ConfirmDialog, ConfirmDialogData } from '../../../shared/confirm-dialog/confirm-dialog';
import { StatusBadge } from '../../../shared/status-badge/status-badge';

type KycStatus = KycCaseResponse.StatusEnum;

const NEXT_STATUSES: Record<KycStatus, KycStatus[]> = {
  NOT_STARTED: ['IN_PROGRESS'],
  IN_PROGRESS: ['PENDING_REVIEW'],
  PENDING_REVIEW: ['APPROVED', 'REJECTED'],
  APPROVED: [],
  REJECTED: [],
  EXPIRED: [],
};

@Component({
  selector: 'app-customer-kyc',
  imports: [
    DatePipe,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatMenuModule,
    TranslatePipe,
    StatusBadge,
  ],
  templateUrl: './customer-kyc.html',
  styleUrl: './customer-kyc.scss',
})
export class CustomerKyc {
  private readonly kycCasesService = inject(KycCasesService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly translate = inject(TranslateService);
  protected readonly language = inject(LanguageService);

  readonly customerId = input.required<string>();
  readonly canReview = input(false);

  protected readonly kycCase = rxResource({
    params: () => ({ customerId: this.customerId() }),
    stream: ({ params }) => this.kycCasesService.getKycCaseByCustomerId(params),
  });

  // The case is created asynchronously from a Kafka event: a 404 just means "not yet".
  protected readonly notCreatedYet = computed(
    () => (this.kycCase.error() as HttpErrorResponse | undefined)?.status === 404,
  );

  protected nextStatuses(kycCase: KycCaseResponse): KycStatus[] {
    return kycCase.status ? NEXT_STATUSES[kycCase.status] : [];
  }

  protected changeStatus(kycCase: KycCaseResponse, status: KycStatus): void {
    this.dialog
      .open<ConfirmDialog, ConfirmDialogData, boolean>(ConfirmDialog, {
        data: {
          title: 'kyc.manage.title',
          message: 'kyc.manage.message',
          confirm: 'kyc.manage.confirm',
          params: { status: this.translate.instant(`kyc.status.${status}`) },
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (confirmed) {
          this.saveStatus(kycCase.id!, status);
        }
      });
  }

  private saveStatus(id: string, status: KycStatus): void {
    this.kycCasesService.updateKycStatus({ id, status }).subscribe({
      next: () => {
        this.snackBar.open(this.translate.instant('kyc.manage.done'), undefined, {
          duration: 4000,
        });
        this.kycCase.reload();
      },
      error: (error: HttpErrorResponse) => {
        const key = error.status === 409 ? 'kyc.manage.notAllowed' : 'kyc.manage.error';
        this.snackBar.open(this.translate.instant(key), undefined, { duration: 6000 });
      },
    });
  }
}
