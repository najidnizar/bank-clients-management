import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideTranslateTesting } from '../../../../testing/translate-testing';
import { CustomerList } from './customer-list';

describe('CustomerList', () => {
  it('should create', () => {
    TestBed.configureTestingModule({
      imports: [CustomerList],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideTranslateTesting()],
    });
    const fixture = TestBed.createComponent(CustomerList);
    expect(fixture.componentInstance).toBeTruthy();
  });
});