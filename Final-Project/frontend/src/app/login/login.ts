import { Component } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';
  loading = false;

  constructor(private http: HttpClient, private router: Router) {}

  onSubmit() {
    this.loading = true;
    this.error = ''; // reset error on submit

    const url = '/realms/banking-app/protocol/openid-connect/token';

    const body = new HttpParams()
      .set('client_id', 'users-service')
      .set('client_secret', 'Gfsc0uvWfFeUsfy4gXnmoArevjPNpKJl')
      .set('grant_type', 'password')
      .set('username', this.username)
      .set('password', this.password)
      .set('scope', 'openid');

    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
      Accept: 'application/json',
    });
    console.debug('Sending token request to', url);
    this.http.post<any>(url, body.toString(), { headers }).subscribe({
      next: (response) => {
        if (response?.access_token) {
          localStorage.setItem('access_token', response.access_token);
        }
        if (response?.id_token) {
          localStorage.setItem('id_token', response.id_token);
        }
        try {
          const jwt = (response?.id_token || response?.access_token) as string;
          const payloadPart = jwt.split('.')[1];
          const base64 = payloadPart.replace(/-/g, '+').replace(/_/g, '/');
          const jsonPayload = decodeURIComponent(
            atob(base64)
              .split('')
              .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
              .join('')
          );
          const claims = JSON.parse(jsonPayload) || {};
          const userId = claims.sub || '';
          const userName = claims.preferred_username || claims.name || this.username || 'User';

          if (userId) {
            localStorage.setItem('user_id', userId);
          }
          if (userName) {
            localStorage.setItem('user_name', userName);
          }
        }catch {
          // Ignore decode errors; tokens still stored above
        }
        const userLookupEmail = this.username;
        const token2 = localStorage.getItem('access_token') || '';
        const authHeaders2 = new HttpHeaders({
          Authorization: `Bearer ${token2}`
        });
        this.http.get<number>(`/api/users/userIdByEmailId/${userLookupEmail}`, { headers: authHeaders2 }).subscribe({
          next: (userIdVal) => {
            localStorage.setItem('user', JSON.stringify(userIdVal));
            localStorage.removeItem('user_id');
            console.log('localStorage.user:', localStorage.getItem('user'));
            var userObj;
            const userStr = localStorage.getItem('user');
            if (userStr) {
              userObj = JSON.parse(userStr);
              console.log(userObj.userId);
            }
            console.log(userObj.userId);
            
            

            // ✅ Now fetch account details for this user
            const accountUrl = `http://localhost:8082/api/accounts/user/${userObj.userId}`;
            this.http.get<any>(accountUrl, { headers: authHeaders2 }).subscribe({
              next: (accountDetails) => {
                localStorage.setItem('account_details', JSON.stringify(accountDetails));
                console.log('localStorage.account_details:', accountDetails);
                this.loading = false;
                this.loading = false;
                localStorage.setItem('access_token', response.access_token);
                alert('Login successful!');
                this.router.navigate(['/dashboard']);
              },
              error: (accErr) => {
                console.error('Failed to fetch account details', accErr);
                this.loading = false;
                // still navigate to dashboard even if account fetch fails
                this.router.navigate(['/dashboard']);
              }
            });
          },
          error: (e) => {
            console.error('Failed to fetch userIdByEmailId', e);
            this.loading = false;
            this.router.navigate(['/dashboard']);
          }
        });
        
      },
      error: (err) => {
        this.loading = false;

        if (err?.status === 0) {
          this.error =
            'Cannot reach auth server. Ensure the backend is running and the dev proxy is active.';
        } else if (err?.error?.error_description) {
          this.error = err.error.error_description;
        } else if (err?.error?.error) {
          this.error = err.error.error;
        } else {
          this.error = 'Login failed. Please check your credentials.';
        }

        alert(this.error); // show error alert

        // Reload the page to reset form and state
        window.location.reload();
      },
    });
  }
}
