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

    this.http.post<any>(url, body.toString(), { headers }).subscribe({
      next: (response) => {
        this.loading = false;
        localStorage.setItem('access_token', response.access_token);
        alert('Login successful!');
        this.router.navigate(['/dashboard']);
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
