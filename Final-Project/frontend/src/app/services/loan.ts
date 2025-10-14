import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, of } from 'rxjs';
import { Loan, LoanEmi, LoanEligibility } from '../models/loan';

@Injectable({ providedIn: 'root' })
export class LoanService {
  // LoanController base mapping is "/api/loans"
  private readonly baseUrl = '/api/loans';

  constructor(private http: HttpClient) {}

  // GET /api/loans/user/{userId}
  getUserLoans(userId: number): Observable<Loan[]> {
    return this.http.get<any[]>(`${this.baseUrl}/user/${userId}`).pipe(
      map((list) => (list || []).map((dto) => this.toLoan(dto)))
    );
  }

  // GET /api/loans/{id}
  getLoanById(id: number): Observable<Loan> {
    return this.http.get<any>(`${this.baseUrl}/${id}`).pipe(map((dto) => this.toLoan(dto)));
  }

  // POST /api/loans
  // Backend expects LoanCreateRequest: { userId, accountId, loanType, principalAmount, interestRate, tenureMonths }
  applyForLoan(application: Partial<Loan>): Observable<Loan> {
    const userIdStr = localStorage.getItem('user_id');
    const userId = userIdStr ? Number(userIdStr) : undefined;

    const payload: any = {
      userId,
      accountId: application.accountId ?? null,
      loanType: (application.type || 'PERSONAL').toString().toUpperCase(),
      principalAmount: application.principalAmount ?? application.amount ?? 0,
      interestRate: application.interestRate ?? 12,
      tenureMonths: application.tenure ?? 12
    };
    return this.http.post<any>(this.baseUrl, payload).pipe(map((dto) => this.toLoan(dto)));
  }

  // PUT /api/loans/{id}/close
  closeLoan(id: number): Observable<Loan> {
    return this.http.put<any>(`${this.baseUrl}/${id}/close`, {}).pipe(map((dto) => this.toLoan(dto)));
  }

  // Utilities for UI-only flows (no backend endpoints available in controller)
  // Client-side EMI calculation
  calculateEmi(principal: number, annualRatePercent: number, tenureMonths: number):
    Observable<{ emiAmount: number; totalAmount: number; totalInterest: number }> {
    const P = Number(principal) || 0;
    const r = (Number(annualRatePercent) || 0) / 12 / 100;
    const n = Number(tenureMonths) || 0;

    let emi = 0;
    if (P > 0 && r > 0 && n > 0) {
      const pow = Math.pow(1 + r, n);
      emi = (P * r * pow) / (pow - 1);
    } else if (P > 0 && n > 0) {
      emi = P / n;
    }
    const totalAmount = emi * n;
    const totalInterest = totalAmount - P;

    return of({
      emiAmount: Number(emi.toFixed(2)),
      totalAmount: Number(totalAmount.toFixed(2)),
      totalInterest: Number(totalInterest.toFixed(2)),
    });
  }

  // Simple eligibility stub (no endpoint in controller)
  checkEligibility(_userId: number, amount: number, type: string): Observable<LoanEligibility> {
    const cap = 500000;
    const eligible = (Number(amount) || 0) <= cap;
    return of({
      eligible,
      maxEligibleAmount: cap,
      message: eligible
        ? `Eligible for ${type} loan up to $${cap.toLocaleString('en-US')}`
        : `Requested amount exceeds maximum eligible amount of $${cap.toLocaleString('en-US')}`
    });
  }

  // EMI schedule stub (no endpoint in controller)
  getEmiSchedule(loanId: number): Observable<LoanEmi[]> {
    // Produce a 12-month stub schedule
    const today = new Date();
    const schedule: LoanEmi[] = Array.from({ length: 12 }).map((_, i) => {
      const due = new Date(today);
      due.setMonth(due.getMonth() + i + 1);
      return {
        id: i + 1,
        amount: 0,
        dueDate: due.toISOString(),
        status: 'PENDING'
      };
    });
    return of(schedule);
  }

  // payEmi stub (no endpoint in controller)
  payEmi(_loanId: number, _amount: number): Observable<void> {
    return of(void 0);
  }

  private toLoan(dto: any): Loan {
    return {
      id: dto?.id,
      loanNumber: dto?.loanNumber ?? String(dto?.id ?? ''),
      type: dto?.loanType ?? dto?.type ?? 'PERSONAL',
      status: dto?.loanStatus ?? dto?.status ?? 'ACTIVE',
      outstandingAmount: Number(dto?.totalPayableAmount ?? 0),
      nextEmiDate: dto?.endDate || dto?.disbursementDate || new Date().toISOString(),
      principalAmount: dto?.principalAmount,
      interestRate: dto?.interestRate,
      tenure: dto?.tenureMonths,
      accountId: dto?.accountId,
      userId: dto?.userId,
      emiAmount: dto?.emiAmount,
      totalAmount: dto?.totalPayableAmount,
    };
  }
}
