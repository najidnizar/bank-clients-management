import { Routes } from '@angular/router';
import { roleGuard } from './core/auth/role.guard'
 
export const routes: Routes = [
  {
    path: '',
    title: 'titles.home', // translation key, see TranslatedTitleStrategy
    // Lazy loading: the screen's code is downloaded only when the route is visited.
    loadComponent: () => import('./features/home/home').then((m) => m.Home),
  },
  {
    path: 'customers/new',
    title: 'titles.newCustomer',
    canActivate: [roleGuard('ADVISOR')],
    loadComponent: () =>
      import('./features/customers/customer-create/customer-create').then((m) => m.CustomerCreate),
  },
  {
    path: 'customers',
    title: 'titles.customers',
    loadComponent: () =>
      import('./features/customers/customer-list/customer-list').then((m) => m.CustomerList),
  },
  { path: '**', redirectTo: '' },
];
