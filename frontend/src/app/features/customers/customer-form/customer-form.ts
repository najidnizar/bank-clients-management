import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import {
  AbstractControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterLink } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { CustomersService } from '../../../api/onboarding';

// Used for both /customers/new and /customers/:id/edit.
@Component({
  selector: 'app-customer-form',
  imports: [ReactiveFormsModule, RouterLink, MatButtonModule, MatInputModule, TranslatePipe],
  templateUrl: './customer-form.html',
  styleUrl: './customer-form.scss',
})
export class CustomerForm {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly customersService = inject(CustomersService);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);
  private readonly translate = inject(TranslateService);

  protected readonly saving = signal(false);
  protected readonly serverError = signal<string | undefined>(undefined);

  readonly id = input<string>();
  protected readonly isEdit = computed(() => !!this.id());

  // Same rules as the back-end DTOs.
  protected readonly form = this.formBuilder.group({
    firstName: ['', [Validators.required, Validators.maxLength(100)]],
    lastName: ['', [Validators.required, Validators.maxLength(100)]],
    dateOfBirth: ['', [Validators.required, adultValidator]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
    phoneNumber: ['', [Validators.required]],
  });

  private readonly customer = rxResource({
    params: () => (this.id() ? { id: this.id()! } : undefined),
    stream: ({ params }) => this.customersService.getCustomerById(params),
  });

  constructor() {
    effect(() => {
      if (this.customer.hasValue()) {
        this.form.patchValue(this.customer.value());
      }
    });
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.serverError.set(undefined);

    const id = this.id();
    const request = this.form.getRawValue();
    const save$ = id
      ? this.customersService.updateCustomer({ id, updateCustomerRequest: request })
      : this.customersService.createCustomer({ createCustomerRequest: request });
    const messages = id ? 'customers.edit' : 'customers.create';

    save$.subscribe({
      next: (customer) => {
        this.snackBar.open(
          this.translate.instant(`${messages}.success`, { number: customer.customerNumber }),
          undefined,
          { duration: 4000 },
        );
        this.router.navigate(['/customers', customer.id]);
      },
      error: (error: HttpErrorResponse) => {
        this.saving.set(false);
        if (error.status === 409) {
          this.form.controls.email.setErrors({ alreadyUsed: true });
          this.form.controls.email.markAsTouched();
        } else if (error.status === 400) {
          this.serverError.set('customers.create.invalid');
        } else {
          this.serverError.set(`${messages}.error`);
        }
      },
    });
  }
}

function adultValidator(control: AbstractControl<string>): ValidationErrors | null {
  if (!control.value) {
    return null;
  }
  const birth = new Date(control.value);
  const adultFrom = new Date(birth.getFullYear() + 18, birth.getMonth(), birth.getDate());
  return adultFrom <= new Date() ? null : { underage: true };
}
