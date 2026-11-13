import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    title: 'titles.home', // translation key, see TranslatedTitleStrategy
    // Lazy loading: the screen's code is downloaded only when the route is visited.
    loadComponent: () => import('./features/home/home').then((m) => m.Home),
  },
  {
    path: 'customers',
    title: 'titles.customers',
    loadComponent: () =>
      import('./features/customers/customer-list/customer-list').then((m) => m.CustomerList),
  },
  { path: '**', redirectTo: '' },
];
