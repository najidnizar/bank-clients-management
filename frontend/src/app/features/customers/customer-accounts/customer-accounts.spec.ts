import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideTranslateTesting } from '../../../../testing/translate-testing';
import { provideApi } from '../../../api/account';
import { CustomerAccounts } from './customer-accounts';

describe('CustomerAccounts', () => {
  let httpMock: HttpTestingController;

  function create() {
    TestBed.configureTestingModule({
      imports: [CustomerAccounts],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideTranslateTesting(),
        provideApi(''),
      ],
    });
    httpMock = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(CustomerAccounts);
    fixture.componentRef.setInput('customerId', 'customer-1');
    TestBed.tick();
    return fixture;
  }

  function text(fixture: { nativeElement: HTMLElement }): string {
    return fixture.nativeElement.textContent ?? '';
  }

  afterEach(() => httpMock.verify());

  it('lists the accounts of the customer with their balance', async () => {
    const fixture = create();
    httpMock
      .expectOne((request) => request.url === '/api/v1/accounts/customer/customer-1')
      .flush({
        content: [
          {
            id: 'a1',
            accountNumber: 'FR7612345',
            accountType: 'CURRENT',
            status: 'ACTIVE',
            balance: 1250,
            currencyCode: 'EUR',
          },
        ],
        totalElements: 1,
      });
    await fixture.whenStable();

    expect(text(fixture)).toContain('FR7612345');
    expect(text(fixture)).toContain('Current account');
    expect(text(fixture)).toContain('€1,250.00');
  });

  it('says when the customer has no account', async () => {
    const fixture = create();
    httpMock
      .expectOne((request) => request.url === '/api/v1/accounts/customer/customer-1')
      .flush({ content: [], totalElements: 0 });
    await fixture.whenStable();

    expect(text(fixture)).toContain('No account yet.');
  });
});
