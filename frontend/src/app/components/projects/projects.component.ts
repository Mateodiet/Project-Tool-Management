import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProjectService } from '../../services/project.service';
import { AuthService } from '../../services/auth.service';
import { Project } from '../../models/project.model';

@Component({
  selector: 'app-projects',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="projects-page">
      <div class="page-header">
        <h1 class="page-title">Projects</h1>
        <button class="btn btn-primary" (click)="showCreateModal = true" data-testid="button-create-project">
          + New Project
        </button>
      </div>

      <div class="card">
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Description</th>
              <th>Start Date</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let project of projects" data-testid="row-project-{{ project.projectId }}">
              <td data-testid="text-project-name">{{ project.projectName }}</td>
              <td>{{ project.projectDescription || '-' }}</td>
              <td>{{ project.projectStartDate || '-' }}</td>
              <td>
                <span class="badge" [class]="'badge-' + project.projectStatus?.toLowerCase()">
                  {{ project.projectStatus }}
                </span>
              </td>
              <td>
                <button class="btn btn-primary btn-sm" (click)="viewProject(project)" data-testid="button-view-project">
                  View
                </button>
                <button class="btn btn-danger btn-sm" (click)="deleteProject(project)" data-testid="button-delete-project">
                  Delete
                </button>
              </td>
            </tr>
            <tr *ngIf="projects.length === 0">
              <td colspan="5" style="text-align: center; color: #999;">No projects found</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create Project Modal -->
    <div *ngIf="showCreateModal" class="modal-overlay" (click)="showCreateModal = false">
      <div class="modal" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h2 class="modal-title">Create New Project</h2>
        </div>
        
        <form (ngSubmit)="createProject()">
          <div class="form-group">
            <label for="projectName">Project Name *</label>
            <input 
              type="text" 
              id="projectName" 
              [(ngModel)]="newProject.projectName" 
              name="projectName"
              required
              data-testid="input-project-name">
          </div>

          <div class="form-group">
            <label for="projectDescription">Description</label>
            <textarea 
              id="projectDescription" 
              [(ngModel)]="newProject.projectDescription" 
              name="projectDescription"
              rows="3"
              data-testid="input-project-description"></textarea>
          </div>

          <div class="form-group">
            <label for="projectStartDate">Start Date</label>
            <input 
              type="date" 
              id="projectStartDate" 
              [(ngModel)]="newProject.projectStartDate" 
              name="projectStartDate"
              data-testid="input-project-date">
          </div>

          <div *ngIf="error" class="error-message">{{ error }}</div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="showCreateModal = false">Cancel</button>
            <button type="submit" class="btn btn-primary" data-testid="button-submit-project">Create</button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .btn-sm {
      padding: 5px 10px;
      font-size: 12px;
      margin-right: 5px;
    }
    .error-message {
      background: #fee;
      color: #c00;
      padding: 10px;
      border-radius: 6px;
      margin-bottom: 15px;
    }
  `]
})
export class ProjectsComponent implements OnInit {
  projects: Project[] = [];
  showCreateModal = false;
  error = '';

  newProject: Partial<Project> = {
    projectName: '',
    projectDescription: '',
    projectStartDate: '',
    projectStatus: 'ACTIVE'
  };

  constructor(
    private projectService: ProjectService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(): void {
    this.projectService.getAllProjects().subscribe({
      next: (response) => {
        if (response.data) {
          this.projects = response.data;
        }
      },
      error: (err) => console.error('Failed to load projects', err)
    });
  }

  createProject(): void {
    if (!this.newProject.projectName) {
      this.error = 'Project name is required';
      return;
    }

    const email = this.authService.currentUser?.email;
    if (!email) return;

    this.projectService.createProject(this.newProject, email).subscribe({
      next: () => {
        this.showCreateModal = false;
        this.newProject = { projectName: '', projectDescription: '', projectStartDate: '', projectStatus: 'ACTIVE' };
        this.error = '';
        this.loadProjects();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to create project';
      }
    });
  }

  viewProject(project: Project): void {
    this.router.navigate(['/home/project', project.projectName]);
  }

  deleteProject(project: Project): void {
    if (confirm(`Delete project "${project.projectName}"? This will delete all tasks in this project.`)) {
      this.projectService.deleteProject(project.projectName).subscribe({
        next: () => this.loadProjects(),
        error: (err) => console.error('Failed to delete project', err)
      });
    }
  }
}
