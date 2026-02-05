import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <div class="layout">
      <aside class="sidebar">
        <div class="sidebar-header">PMT</div>
        <nav class="sidebar-nav">
          <a routerLink="/home/dashboard" routerLinkActive="active" data-testid="link-dashboard">Dashboard</a>
          <a routerLink="/home/projects" routerLinkActive="active" data-testid="link-projects">Projects</a>
          <a routerLink="/home/users" routerLinkActive="active" data-testid="link-users">Users</a>
        </nav>
      </aside>
      
      <main class="main-content">
        <header class="topbar">
          <div class="user-info">
            <span class="user-name" data-testid="text-username">{{ currentUser?.name }}</span>
            <span class="user-email">{{ currentUser?.email }}</span>
          </div>
          <button class="btn btn-secondary" (click)="logout()" data-testid="button-logout">Logout</button>
        </header>
        
        <div class="content">
          <router-outlet></router-outlet>
        </div>
      </main>
    </div>
  `,
  styles: [`
    .layout {
      display: flex;
      min-height: 100vh;
    }
    .user-info {
      display: flex;
      flex-direction: column;
      margin-right: 20px;
      text-align: right;
    }
    .user-name {
      font-weight: 600;
      color: #333;
    }
    .user-email {
      font-size: 12px;
      color: #666;
    }
    .topbar {
      display: flex;
      justify-content: flex-end;
      align-items: center;
    }
  `]
})
export class HomeComponent {
  currentUser = this.authService.currentUser;

  constructor(private authService: AuthService, private router: Router) {}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
