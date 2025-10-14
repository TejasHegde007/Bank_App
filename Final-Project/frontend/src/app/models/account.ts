export interface Account {
  // Align with backend AccountResponseDto while keeping existing UI expectations
  id?: number;             // convenience alias for accountId
  accountId?: number;      // from backend
  accountNumber: string;
  type: string;            // maps from backend accountType
  balance: number;
  status?: string;
  userId?: number;
  createdAt?: string;
  updatedAt?: string;
  createdDate?: string;
  lastActivity?: string;
}
