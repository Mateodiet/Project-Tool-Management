import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="auth-container">
      <div class="auth-card">
        <h1 class="auth-title">Create Account</h1>
        <p class="auth-subtitle">Join PMT today</p>
        
        <form (ngSubmit)="onSubmit()" class="auth-form">
          <div class="form-group">
            <label for="name">Full Name</label>
            <input 
              type="text" 
              id="name" 
              [(ngModel)]="name" 
              name="name"
              placeholder="Enter your name"
              required
              data-testid="input-name">
          </div>

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
              placeholder="Create a password"
              required
              data-testid="input-password">
          </div>

          <div class="form-group">
            <label for="contactNumber">Contact Number (optional)</label>
            <input 
              type="tel" 
              id="contactNumber" 
              [(ngModel)]="contactNumber" 
              name="contactNumber"
              placeholder="Enter phone number"
              data-testid="input-contact">
          </div>
          
          <div *ngIf="error" class="error-message">{{ error }}</div>
          <div *ngIf="success" class="success-message">{{ success }}</div>
          
          <button type="submit" class="btn btn-primary btn-block" [disabled]="loading" data-testid="button-register">
            {{ loading ? 'Creating account...' : 'Register' }}
          </button>
        </form>
        
        <p class="auth-link">
          Already have an account? <a routerLink="/login" data-testid="link-login">Login here</a>
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
    .success-message {
      background: #efe;
      color: #060;
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
export class RegisterComponent {
  name = '';
  email = '';
  password = '';
  contactNumber = '';
  error = '';
  success = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    if (!this.name || !this.email || !this.password) {
      this.error = 'Please fill in all required fields';
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    this.authService.register({
      name: this.name,
      email: this.email,
      password: this.password,
      contactNumber: this.contactNumber
    }).subscribe({
      next: (response) => {
        if (response.data) {
          this.success = 'Account created successfully! Redirecting to login...';
          setTimeout(() => this.router.navigate(['/login']), 2000);
        } else {
          this.error = response.message || 'Registration failed';
        }
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Registration failed. Please try again.';
        this.loading = false;
      }
    });
  }
}
