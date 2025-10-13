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
    const url = '/realms/banking-app/protocol/openid-connect/token';

    const body = new HttpParams()
      .set('client_id', 'users-service')
      .set('client_secret', '4ng7e682KnVuvfolBaMp3X1zpavksBqU')
      .set('grant_type', 'password')
      .set('username', this.username)
      .set('password', this.password)
      .set('scope', 'openid');

    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
      'Accept': 'application/json'
    });

    console.debug('Sending token request to', url);
    this.http.post<any>(url, body.toString(), { headers }).subscribe({
      next: (response) => {
        localStorage.setItem('access_token', response.access_token);
        this.loading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        console.error('Login failed', err);
        if (err?.status === 0) {
          this.error = 'Cannot reach auth server. Ensure the backend is running and the dev proxy is active.';
        } else if (err?.error?.error_description) {
          this.error = err.error.error_description;
        } else if (err?.error?.error) {
          this.error = err.error.error;
        } else {
          this.error = 'Login failed. Please check your credentials.';
        }
      }
    });
  }
}
