import { Component } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, DatePipe, CurrencyPipe],
  templateUrl: './transactions.html',
  styleUrl: './transactions.css'
})
export class Transactions {
  // Demo data to make the template functional
  transactions = [
    { date: new Date(), id: 'TXN-001', type: 'Credit', amount: 1500, fromAccount: 'A-1001', toAccount: 'A-2001' },
    { date: new Date(), id: 'TXN-002', type: 'Debit', amount: 450, fromAccount: 'A-1001', toAccount: 'A-3001' },
    { date: new Date(), id: 'TXN-003', type: 'Credit', amount: 2300, fromAccount: 'A-4001', toAccount: 'A-1001' },
  ];

  filteredTransactions = [...this.transactions];
  paginatedTransactions = [...this.filteredTransactions];

  totalIncome = 0;
  totalExpenses = 0;

  searchTerm = '';
  typeFilter: 'all' | 'Credit' | 'Debit' = 'all';
  sortBy: 'date' | 'amount' | 'type' = 'date';
  sortOrder: 'asc' | 'desc' = 'asc';

  itemsPerPage = 10;
  currentPage = 1;
  totalPages = 1;
  visiblePageNumbers: number[] = [];

  get currentPageEnd(): number {
    const end = this.currentPage * this.itemsPerPage;
    return Math.min(end, this.filteredTransactions.length);
  }

  selectedTransaction: any = null;
  selectedTransactionIndex = 0;

  constructor() {
    this.applyAll();
  }

  onSearch() {
    this.currentPage = 1;
    this.applyAll();
  }

  onFilterChange() {
    this.currentPage = 1;
    this.applyAll();
  }

  onSort() {
    this.applyAll();
  }

  toggleSortOrder() {
    this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
    this.applyAll();
  }

  clearFilters() {
    this.searchTerm = '';
    this.typeFilter = 'all';
    this.sortBy = 'date';
    this.sortOrder = 'asc';
    this.currentPage = 1;
    this.applyAll();
  }

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
    this.paginate();
  }

  openTransactionDetails(tx: any) {
    this.selectedTransaction = tx;
    this.selectedTransactionIndex =
      (this.currentPage - 1) * this.itemsPerPage +
      this.paginatedTransactions.indexOf(tx) + 1;
  }

  closeTransactionDetails() {
    this.selectedTransaction = null;
  }

  private applyAll() {
    this.filter();
    this.sort();
    this.computeTotals();
    this.computeTotalPages();
    this.paginate();
    this.computeVisiblePages();
  }

  private filter() {
    const term = this.searchTerm.trim().toLowerCase();
    this.filteredTransactions = this.transactions.filter((t) => {
      const matchesType = this.typeFilter === 'all' ? true : t.type === this.typeFilter;
      const matchesTerm =
        !term ||
        (t.id && t.id.toLowerCase().includes(term)) ||
        (t.fromAccount && t.fromAccount.toLowerCase().includes(term)) ||
        (t.toAccount && t.toAccount.toLowerCase().includes(term)) ||
        (t.type && t.type.toLowerCase().includes(term));
      return matchesType && matchesTerm;
    });
  }

  private sort() {
    const dir = this.sortOrder === 'asc' ? 1 : -1;
    this.filteredTransactions.sort((a: any, b: any) => {
      let cmp = 0;
      if (this.sortBy === 'date') cmp = new Date(a.date).getTime() - new Date(b.date).getTime();
      else if (this.sortBy === 'amount') cmp = a.amount - b.amount;
      else if (this.sortBy === 'type') cmp = a.type.localeCompare(b.type);
      return cmp * dir;
    });
  }

  private computeTotals() {
    this.totalIncome = this.filteredTransactions
      .filter((t) => t.type === 'Credit')
      .reduce((sum, t) => sum + (t.amount || 0), 0);
    this.totalExpenses = this.filteredTransactions
      .filter((t) => t.type === 'Debit')
      .reduce((sum, t) => sum + (t.amount || 0), 0);
  }

  private computeTotalPages() {
    this.totalPages = Math.max(1, Math.ceil(this.filteredTransactions.length / this.itemsPerPage));
    if (this.currentPage > this.totalPages) this.currentPage = this.totalPages;
  }

  private paginate() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    this.paginatedTransactions = this.filteredTransactions.slice(start, start + this.itemsPerPage);
  }

  private computeVisiblePages() {
    const pages: number[] = [];
    for (let i = 1; i <= this.totalPages; i++) pages.push(i);
    this.visiblePageNumbers = pages.slice(0, 10);
  }
}
