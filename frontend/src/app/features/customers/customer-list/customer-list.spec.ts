import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideTranslateTesting } from '../../../../testing/translate-testing';
import { CustomerList } from './customer-list';
import { provideRouter } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

describe('CustomerList', () => {
  it('should create', () => {
    TestBed.configureTestingModule({
      imports: [CustomerList],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideTranslateTesting(),
        provideRouter([]),
        { provide: AuthService, useValue: { hasRole: () => true } },
      ],
    });
    const fixture = TestBed.createComponent(CustomerList);
    expect(fixture.componentInstance).toBeTruthy();
  });
});