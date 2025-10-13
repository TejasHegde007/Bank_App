import { Component } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, DatePipe, CurrencyPipe],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard {
  // Static demo data for a purely static dashboard
  accounts = [
    { type: 'Checking', accountNumber: 'CHK-001', balance: 2500 },
    { type: 'Savings', accountNumber: 'SAV-123', balance: 7200 },
    { type: 'Business', accountNumber: 'BUS-777', balance: 5000 },
  ];

  transactions = [
    { type: 'Credit', amount: 350, date: new Date(), fromAccount: 'Employer', toAccount: 'CHK-001' },
    { type: 'Debit', amount: 120, date: new Date(), fromAccount: 'CHK-001', toAccount: 'Utility' },
    { type: 'Debit', amount: 45, date: new Date(), fromAccount: 'CHK-001', toAccount: 'Groceries' },
  ];

  loanSummary = {
    activeLoans: 1,
    totalOutstanding: 15000,
  };

  cardSummary = {
    activeCards: 2,
    totalLimit: 8000,
  };

  // Static helpers referenced by the template
  refreshData(): void {
    // No-op for static dashboard
  }

  getTotalBalance(): number {
    return this.accounts.reduce((sum, a) => sum + (a.balance || 0), 0);
  }

  getMonthlyIncome(): number {
    // Static value for demo
    return 5200;
  }

  getMonthlyExpenses(): number {
    // Static value for demo
    return 1850;
  }

  getAccountTypesCount(): number {
    return new Set(this.accounts.map(a => a.type)).size;
  }
}
