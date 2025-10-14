import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../services/account';
import { Account } from '../models/account';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './accounts.html',
  styleUrls: ['./accounts.css']
})
export class AccountsComponent implements OnInit {
  accounts: Account[] = [];
  selectedAccount: Account | null = null;
  showCreateForm: boolean = false;
  showEditForm: boolean = false;
  showDeleteModal: boolean = false;
  showDepositForm: boolean = false;
  showWithdrawForm: boolean = false;

  // Form data
  newAccount: Partial<Account> = {
    type: 'SAVINGS',
    balance: 0
  };

  editAccount: Partial<Account> = {};
  transactionAmount: number = 0;

  // Loading states
  loading: boolean = false;
  actionLoading: boolean = false;

  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.loading = true;
    this.accountService.getAccounts().subscribe({
      next: (data: Account[]) => {
        this.accounts = data;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error fetching accounts:', err);
        this.loading = false;
      },
    });
  }

  openCreateForm(): void {
    this.newAccount = { type: 'SAVINGS', balance: 0 };
    this.showCreateForm = true;
  }

  closeCreateForm(): void {
    this.showCreateForm = false;
    this.newAccount = { type: 'SAVINGS', balance: 0 };
  }

  createAccount(): void {
    if (!this.newAccount.type) return;

    this.actionLoading = true;
    this.accountService.createAccount(this.newAccount).subscribe({
      next: (account: Account) => {
        this.accounts.push(account);
        this.closeCreateForm();
        this.actionLoading = false;
      },
      error: (error: any) => {
        console.error('Error creating account:', error);
        this.actionLoading = false;
      }
    });
  }

  openEditForm(account: Account): void {
    this.selectedAccount = account;
    this.editAccount = { ...account };
    this.showEditForm = true;
  }

  closeEditForm(): void {
    this.showEditForm = false;
    this.selectedAccount = null;
    this.editAccount = {};
  }

  updateAccount(): void {
    if (!this.selectedAccount || !this.editAccount.accountNumber) return;

    this.actionLoading = true;
    this.accountService.updateAccount(this.selectedAccount.accountNumber, this.editAccount).subscribe({
      next: () => {
        const index = this.accounts.findIndex(acc => acc.id === this.selectedAccount!.id);
        if (index !== -1) {
          this.accounts[index] = { ...this.selectedAccount!, ...this.editAccount };
        }
        this.closeEditForm();
        this.actionLoading = false;
      },
      error: (error: any) => {
        console.error('Error updating account:', error);
        this.actionLoading = false;
      }
    });
  }

  openDeleteModal(account: Account): void {
    this.selectedAccount = account;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.selectedAccount = null;
  }

  deleteAccount(): void {
    if (!this.selectedAccount) return;

    this.actionLoading = true;
    this.accountService.closeAccount(this.selectedAccount.accountNumber).subscribe({
      next: () => {
        this.accounts = this.accounts.filter(acc => acc.id !== this.selectedAccount!.id);
        this.closeDeleteModal();
        this.actionLoading = false;
      },
      error: (error: any) => {
        console.error('Error deleting account:', error);
        this.actionLoading = false;
      }
    });
  }

  openDepositForm(account: Account): void {
    this.selectedAccount = account;
    this.transactionAmount = 0;
    this.showDepositForm = true;
  }

  closeDepositForm(): void {
    this.showDepositForm = false;
    this.selectedAccount = null;
    this.transactionAmount = 0;
  }

  deposit(): void {
    if (!this.selectedAccount || this.transactionAmount <= 0) return;

    this.actionLoading = true;
    // For now, simulate deposit by updating balance locally
    // In real app, call service method
    this.selectedAccount.balance += this.transactionAmount;
    this.closeDepositForm();
    this.actionLoading = false;
  }

  openWithdrawForm(account: Account): void {
    this.selectedAccount = account;
    this.transactionAmount = 0;
    this.showWithdrawForm = true;
  }

  closeWithdrawForm(): void {
    this.showWithdrawForm = false;
    this.selectedAccount = null;
    this.transactionAmount = 0;
  }

  withdraw(): void {
    if (!this.selectedAccount || this.transactionAmount <= 0) return;

    if (this.transactionAmount > this.selectedAccount.balance) {
      alert('Insufficient balance');
      return;
    }

    this.actionLoading = true;
    // For now, simulate withdrawal by updating balance locally
    // In real app, call service method
    this.selectedAccount.balance -= this.transactionAmount;
    this.closeWithdrawForm();
    this.actionLoading = false;
  }

  getTotalBalance(): number {
    return this.accounts.reduce((total, account) => total + account.balance, 0);
  }

  getUniqueAccountTypes(): number {
    const types = new Set(this.accounts.map(account => account.type));
    return types.size;
  }

  getAccountStatusColor(status: string): string {
    switch (status?.toLowerCase()) {
      case 'active': return '#10b981';
      case 'inactive': return '#f59e0b';
      case 'closed': return '#ef4444';
      case 'frozen': return '#6b7280';
      default: return '#6b7280';
    }
  }
}
