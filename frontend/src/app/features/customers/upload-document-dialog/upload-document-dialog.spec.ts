import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { provideTranslateTesting } from '../../../../testing/translate-testing';
import { provideApi } from '../../../api/document';
import { UploadDocumentDialog } from './upload-document-dialog';

describe('UploadDocumentDialog', () => {
  let httpMock: HttpTestingController;
  const dialogRef = { close: vi.fn() };

  async function create() {
    TestBed.configureTestingModule({
      imports: [UploadDocumentDialog],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideTranslateTesting(),
        provideApi(''),
        { provide: MAT_DIALOG_DATA, useValue: { customerId: 'customer-1' } },
        { provide: MatDialogRef, useValue: dialogRef },
      ],
    });
    httpMock = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(UploadDocumentDialog);
    await fixture.whenStable();
    return fixture;
  }

  // jsdom cannot open a file picker: the chosen file is set on the input directly.
  async function chooseFile(
    fixture: { nativeElement: HTMLElement; whenStable(): Promise<unknown> },
    file: File,
  ) {
    const input = fixture.nativeElement.querySelector<HTMLInputElement>('input[type=file]')!;
    Object.defineProperty(input, 'files', { value: [file] });
    input.dispatchEvent(new Event('change'));
    await fixture.whenStable();
  }

  function submitButton(fixture: { nativeElement: HTMLElement }): HTMLButtonElement {
    return fixture.nativeElement.querySelector('button[type=submit]')!;
  }

  afterEach(() => {
    httpMock.verify();
    dialogRef.close.mockClear();
  });

  it('uploads the chosen file with its customer and type', async () => {
    const fixture = await create();
    await chooseFile(fixture, new File(['pdf'], 'passport.pdf', { type: 'application/pdf' }));
    submitButton(fixture).click();

    const request = httpMock.expectOne((req) => req.url === '/api/v1/documents');
    expect(request.request.method).toBe('POST');
    expect(request.request.params.get('customerId')).toBe('customer-1');
    expect(request.request.params.get('documentType')).toBe('PASSPORT');
    expect(((request.request.body as FormData).get('file') as File).name).toBe('passport.pdf');
    request.flush({ id: 'd1', fileName: 'passport.pdf' });

    expect(dialogRef.close).toHaveBeenCalledWith({ id: 'd1', fileName: 'passport.pdf' });
  });

  it('refuses a file larger than 10 MB without calling the API', async () => {
    const fixture = await create();
    const file = new File(['x'], 'scan.pdf');
    Object.defineProperty(file, 'size', { value: 11 * 1024 * 1024 });
    await chooseFile(fixture, file);

    expect(submitButton(fixture).disabled).toBe(true);
    expect(fixture.nativeElement.textContent).toContain('The file is larger than 10 MB.');
  });
});
