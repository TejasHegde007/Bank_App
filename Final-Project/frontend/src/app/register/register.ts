import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css'],
})
export class Register {
  firstName = '';
  lastName = '';
  email = '';
  phoneNumber = '';
  password = '';
  confirmPassword = '';

  loading = false;
  error = '';

  constructor(private http: HttpClient, private router: Router) {}

  onSubmit() {
    if (this.password !== this.confirmPassword) {
      this.error = 'Passwords do not match!';
      return;
    }

    this.loading = true;
    const url = '/api/users/public/register';

    const payload = {
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email,
      phoneNumber: this.phoneNumber,
      password: this.password,
    };

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Accept: 'application/json',
    });

    this.http.post(url, payload, { headers }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.loading = false;
        console.error('Registration failed', err);
        if (err?.error?.message) {
          this.error = err.error.message;
        } else if (typeof err?.error === 'string') {
          this.error = err.error;
        } else {
          this.error = 'Registration failed. Please try again.';
        }
      },
    });
  }
}