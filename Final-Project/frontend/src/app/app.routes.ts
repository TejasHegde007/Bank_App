import { Routes } from '@angular/router';
import { LoginComponent } from './login/login';
import { Dashboard } from './dashboard/dashboard';
import { Register } from './register/register';
import { Transactions } from './transactions/transactions';
import { AccountManagementComponent } from './account-management/account-management';
import { LoanManagementComponent } from './loan-management/loan-management';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'register', component: Register },
  { path: 'dashboard', component: Dashboard },
  { path: 'transactions', component: Transactions },
  { path: 'transaction', component: Transactions },
  { path: 'account-management', component: AccountManagementComponent },
  { path: 'loan-management', component: LoanManagementComponent },
  { path: 'accounts', redirectTo: 'account-management' },
  { path: 'loans', redirectTo: 'loan-management' },
];
