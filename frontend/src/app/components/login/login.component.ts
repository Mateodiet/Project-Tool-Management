import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="auth-container">
      <div class="auth-card">
        <h1 class="auth-title">PMT Login</h1>
        <p class="auth-subtitle">Project Management Tool</p>
        
        <form (ngSubmit)="onSubmit()" class="auth-form">
          <div class="form-group">
            <label for="email">Email</label>
            <input 
              type="email" 
              id="email" 
              [(ngModel)]="email" 
              name="email"
              placeholder="Enter your email"
              required
              data-testid="input-email">
          </div>
          
          <div class="form-group">
            <label for="password">Password</label>
            <input 
              type="password" 
              id="password" 
              [(ngModel)]="password" 
              name="password"
              placeholder="Enter your password"
              required
              data-testid="input-password">
          </div>
          
          <div *ngIf="error" class="error-message">{{ error }}</div>
          
          <button type="submit" class="btn btn-primary btn-block" [disabled]="loading" data-testid="button-login">
            {{ loading ? 'Logging in...' : 'Login' }}
          </button>
        </form>
        
        <p class="auth-link">
          Don't have an account? <a routerLink="/register" data-testid="link-register">Register here</a>
        </p>
      </div>
    </div>
  `,
  styles: [`
    .auth-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    .auth-card {
      background: white;
      padding: 40px;
      border-radius: 12px;
      box-shadow: 0 10px 40px rgba(0,0,0,0.2);
      width: 100%;
      max-width: 400px;
    }
    .auth-title {
      text-align: center;
      margin-bottom: 5px;
      color: #333;
      font-size: 28px;
    }
    .auth-subtitle {
      text-align: center;
      color: #666;
      margin-bottom: 30px;
    }
    .btn-block {
      width: 100%;
      padding: 12px;
      font-size: 16px;
    }
    .error-message {
      background: #fee;
      color: #c00;
      padding: 10px;
      border-radius: 6px;
      margin-bottom: 15px;
      font-size: 14px;
    }
    .auth-link {
      text-align: center;
      margin-top: 20px;
      color: #666;
    }
    .auth-link a {
      color: #667eea;
      font-weight: 500;
    }
  `]
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    if (!this.email || !this.password) {
      this.error = 'Please fill in all fields';
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: (response) => {
        if (response.data) {
          this.router.navigate(['/home']);
        } else {
          this.error = response.message || 'Login failed';
        }
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Login failed. Please try again.';
        this.loading = false;
      }
    });
  }
}
