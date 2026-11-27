import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideTranslateTesting } from '../../../../testing/translate-testing';
import { provideApi } from '../../../api/document';
import { CustomerDocuments } from './customer-documents';

describe('CustomerDocuments', () => {
  let httpMock: HttpTestingController;

  function create() {
    TestBed.configureTestingModule({
      imports: [CustomerDocuments],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideTranslateTesting(),
        provideApi(''),
      ],
    });
    httpMock = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(CustomerDocuments);
    fixture.componentRef.setInput('customerId', 'customer-1');
    TestBed.tick();
    return fixture;
  }

  function text(fixture: { nativeElement: HTMLElement }): string {
    return fixture.nativeElement.textContent ?? '';
  }

  function expectList() {
    return httpMock.expectOne(
      (request) =>
        request.url === '/api/v1/documents' && request.params.get('customerId') === 'customer-1',
    );
  }

  afterEach(() => httpMock.verify());

  it('lists the documents of the customer', async () => {
    const fixture = create();
    expectList().flush([
      {
        id: 'd1',
        documentType: 'PASSPORT',
        fileName: 'passport.pdf',
        fileSize: 2048,
        status: 'UPLOADED',
        createdAt: '2026-11-20T10:00:00',
      },
    ]);
    await fixture.whenStable();

    expect(text(fixture)).toContain('passport.pdf');
    expect(text(fixture)).toContain('Passport');
    expect(text(fixture)).toContain('Uploaded');
  });

  it('says when the customer has no document', async () => {
    const fixture = create();
    expectList().flush([]);
    await fixture.whenStable();

    expect(text(fixture)).toContain('No document yet.');
  });
});
