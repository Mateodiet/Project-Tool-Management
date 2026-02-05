import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TaskService } from '../../services/task.service';
import { AuthService } from '../../services/auth.service';
import { Task } from '../../models/task.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard">
      <div class="page-header">
        <h1 class="page-title">Dashboard</h1>
      </div>

      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-value purple" data-testid="text-total-projects">{{ stats.totalProjects }}</div>
          <div class="stat-label">Total Projects</div>
        </div>
        <div class="stat-card">
          <div class="stat-value red" data-testid="text-todo-tasks">{{ stats.todoTasks }}</div>
          <div class="stat-label">To Do</div>
        </div>
        <div class="stat-card">
          <div class="stat-value yellow" data-testid="text-progress-tasks">{{ stats.inProgressTasks }}</div>
          <div class="stat-label">In Progress</div>
        </div>
        <div class="stat-card">
          <div class="stat-value green" data-testid="text-completed-tasks">{{ stats.completedTasks }}</div>
          <div class="stat-label">Completed</div>
        </div>
      </div>

      <h2 style="margin-bottom: 20px;">Tasks by Status</h2>
      
      <div class="kanban-board">
        <div class="kanban-column">
          <div class="kanban-column-header todo">TO DO ({{ todoTasks.length }})</div>
          <div class="kanban-column-content">
            <div *ngFor="let task of todoTasks" class="task-card" data-testid="card-task-todo">
              <div class="task-card-title">{{ task.taskName }}</div>
              <div class="task-card-desc">{{ task.taskDescription }}</div>
              <div class="task-card-footer">
                <span class="badge" [class]="'badge-' + task.taskPriority.toLowerCase()">
                  {{ task.taskPriority }}
                </span>
                <span *ngIf="task.dueDate">{{ task.dueDate }}</span>
              </div>
            </div>
            <div *ngIf="todoTasks.length === 0" class="empty-state">No tasks</div>
          </div>
        </div>

        <div class="kanban-column">
          <div class="kanban-column-header in-progress">IN PROGRESS ({{ inProgressTasks.length }})</div>
          <div class="kanban-column-content">
            <div *ngFor="let task of inProgressTasks" class="task-card" data-testid="card-task-progress">
              <div class="task-card-title">{{ task.taskName }}</div>
              <div class="task-card-desc">{{ task.taskDescription }}</div>
              <div class="task-card-footer">
                <span class="badge" [class]="'badge-' + task.taskPriority.toLowerCase()">
                  {{ task.taskPriority }}
                </span>
                <span *ngIf="task.dueDate">{{ task.dueDate }}</span>
              </div>
            </div>
            <div *ngIf="inProgressTasks.length === 0" class="empty-state">No tasks</div>
          </div>
        </div>

        <div class="kanban-column">
          <div class="kanban-column-header completed">COMPLETED ({{ completedTasks.length }})</div>
          <div class="kanban-column-content">
            <div *ngFor="let task of completedTasks" class="task-card" data-testid="card-task-completed">
              <div class="task-card-title">{{ task.taskName }}</div>
              <div class="task-card-desc">{{ task.taskDescription }}</div>
              <div class="task-card-footer">
                <span class="badge" [class]="'badge-' + task.taskPriority.toLowerCase()">
                  {{ task.taskPriority }}
                </span>
                <span *ngIf="task.dueDate">{{ task.dueDate }}</span>
              </div>
            </div>
            <div *ngIf="completedTasks.length === 0" class="empty-state">No tasks</div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .empty-state {
      text-align: center;
      color: #999;
      padding: 20px;
      font-style: italic;
    }
  `]
})
export class DashboardComponent implements OnInit {
  stats = {
    totalProjects: 0,
    todoTasks: 0,
    inProgressTasks: 0,
    completedTasks: 0,
    totalTasks: 0
  };

  todoTasks: Task[] = [];
  inProgressTasks: Task[] = [];
  completedTasks: Task[] = [];

  constructor(
    private taskService: TaskService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    const email = this.authService.currentUser?.email;
    if (!email) return;

    this.taskService.getDashboardStats(email).subscribe({
      next: (response) => {
        if (response.data) {
          this.stats = {
            totalProjects: response.data.totalProjects || 0,
            todoTasks: response.data.todoTasks || 0,
            inProgressTasks: response.data.inProgressTasks || 0,
            completedTasks: response.data.completedTasks || 0,
            totalTasks: response.data.totalTasks || 0
          };

          if (response.data.tasksByStatus) {
            this.todoTasks = response.data.tasksByStatus.TODO || [];
            this.inProgressTasks = response.data.tasksByStatus.IN_PROGRESS || [];
            this.completedTasks = response.data.tasksByStatus.COMPLETED || [];
          }
        }
      },
      error: (err) => console.error('Failed to load dashboard', err)
    });
  }
}
