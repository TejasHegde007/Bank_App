import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map, of, switchMap, tap } from 'rxjs';
import { Account } from '../models/account';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private readonly baseUrl = 'http://localhost:8082/api/accounts';

  constructor(private http: HttpClient) {}

  // GET /api/accounts
  getAccounts(): Observable<Account[]> {
    return this.http.get<any[]>(this.baseUrl).pipe(
      map((list) => (list || []).map((dto) => this.toAccount(dto)))
    );
  }

  // GET /api/accounts/{id}
  getAccountById(id: number): Observable<Account> {
    return this.http.get<any>(`${this.baseUrl}/${id}`).pipe(map((dto) => this.toAccount(dto)));
  }

  // POST /api/accounts (AccountRequestDto)
  // { userId: number, accountType: 'SAVINGS'|'CHECKING'|'BUSINESS', initialBalance: number }
  createAccount(newAccount: Partial<Account>): Observable<Account> {
    const userId = localStorage.getItem('user_id');
    const rawType = newAccount.type ?? 'SAVINGS';
    const enumType = this.toAccountTypeEnum(rawType);
    const payload: any = {
      userId,
      accountType: enumType,
      initialBalance: newAccount.balance ?? 0
    };
    const token = localStorage.getItem('access_token') || '';
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {})
    });
    const headerPreview = {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {})
    };

    // Debug: console userId, final request JSON and Authorization header being sent (browser-only)
    const isBrowser = typeof window !== 'undefined' && typeof window.document !== 'undefined';
    if (isBrowser) {
      try {
        const userIdRawDebug = localStorage.getItem('user_id');
        // Log userId from storage (raw, no conversion)
        console.log('AccountService.createAccount -> userId (from localStorage, raw):', userIdRawDebug);
        // Log Authorization header preview
        console.log('AccountService.createAccount -> Authorization header:', token ? `Bearer ${token}` : 'MISSING');
        // Log final URL and payload
        console.log('AccountService.createAccount -> URL:', this.baseUrl);
        console.log('AccountService.createAccount -> accountType (raw -> enum):', rawType, '->', enumType);
        console.log('AccountService.createAccount -> Payload:', payload);
        console.log('AccountService.createAccount -> Headers preview:', headerPreview);
      } catch { /* ignore logging errors in SSR */ }
    }

    // Route via proxy to avoid CORS: /accounts-api -> http://localhost:8082
    return this.http.post<any>(this.baseUrl, payload, { headers })
      .pipe(
        tap({
          next: (resp) => console.log('AccountService.createAccount -> Raw response:', resp),
          error: (err) => console.error('AccountService.createAccount -> HTTP error:', err)
        }),
        map((dto) => this.toAccount(dto))
      );
  }

  // No update endpoint in backend; provide a no-op to satisfy UI flow
  updateAccount(_accountNumber: string, _patch: Partial<Account>): Observable<void> {
    console.warn('updateAccount not supported by backend; returning no-op.');
    return of(void 0);
  }

  // DELETE /api/accounts/{id}
  // UI passes accountNumber; resolve id by listing accounts first, then delete by id.
  closeAccount(identifier: string | number): Observable<void> {
    if (typeof identifier === 'number') {
      return this.http.delete<void>(`${this.baseUrl}/${identifier}`);
    }
    const accountNumber = identifier;
    return this.getAccounts().pipe(
      map((accounts) => {
        const match = accounts.find((a) => a.accountNumber === accountNumber);
        return match?.accountId ?? match?.id;
      }),
      switchMap((id) => {
        if (!id) {
          console.warn('closeAccount: could not resolve id for accountNumber', accountNumber);
          return of(void 0);
        }
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
      })
    );
  }

  private toAccountTypeEnum(type: string): string {
    const t = (type || '').toUpperCase();
    if (t.includes('SAV')) return 'SAVINGS';
    if (t.includes('CHK') || t.includes('CHECK')) return 'CHECKING';
    if (t.includes('BUS')) return 'BUSINESS';
    return t || 'SAVINGS';
  }

  private toAccount(dto: any): Account {
    const id = dto?.accountId ?? dto?.id;
    const type = dto?.accountType ?? dto?.type ?? 'Account';
    const balance =
      typeof dto?.balance === 'number' ? dto.balance : Number(dto?.balance ?? 0);
    return {
      id,
      accountId: dto?.accountId,
      accountNumber: dto?.accountNumber ?? String(id ?? ''),
      type,
      balance,
      userId: dto?.userId,
      createdAt: dto?.createdAt,
      updatedAt: dto?.updatedAt,
      status: dto?.status
    };
  }
}
