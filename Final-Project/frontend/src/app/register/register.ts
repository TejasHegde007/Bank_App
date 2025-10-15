import { Component, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
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
  passwordMismatch = false;

  @ViewChild('registerForm') registerForm!: NgForm;

  constructor(private http: HttpClient, private router: Router, private cd: ChangeDetectorRef) {}

  // Called when form inputs change to clear errors and validate passwords
  checkPasswords() {
    this.passwordMismatch = this.password !== this.confirmPassword;
    if (this.passwordMismatch) {
      this.error = 'Passwords do not match!';
    } else {
      this.error = '';
    }
  }

  onSubmit() {
    this.checkPasswords();

    if (this.passwordMismatch) {
      return; // Prevent submission if passwords don't match
    }

    if (!this.registerForm.valid) {
      this.error = 'Please fill all required fields correctly.';
      return;
    }

    this.loading = true;
    this.error = '';

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
        this.error = '';
        this.cd.markForCheck();
        console.log('Registration successful, navigating to login.');
        this.router.navigate(['/']);
      },
      error: (error: any) => {
        this.loading = false;
        this.error = 'Unexpected service error. User Already Exists.';
        console.error('Registration failed:', error); // For debugging only
        this.cd.markForCheck();
      },
    });
  }
}