import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateTesting } from '../../../../testing/translate-testing';
import { CustomerForm } from './customer-form';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('CustomerForm', () => {
  it('should create', () => {
    TestBed.configureTestingModule({
      imports: [CustomerForm],
      providers: [
        provideRouter([]),
        provideTranslateTesting(),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    expect(TestBed.createComponent(CustomerForm).componentInstance).toBeTruthy();
  });
});
