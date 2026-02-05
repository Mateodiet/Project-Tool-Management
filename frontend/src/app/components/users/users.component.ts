import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="users-page">
      <div class="page-header">
        <h1 class="page-title">Users</h1>
      </div>

      <div class="card">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Contact</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let user of users" data-testid="row-user-{{ user.userId }}">
              <td data-testid="text-user-id">{{ user.userId }}</td>
              <td data-testid="text-user-name">{{ user.name }}</td>
              <td data-testid="text-user-email">{{ user.email }}</td>
              <td>{{ user.contactNumber || '-' }}</td>
              <td>
                <span class="badge" [class.badge-member]="user.isActive" [class.badge-observer]="!user.isActive">
                  {{ user.isActive ? 'Active' : 'Inactive' }}
                </span>
              </td>
              <td>
                <button 
                  *ngIf="user.isActive" 
                  class="btn btn-secondary btn-sm" 
                  (click)="deactivateUser(user)"
                  data-testid="button-deactivate-user">
                  Deactivate
                </button>
                <button 
                  class="btn btn-danger btn-sm" 
                  (click)="deleteUser(user)"
                  data-testid="button-delete-user">
                  Delete
                </button>
              </td>
            </tr>
            <tr *ngIf="users.length === 0">
              <td colspan="6" style="text-align: center; color: #999;">No users found</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`
    .btn-sm {
      padding: 5px 10px;
      font-size: 12px;
      margin-right: 5px;
    }
  `]
})
export class UsersComponent implements OnInit {
  users: User[] = [];

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (response) => {
        if (response.data) {
          this.users = response.data;
        }
      },
      error: (err) => console.error('Failed to load users', err)
    });
  }

  deactivateUser(user: User): void {
    if (confirm(`Deactivate user ${user.name}?`)) {
      this.userService.deactivateUser(user.userId).subscribe({
        next: () => this.loadUsers(),
        error: (err) => console.error('Failed to deactivate user', err)
      });
    }
  }

  deleteUser(user: User): void {
    if (confirm(`Delete user ${user.name}? This action cannot be undone.`)) {
      this.userService.deleteUser(user.userId).subscribe({
        next: () => this.loadUsers(),
        error: (err) => console.error('Failed to delete user', err)
      });
    }
  }
}
