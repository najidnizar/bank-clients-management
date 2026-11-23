import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateTesting } from '../../../../testing/translate-testing';
import { CustomerDetail } from './customer-detail';
import { AuthService } from './../../../core/auth/auth.service';

describe('CustomerDetail', () => {
  it('should create', () => {
    TestBed.configureTestingModule({
      imports: [CustomerDetail],
      providers: [
        provideRouter([]), 
        provideTranslateTesting(), 
        provideHttpClient(), 
        provideHttpClientTesting(),
        { provide: AuthService, useValue: { hasRole: () => true } },
      ],
    });
    const fixture = TestBed.createComponent(CustomerDetail);
    fixture.componentRef.setInput('id', 'some-id'); // a required input must be set before rendering
    expect(fixture.componentInstance).toBeTruthy();
  });
});