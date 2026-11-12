import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    title: 'Home | Bank Clients',
    // Lazy loading: the screen's code is downloaded only when the route is visited.
    loadComponent: () => import('./features/home/home').then((m) => m.Home),
  },
  { path: '**', redirectTo: '' },
];
