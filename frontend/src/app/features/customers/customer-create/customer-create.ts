import {
  AbstractControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';   // ajoute signal
import { Router, RouterLink } from '@angular/router';         // ajoute Router
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';   // ajoute TranslateService
import { CustomersService } from '../../../api/onboarding';

@Component({
  selector: 'app-customer-create',
  imports: [ReactiveFormsModule, RouterLink, MatButtonModule, MatInputModule, TranslatePipe],
  templateUrl: './customer-create.html',
  styleUrl: './customer-create.scss',
})
export class CustomerCreate {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly customersService = inject(CustomersService);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);
  private readonly translate = inject(TranslateService);

  protected readonly saving = signal(false);
  protected readonly serverError = signal<string | undefined>(undefined); // translation key of a form-level error

  // Same rules as CreateCustomerRequest in the back-end (which stays the reference).
  protected readonly form = this.formBuilder.group({
    firstName: ['', [Validators.required, Validators.maxLength(100)]],
    lastName: ['', [Validators.required, Validators.maxLength(100)]],
    dateOfBirth: ['', [Validators.required, adultValidator]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
    phoneNumber: ['', [Validators.required]],
  });

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.serverError.set(undefined);

    this.customersService
      .createCustomer({ createCustomerRequest: this.form.getRawValue() })
      .subscribe({
        next: (customer) => {
          this.snackBar.open(
            this.translate.instant('customers.create.success', { number: customer.customerNumber }),
            undefined,
            { duration: 4000 },
          );
          this.router.navigate(['/customers', customer.id]);
        },
        error: (error: HttpErrorResponse) => {
          this.saving.set(false);
          if (error.status === 409) {
            // Email already used by another customer: show it under the email field
            this.form.controls.email.setErrors({ alreadyUsed: true });
            this.form.controls.email.markAsTouched();
          } else if (error.status === 400) {
            this.serverError.set('customers.create.invalid');
          } else {
            this.serverError.set('customers.create.error');
          }
        },
      });
  }
}

/** Customers must be adults. Returns null when valid, an error object otherwise. */
function adultValidator(control: AbstractControl<string>): ValidationErrors | null {
  if (!control.value) {
    return null; // the empty case is handled by Validators.required
  }
  const birth = new Date(control.value);
  const adultFrom = new Date(birth.getFullYear() + 18, birth.getMonth(), birth.getDate());
  return adultFrom <= new Date() ? null : { underage: true };
}