import { Component, OnInit } from '@angular/core';
import { AccountService } from '../services/account';
import { Account } from '../models/account';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-account-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './account-management.html',
  styleUrls: ['./account-management.css']
})
export class AccountManagementComponent implements OnInit {
  accounts: Account[] = [];
  selectedAccount: Account | null = null;
  showCreateForm: boolean = false;
  showDetailsModal: boolean = false;
  showCloseModal: boolean = false;

  // Form data
  newAccount: Partial<Account> = {
    type: 'SAVINGS',
    balance: 0
  };

  // Filters and search
  searchTerm: string = '';
  typeFilter: string = 'all';
  statusFilter: string = 'all';

  // Loading states
  loading: boolean = false;
  actionLoading: boolean = false;

  // Show API results/errors in UI
  createResponse: any = null;
  createError: any = null;

  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    // Avoid SSR 401s: only call APIs in browser
    if (typeof window !== 'undefined') {
      this.loadAccounts();
    }
  }

  loadAccounts(): void {
    // Read accounts from localStorage.account_detail instead of calling API
    this.loading = false; // no loading UI; we'll show empty state when there are no accounts

    try {
      const raw = (typeof window !== 'undefined') ? localStorage.getItem('account_detail') : null;
      if (raw) {
        console.log('Raw localStorage.account_detail:', raw);
        const parsed = JSON.parse(raw);

        // 1) If it's already an array, use it
        if (Array.isArray(parsed)) {
          this.accounts = parsed as Account[];
        }
        // 2) If it has an `accounts` property that's an array, use that
        else if (parsed && Array.isArray((parsed as any).accounts)) {
          this.accounts = (parsed as any).accounts as Account[];
        }
        // 3) If it's an object with numeric keys like {"0": {...}, "1": {...}},
        //    convert to an array preserving numeric order
        else if (parsed && typeof parsed === 'object') {
          const keys = Object.keys(parsed);
          const allNumeric = keys.length > 0 && keys.every(k => String(Number(k)) === k);
          if (allNumeric) {
            this.accounts = keys
              .map(k => ({ key: Number(k), val: (parsed as any)[k] }))
              .sort((a, b) => a.key - b.key)
              .map(x => x.val as Account);
          } else {
            // 4) Maybe it's an object whose values are account objects (non-numeric keys)
            //    e.g., { acctA: {...}, acctB: {...} } -> take values that look like accounts
            const values = Object.values(parsed).filter(v => v && typeof v === 'object');
            if (values.length > 0 && values.every(v => (v as any).accountNumber || (v as any).type)) {
              this.accounts = values as Account[];
            }
            // 5) If it's a single account-like object, wrap it
            else if ((parsed as any).accountNumber || (parsed as any).type) {
              this.accounts = [parsed as Account];
            } else {
              this.accounts = [];
            }
          }
        } else {
          this.accounts = [];
        }
      } else {
        this.accounts = [];
      }
    } catch (err) {
      console.error('Error parsing localStorage.account_detail', err);
      this.accounts = [];
    }
  }

  get filteredAccounts(): Account[] {
    return this.accounts.filter(account => {
      const matchesSearch = !this.searchTerm ||
        account.accountNumber.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        account.type.toLowerCase().includes(this.searchTerm.toLowerCase());

      const matchesType = this.typeFilter === 'all' || account.type === this.typeFilter;

      return matchesSearch && matchesType;
    });
  }

  getAccountSummary(): any {
    const totalAccounts = this.accounts.length;
    const totalBalance = this.accounts.reduce((sum, acc) => sum + acc.balance, 0);
    const accountTypes = this.accounts.reduce((types, acc) => {
      types[acc.type] = (types[acc.type] || 0) + 1;
      return types;
    }, {} as Record<string, number>);

    return { totalAccounts, totalBalance, accountTypes };
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

    // Extra console logging to cross-check inputs at component level
    console.log('[Component.createAccount] newAccount input:', this.newAccount);
    console.log('[Component.createAccount] type (raw):', this.newAccount.type, 'initialBalance (raw):', this.newAccount.balance);

    // Validate user_id presence (send raw value as-is)
    const user = (localStorage.getItem('user') || null);
    console.log('localStorage.user:', user);
    const userid = user ? JSON.parse(user).userId : null;
    console.log('--------------------------',userid);
    
    if (!userid) {
      const validationError = {
        message: 'Missing user_id in localStorage.',
        userId: userid
      };
      console.error('Create account validation failed:', validationError);
      this.createError = validationError;
      this.createResponse = null;
      return;
    }

    // Preview payload and URL for debugging
    const payloadPreview = {
      userId: userid,
      accountType: this.newAccount.type,
      initialBalance: this.newAccount.balance ?? 0
    };
    const urlPreview = 'http://localhost:8082/api/accounts';
    // Console userId and Authorization header preview as requested
    console.log('Create account userId (from localStorage, raw):', userid);
    const tokenPreview = (typeof window !== 'undefined') ? localStorage.getItem('access_token') : null;
    console.log('Create account Authorization header (preview):', tokenPreview ? `Bearer ${tokenPreview}` : 'MISSING');
    console.log('Create account request:', { url: urlPreview, payload: payloadPreview });

    this.actionLoading = true;
    this.accountService.createAccount(this.newAccount).subscribe({
      next: (account: Account) => {
        console.log('Create account response:', account);
        this.createResponse = account;
        this.createError = null;
        this.accounts.push(account);
        this.closeCreateForm();
        this.actionLoading = false;
      },

      error: (error: any) => {
        // Decode HttpErrorResponse for easier debugging
        const decoded = {
          status: error?.status,
          statusText: error?.statusText,
          url: error?.url,
          message: error?.message,
          // Attempt to stringify backend error body (may be text/html or JSON)
          errorBody: typeof error?.error === 'string' ? error.error : (error?.error ? JSON.stringify(error.error) : null),
          request: { url: urlPreview, payload: payloadPreview }
        };
        console.error('Error creating account (decoded):', decoded);
        this.createError = decoded;
        this.createResponse = null;
        this.actionLoading = false;
      }
    });
  }

  viewAccountDetails(account: Account): void {
    this.selectedAccount = account;
    this.showDetailsModal = true;
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedAccount = null;
  }

  openCloseModal(account: Account): void {
    this.selectedAccount = account;
    this.showCloseModal = true;
  }

  closeCloseModal(): void {
    this.showCloseModal = false;
    this.selectedAccount = null;
  }

  confirmCloseAccount(): void {
    if (!this.selectedAccount) return;

    this.actionLoading = true;
    this.accountService.closeAccount(this.selectedAccount.accountNumber).subscribe({
      next: () => {
        this.selectedAccount!.status = 'CLOSED';
        this.closeCloseModal();
        this.actionLoading = false;
      },
      error: (error: any) => {
        console.error('Error closing account:', error);
        this.actionLoading = false;
      }
    });
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.typeFilter = 'all';
    this.statusFilter = 'all';
  }

  getAccountTypeIcon(type: string): string {
    switch (type.toLowerCase()) {
      case 'SAVINGS': return '💰';
      case 'checking': return '🏦';
      case 'business': return '💼';
      default: return '💳';
    }
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

  formatBalance(balance: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(balance);
  }

  objectKeys(obj: any): string[] {
    return Object.keys(obj);
  }
}
