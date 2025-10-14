import { Component, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class Navbar implements OnInit {
  isMobile: boolean = false;
  isSidenavOpen: boolean = false;
  isDarkMode: boolean = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.updateIsMobile();
    this.isDarkMode = document.documentElement.classList.contains('dark');
  }

  @HostListener('window:resize')
  onResize(): void {
    this.updateIsMobile();
    if (!this.isMobile) {
      this.isSidenavOpen = false;
    }
  }

  private updateIsMobile(): void {
    this.isMobile = window.matchMedia('(max-width: 768px)').matches;
  }

  // Decode user claims from stored id_token/access_token
  private getClaimsFromStoredToken(): any {
    const token = localStorage.getItem('id_token') || localStorage.getItem('access_token');
    if (!token) return null;
    try {
      const payloadPart = token.split('.')[1];
      const base64 = payloadPart.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch {
      return null;
    }
  }

  get isLoggedIn(): boolean {
    return !!localStorage.getItem('access_token');
  }

  get currentUserName(): string {
    const stored = localStorage.getItem('user_name');
    if (stored) return stored;

    const claims = this.getClaimsFromStoredToken();
    if (claims) {
      return claims.preferred_username || claims.name || '';
    }
    return '';
  }

  toggleSidenav(): void {
    this.isSidenavOpen = !this.isSidenavOpen;
  }

  toggleTheme(): void {
    this.isDarkMode = !this.isDarkMode;
    document.documentElement.classList.toggle('dark', this.isDarkMode);
  }

  logout(): void {
    localStorage.removeItem('access_token');
    localStorage.removeItem('id_token');
    localStorage.removeItem('user_id');
    localStorage.removeItem('user_name');
    this.router.navigate(['/login']);
  }
}
