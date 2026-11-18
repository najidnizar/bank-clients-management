import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateTesting } from '../../../../testing/translate-testing';
import { CustomerCreate } from './customer-create';

describe('CustomerCreate', () => {
  it('should create', () => {
    TestBed.configureTestingModule({
      imports: [CustomerCreate],
      providers: [provideRouter([]), provideTranslateTesting()],
    });
    expect(TestBed.createComponent(CustomerCreate).componentInstance).toBeTruthy();
  });
});