import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {
  isLoggedIn = !!localStorage.getItem('access_token');
  currentUserName = 'User';
  isDarkMode = false;
  isMobile = false;
  isSidenavOpen = false;

  constructor(private router: Router) {
    // Basic mobile detection
    this.isMobile = typeof window !== 'undefined' ? window.innerWidth < 768 : false;
  }

  toggleTheme() {
    this.isDarkMode = !this.isDarkMode;
    const body = document.body;
    if (this.isDarkMode) {
      body.classList.add('dark-theme');
    } else {
      body.classList.remove('dark-theme');
    }
  }

  toggleSidenav() {
    this.isSidenavOpen = !this.isSidenavOpen;
  }

  logout() {
    localStorage.removeItem('access_token');
    this.isLoggedIn = false;
    this.router.navigate(['/']);
  }
}
