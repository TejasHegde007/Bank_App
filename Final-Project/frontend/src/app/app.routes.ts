import { Routes } from '@angular/router';
import { LoginComponent } from './login/login';
import { Dashboard } from './dashboard/dashboard';
import { Register } from './register/register';
import { Transactions } from './transactions/transactions';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'register', component: Register },
  { path: 'dashboard', component: Dashboard },
  { path: 'transactions', component: Transactions },
  { path: 'transaction', component: Transactions },
];
