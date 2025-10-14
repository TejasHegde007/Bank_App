import { Component, OnInit } from '@angular/core';
import { LoanService } from '../services/loan';
import { Loan, LoanEmi, LoanEligibility } from '../models/loan';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-loan-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './loan-management.html',
  styleUrls: ['./loan-management.css']
})
export class LoanManagementComponent implements OnInit {
  loans: Loan[] = [];
  selectedLoan: Loan | null = null;
  emiSchedule: LoanEmi[] = [];
  showApplyForm: boolean = false;
  showDetailsModal: boolean = false;
  showEligibilityModal: boolean = false;

  // Form data
  loanApplication: Partial<Loan> = {
    amount: 0,
    tenure: 12,
    type: 'PERSONAL'
  };

  // Eligibility check
  eligibilityCheck: LoanEligibility | null = null;

  // Filters and search
  searchTerm: string = '';
  typeFilter: string = 'all';
  statusFilter: string = 'all';

  // Loading states
  loading: boolean = false;
  actionLoading: boolean = false;

  // Calculator
  calculator: {
    principal: number;
    rate: number;
    tenure: number;
    emi: number;
    totalAmount: number;
    totalInterest: number;
  } = {
    principal: 100000,
    rate: 12,
    tenure: 12,
    emi: 0,
    totalAmount: 0,
    totalInterest: 0
  };

  constructor(private loanService: LoanService) {}

  ngOnInit(): void {
    this.loadLoans();
    this.calculateEMI();
  }

  loadLoans(): void {
    this.loading = true;
    const userIdStr = localStorage.getItem('user_id');
    const userId = userIdStr ? Number(userIdStr) : 0;
    if (!userId) {
      this.loading = false;
      return;
    }
    this.loanService.getUserLoans(userId).subscribe({
      next: (data: Loan[]) => {
        this.loans = data;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading loans:', error);
        this.loading = false;
      }
    });
  }

  get filteredLoans(): Loan[] {
    return this.loans.filter(loan => {
      const matchesSearch = !this.searchTerm ||
        loan.loanNumber.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        loan.type.toLowerCase().includes(this.searchTerm.toLowerCase());

      const matchesType = this.typeFilter === 'all' || loan.type === this.typeFilter;
      const matchesStatus = this.statusFilter === 'all' || loan.status === this.statusFilter;

      return matchesSearch && matchesType && matchesStatus;
    });
  }

  getLoanSummary(): any {
    const totalLoans = this.loans.length;
    const activeLoans = this.loans.filter(l => l.status === 'ACTIVE').length;
    const totalOutstanding = this.loans.reduce((sum, loan) => sum + loan.outstandingAmount, 0);
    const nextEmiDue = this.loans
      .filter(l => l.status === 'ACTIVE')
      .sort((a, b) => new Date(a.nextEmiDate).getTime() - new Date(b.nextEmiDate).getTime())[0]?.nextEmiDate;

    return { totalLoans, activeLoans, totalOutstanding, nextEmiDue };
  }

  calculateEMI(): void {
    this.loanService.calculateEmi(
      this.calculator.principal,
      this.calculator.rate,
      this.calculator.tenure
    ).subscribe({
      next: (result: { emiAmount: number; totalAmount: number; totalInterest: number }) => {
        this.calculator.emi = result.emiAmount;
        this.calculator.totalAmount = result.totalAmount;
        this.calculator.totalInterest = result.totalInterest;
      },
      error: (error: any) => {
        console.error('Error calculating EMI:', error);
      }
    });
  }

  checkEligibility(): void {
    if (!this.loanApplication.amount || !this.loanApplication.type) return;

    this.actionLoading = true;
    const userIdStr = localStorage.getItem('user_id');
    const userId = userIdStr ? Number(userIdStr) : 0;
    this.loanService.checkEligibility(userId, this.loanApplication.amount!, String(this.loanApplication.type)).subscribe({
      next: (result: LoanEligibility) => {
        this.eligibilityCheck = result;
        this.showEligibilityModal = true;
        this.actionLoading = false;
      },
      error: (error: any) => {
        console.error('Error checking eligibility:', error);
        this.actionLoading = false;
      }
    });
  }

  applyForLoan(): void {
    if (!this.eligibilityCheck?.eligible) return;

    this.actionLoading = true;
    this.loanService.applyForLoan(this.loanApplication).subscribe({
      next: (loan: Loan) => {
        this.loans.push(loan);
        this.closeApplyForm();
        this.actionLoading = false;
      },
      error: (error: any) => {
        console.error('Error applying for loan:', error);
        this.actionLoading = false;
      }
    });
  }

  viewLoanDetails(loan: Loan): void {
    this.selectedLoan = loan;
    this.loadEmiSchedule(loan.id);
    this.showDetailsModal = true;
  }

  loadEmiSchedule(loanId: number): void {
    this.loanService.getEmiSchedule(loanId).subscribe({
      next: (schedule: LoanEmi[]) => {
        this.emiSchedule = schedule;
      },
      error: (error: any) => {
        console.error('Error loading EMI schedule:', error);
      }
    });
  }

  payEmi(loanId: number, emiId?: number, amount?: number): void {
    this.actionLoading = true;
    const amt = Number(amount ?? 0);
    this.loanService.payEmi(loanId, amt).subscribe({
      next: () => {
        // Refresh loan data
        this.loadLoans();
        this.loadEmiSchedule(loanId);
        this.actionLoading = false;
      },
      error: (error: any) => {
        console.error('Error paying EMI:', error);
        this.actionLoading = false;
      }
    });
  }

  openApplyForm(): void {
    this.loanApplication = {
      amount: 0,
      tenure: 12,
      type: 'PERSONAL'
    };
    this.eligibilityCheck = null;
    this.showApplyForm = true;
  }

  closeApplyForm(): void {
    this.showApplyForm = false;
    this.loanApplication = {
      amount: 0,
      tenure: 12,
      type: 'PERSONAL'
    };
    this.eligibilityCheck = null;
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedLoan = null;
    this.emiSchedule = [];
  }

  closeEligibilityModal(): void {
    this.showEligibilityModal = false;
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.typeFilter = 'all';
    this.statusFilter = 'all';
  }

  getLoanTypeIcon(type: string): string {
    switch (type.toLowerCase()) {
      case 'personal': return '👤';
      case 'home': return '🏠';
      case 'car': return '🚗';
      case 'education': return '🎓';
      case 'business': return '💼';
      default: return '💰';
    }
  }

  getLoanStatusColor(status: string): string {
    switch (status?.toLowerCase()) {
      case 'active': return '#10b981';
      case 'closed': return '#6b7280';
      case 'defaulted': return '#ef4444';
      case 'pending': return '#f59e0b';
      default: return '#6b7280';
    }
  }

  formatCurrency(amount?: number): string {
    const n = Number(amount ?? 0);
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(n);
  }

  formatDate(date?: string): string {
    if (!date) return '-';
    const d = new Date(date);
    if (isNaN(d.getTime())) return '-';
    return d.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  getEmiStatusColor(status: string): string {
    switch (status?.toLowerCase()) {
      case 'paid': return '#10b981';
      case 'pending': return '#f59e0b';
      case 'overdue': return '#ef4444';
      default: return '#6b7280';
    }
  }
}
