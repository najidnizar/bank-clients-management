import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateTesting } from '../../../../testing/translate-testing';
import { CustomerCreate } from './customer-create';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('CustomerCreate', () => {
  it('should create', () => {
    TestBed.configureTestingModule({
      imports: [CustomerCreate],
      providers: [provideRouter([]), provideTranslateTesting(), provideHttpClient(), provideHttpClientTesting()],
    });
    expect(TestBed.createComponent(CustomerCreate).componentInstance).toBeTruthy();
  });
});