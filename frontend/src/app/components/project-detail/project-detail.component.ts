import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService } from '../../services/project.service';
import { TaskService } from '../../services/task.service';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { Project, ProjectMember, InviteRequest } from '../../models/project.model';
import { Task, CreateTaskRequest } from '../../models/task.model';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="project-detail">
      <div class="page-header">
        <h1 class="page-title" data-testid="text-project-title">{{ project?.projectName }}</h1>
        <button class="btn btn-secondary" (click)="goBack()" data-testid="button-back">Back to Projects</button>
      </div>

      <div class="project-info card" *ngIf="project">
        <p><strong>Description:</strong> {{ project.projectDescription || 'No description' }}</p>
        <p><strong>Status:</strong> <span class="badge">{{ project.projectStatus }}</span></p>
        <p><strong>Start Date:</strong> {{ project.projectStartDate || 'Not set' }}</p>
      </div>

      <div class="tabs">
        <div class="tab" [class.active]="activeTab === 'tasks'" (click)="activeTab = 'tasks'">Tasks</div>
        <div class="tab" [class.active]="activeTab === 'members'" (click)="activeTab = 'members'">Members</div>
      </div>

      <!-- Tasks Tab -->
      <div *ngIf="activeTab === 'tasks'" class="tab-content">
        <div class="actions-bar">
          <button class="btn btn-primary" (click)="showTaskModal = true" data-testid="button-create-task">+ Add Task</button>
        </div>

        <div class="kanban-board">
          <div class="kanban-column">
            <div class="kanban-column-header todo">TO DO</div>
            <div class="kanban-column-content">
              <div *ngFor="let task of todoTasks" class="task-card" data-testid="card-task">
                <div class="task-card-title">{{ task.taskName }}</div>
                <div class="task-card-desc">{{ task.taskDescription }}</div>
                <div class="task-card-footer">
                  <span class="badge" [class]="'badge-' + task.taskPriority?.toLowerCase()">{{ task.taskPriority }}</span>
                  <div class="task-actions">
                    <button class="btn-icon" (click)="editTask(task)" data-testid="button-edit-task">Edit</button>
                    <button class="btn-icon" (click)="deleteTask(task)" data-testid="button-delete-task">Delete</button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="kanban-column">
            <div class="kanban-column-header in-progress">IN PROGRESS</div>
            <div class="kanban-column-content">
              <div *ngFor="let task of inProgressTasks" class="task-card">
                <div class="task-card-title">{{ task.taskName }}</div>
                <div class="task-card-desc">{{ task.taskDescription }}</div>
                <div class="task-card-footer">
                  <span class="badge" [class]="'badge-' + task.taskPriority?.toLowerCase()">{{ task.taskPriority }}</span>
                  <div class="task-actions">
                    <button class="btn-icon" (click)="editTask(task)">Edit</button>
                    <button class="btn-icon" (click)="deleteTask(task)">Delete</button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="kanban-column">
            <div class="kanban-column-header completed">COMPLETED</div>
            <div class="kanban-column-content">
              <div *ngFor="let task of completedTasks" class="task-card">
                <div class="task-card-title">{{ task.taskName }}</div>
                <div class="task-card-desc">{{ task.taskDescription }}</div>
                <div class="task-card-footer">
                  <span class="badge" [class]="'badge-' + task.taskPriority?.toLowerCase()">{{ task.taskPriority }}</span>
                  <div class="task-actions">
                    <button class="btn-icon" (click)="editTask(task)">Edit</button>
                    <button class="btn-icon" (click)="deleteTask(task)">Delete</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Members Tab -->
      <div *ngIf="activeTab === 'members'" class="tab-content">
        <div class="actions-bar">
          <button class="btn btn-primary" (click)="showInviteModal = true" data-testid="button-invite-member">+ Invite Member</button>
        </div>

        <div class="card">
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let member of members" data-testid="row-member">
                <td>{{ member.name }}</td>
                <td>{{ member.email }}</td>
                <td>
                  <select [(ngModel)]="member.role" (change)="updateMemberRole(member)" data-testid="select-role">
                    <option value="ADMIN">Admin</option>
                    <option value="MEMBER">Member</option>
                    <option value="OBSERVER">Observer</option>
                  </select>
                </td>
                <td><span class="badge" [class]="'badge-' + member.status?.toLowerCase()">{{ member.status }}</span></td>
                <td>
                  <button class="btn btn-danger btn-sm" (click)="removeMember(member)" data-testid="button-remove-member">Remove</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Create/Edit Task Modal -->
    <div *ngIf="showTaskModal" class="modal-overlay" (click)="closeTaskModal()">
      <div class="modal" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h2 class="modal-title">{{ editingTask ? 'Edit Task' : 'Create Task' }}</h2>
        </div>
        
        <form (ngSubmit)="saveTask()">
          <div class="form-group">
            <label>Task Name *</label>
            <input type="text" [(ngModel)]="taskForm.taskName" name="taskName" required data-testid="input-task-name">
          </div>
          <div class="form-group">
            <label>Description</label>
            <textarea [(ngModel)]="taskForm.taskDescription" name="taskDescription" rows="3" data-testid="input-task-desc"></textarea>
          </div>
          <div class="form-group">
            <label>Status</label>
            <select [(ngModel)]="taskForm.taskStatus" name="taskStatus" data-testid="select-task-status">
              <option value="TODO">To Do</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="COMPLETED">Completed</option>
            </select>
          </div>
          <div class="form-group">
            <label>Priority</label>
            <select [(ngModel)]="taskForm.taskPriority" name="taskPriority" data-testid="select-task-priority">
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>
          <div class="form-group">
            <label>Due Date</label>
            <input type="date" [(ngModel)]="taskForm.dueDate" name="dueDate" data-testid="input-task-due">
          </div>
          <div class="form-group">
            <label>Assign To</label>
            <select [(ngModel)]="taskForm.assignedTo" name="assignedTo" data-testid="select-task-assignee">
              <option [value]="null">Unassigned</option>
              <option *ngFor="let u of users" [value]="u.userId">{{ u.name }}</option>
            </select>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="closeTaskModal()">Cancel</button>
            <button type="submit" class="btn btn-primary" data-testid="button-save-task">Save</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Invite Member Modal -->
    <div *ngIf="showInviteModal" class="modal-overlay" (click)="showInviteModal = false">
      <div class="modal" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h2 class="modal-title">Invite Member</h2>
        </div>
        
        <form (ngSubmit)="inviteMember()">
          <div class="form-group">
            <label>Email *</label>
            <input type="email" [(ngModel)]="inviteForm.email" name="email" required data-testid="input-invite-email">
          </div>
          <div class="form-group">
            <label>Role</label>
            <select [(ngModel)]="inviteForm.role" name="role" data-testid="select-invite-role">
              <option value="MEMBER">Member</option>
              <option value="ADMIN">Admin</option>
              <option value="OBSERVER">Observer</option>
            </select>
          </div>

          <div *ngIf="inviteError" class="error-message">{{ inviteError }}</div>
          <div *ngIf="inviteSuccess" class="success-message">{{ inviteSuccess }}</div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="showInviteModal = false">Cancel</button>
            <button type="submit" class="btn btn-primary" data-testid="button-send-invite">Send Invite</button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .project-info { margin-bottom: 20px; }
    .actions-bar { margin-bottom: 20px; }
    .tab-content { margin-top: 20px; }
    .btn-sm { padding: 5px 10px; font-size: 12px; }
    .btn-icon { 
      background: none; 
      border: none; 
      color: #667eea; 
      font-size: 12px; 
      cursor: pointer; 
      margin-right: 5px;
    }
    .btn-icon:hover { text-decoration: underline; }
    .task-actions { margin-top: 8px; }
    .error-message { background: #fee; color: #c00; padding: 10px; border-radius: 6px; margin-bottom: 15px; }
    .success-message { background: #efe; color: #060; padding: 10px; border-radius: 6px; margin-bottom: 15px; }
  `]
})
export class ProjectDetailComponent implements OnInit {
  projectName = '';
  project: Project | null = null;
  tasks: Task[] = [];
  members: ProjectMember[] = [];
  users: User[] = [];
  activeTab = 'tasks';

  showTaskModal = false;
  showInviteModal = false;
  editingTask: Task | null = null;

  taskForm: Partial<CreateTaskRequest> = {
    taskName: '',
    taskDescription: '',
    taskStatus: 'TODO',
    taskPriority: 'MEDIUM',
    dueDate: '',
    assignedTo: undefined
  };

  inviteForm = { email: '', role: 'MEMBER' };
  inviteError = '';
  inviteSuccess = '';

  get todoTasks(): Task[] { return this.tasks.filter(t => t.taskStatus === 'TODO'); }
  get inProgressTasks(): Task[] { return this.tasks.filter(t => t.taskStatus === 'IN_PROGRESS'); }
  get completedTasks(): Task[] { return this.tasks.filter(t => t.taskStatus === 'COMPLETED'); }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService,
    private taskService: TaskService,
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.projectName = this.route.snapshot.paramMap.get('name') || '';
    this.loadProject();
    this.loadTasks();
    this.loadMembers();
    this.loadUsers();
  }

  loadProject(): void {
    this.projectService.getProjectByName(this.projectName).subscribe({
      next: (res) => { if (res.data) this.project = res.data; },
      error: (err) => console.error(err)
    });
  }

  loadTasks(): void {
    this.taskService.getTasksByProjectName(this.projectName).subscribe({
      next: (res) => { if (res.data) this.tasks = res.data; },
      error: (err) => console.error(err)
    });
  }

  loadMembers(): void {
    this.projectService.getProjectMembers(this.projectName).subscribe({
      next: (res) => { if (res.data) this.members = res.data; },
      error: (err) => console.error(err)
    });
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (res) => { if (res.data) this.users = res.data; },
      error: (err) => console.error(err)
    });
  }

  goBack(): void {
    this.router.navigate(['/home/projects']);
  }

  editTask(task: Task): void {
    this.editingTask = task;
    this.taskForm = {
      taskName: task.taskName,
      taskDescription: task.taskDescription,
      taskStatus: task.taskStatus,
      taskPriority: task.taskPriority,
      dueDate: task.dueDate,
      assignedTo: task.assignedTo
    };
    this.showTaskModal = true;
  }

  closeTaskModal(): void {
    this.showTaskModal = false;
    this.editingTask = null;
    this.taskForm = { taskName: '', taskDescription: '', taskStatus: 'TODO', taskPriority: 'MEDIUM', dueDate: '', assignedTo: undefined };
  }

  saveTask(): void {
    if (!this.taskForm.taskName) return;

    const userId = this.authService.currentUser?.userId;
    if (!userId || !this.project) return;

    if (this.editingTask) {
      this.taskService.updateTask(this.editingTask.taskId, this.taskForm as CreateTaskRequest, userId).subscribe({
        next: () => { this.closeTaskModal(); this.loadTasks(); },
        error: (err) => console.error(err)
      });
    } else {
      const request: CreateTaskRequest = {
        ...(this.taskForm as CreateTaskRequest),
        projectId: this.project.projectId,
        createdBy: userId
      };
      this.taskService.createTask(request).subscribe({
        next: () => { this.closeTaskModal(); this.loadTasks(); },
        error: (err) => console.error(err)
      });
    }
  }

  deleteTask(task: Task): void {
    if (confirm(`Delete task "${task.taskName}"?`)) {
      this.taskService.deleteTask(task.taskId).subscribe({
        next: () => this.loadTasks(),
        error: (err) => console.error(err)
      });
    }
  }

  inviteMember(): void {
    this.inviteError = '';
    this.inviteSuccess = '';

    if (!this.inviteForm.email) {
      this.inviteError = 'Email is required';
      return;
    }

    const request: InviteRequest = {
      email: this.inviteForm.email,
      projectName: this.projectName,
      role: this.inviteForm.role,
      invitedBy: this.authService.currentUser?.email || ''
    };

    this.projectService.inviteMember(request).subscribe({
      next: () => {
        this.inviteSuccess = 'Invitation sent successfully!';
        this.inviteForm = { email: '', role: 'MEMBER' };
        this.loadMembers();
      },
      error: (err) => {
        this.inviteError = err.error?.message || 'Failed to send invitation';
      }
    });
  }

  updateMemberRole(member: ProjectMember): void {
    this.projectService.updateMemberRole(this.projectName, member.email, member.role).subscribe({
      next: () => this.loadMembers(),
      error: (err) => console.error(err)
    });
  }

  removeMember(member: ProjectMember): void {
    if (confirm(`Remove ${member.name} from the project?`)) {
      this.projectService.removeMember(this.projectName, member.email).subscribe({
        next: () => this.loadMembers(),
        error: (err) => console.error(err)
      });
    }
  }
}
