import { Component, inject } from '@angular/core';
import {
  AbstractControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-customer-create',
  imports: [ReactiveFormsModule, RouterLink, MatButtonModule, MatInputModule, TranslatePipe],
  templateUrl: './customer-create.html',
  styleUrl: './customer-create.scss',
})
export class CustomerCreate {
  private readonly formBuilder = inject(NonNullableFormBuilder);

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
      this.form.markAllAsTouched(); // shows every error at once
      return;
    }
    console.log(this.form.getRawValue()); // next step (F3b): send it to the API
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