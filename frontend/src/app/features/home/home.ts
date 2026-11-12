import { Component, inject } from '@angular/core';
import { httpResource } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-home',
  imports: [MatCardModule, MatIconModule, TranslatePipe],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  protected readonly auth = inject(AuthService);

  // Authenticated call through the API gateway: checks the whole chain
  // (proxy -> gateway -> onboarding service) and that the interceptor adds the token.
  protected readonly apiStatus = httpResource.text(() => '/api/v1/customers/ping');
}
