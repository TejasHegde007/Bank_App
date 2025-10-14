export interface Loan {
  id: number;
  loanNumber: string;
  type: string;              // maps from backend loanType
  status: string;            // maps from backend loanStatus
  outstandingAmount: number; // best-effort mapping (see service)
  nextEmiDate: string;       // best-effort mapping (see service)
  principalAmount?: number;
  interestRate?: number;
  tenure?: number;
  accountId?: number;
  userId?: number;
  emiAmount?: number;
  totalAmount?: number;
  amount?: number;
  createdDate?: string;      // optional to match template usage
}

export interface LoanEmi {
  id?: number;
  emiNumber?: number;  // optional to match template usage
  amount: number;
  dueDate: string;
  paidDate?: string;   // optional to match template usage
  status: string;
}

export interface LoanEligibility {
  eligible: boolean;
  maxEligibleAmount?: number;
  maxLoanAmount?: number; // alias used in template
  interestRate?: number;  // optional to match template usage
  reason?: string;        // optional to match template usage
  message?: string;
}
