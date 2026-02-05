import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DashboardComponent } from './dashboard.component';
import { TaskService } from '../../services/task.service';
import { ProjectService } from '../../services/project.service';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { ApiResponse } from '../../models/api-response.model';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let taskServiceSpy: jasmine.SpyObj<TaskService>;
  let projectServiceSpy: jasmine.SpyObj<ProjectService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockTasks = [
    { taskId: 1, taskName: 'Task 1', taskStatus: 'TODO', taskPriority: 'HIGH', projectId: 1 },
    { taskId: 2, taskName: 'Task 2', taskStatus: 'IN_PROGRESS', taskPriority: 'MEDIUM', projectId: 1 },
    { taskId: 3, taskName: 'Task 3', taskStatus: 'DONE', taskPriority: 'LOW', projectId: 1 }
  ];

  const mockProjects = [
    { projectId: 1, projectName: 'Project 1', projectStatus: 'ACTIVE' }
  ];

  beforeEach(async () => {
    taskServiceSpy = jasmine.createSpyObj('TaskService', ['getAllTasks', 'getTasksByProject']);
    projectServiceSpy = jasmine.createSpyObj('ProjectService', ['getAllProjects']);
    authServiceSpy = jasmine.createSpyObj('AuthService', [], {
      currentUser: { userId: 1, email: 'test@test.com', name: 'Test' }
    });

    taskServiceSpy.getAllTasks.and.returnValue(of({
      status: 'OK',
      message: 'Success',
      data: mockTasks
    } as ApiResponse<any[]>));

    projectServiceSpy.getAllProjects.and.returnValue(of({
      status: 'OK',
      message: 'Success',
      data: mockProjects
    } as ApiResponse<any[]>));

    await TestBed.configureTestingModule({
      imports: [DashboardComponent, HttpClientTestingModule],
      providers: [
        { provide: TaskService, useValue: taskServiceSpy },
        { provide: ProjectService, useValue: projectServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load tasks on init', () => {
    expect(taskServiceSpy.getAllTasks).toHaveBeenCalled();
  });

  it('should filter tasks by status TODO', () => {
    component.tasks = mockTasks as any;
    component.filterByStatus('TODO');
    expect(component.filteredTasks.length).toBe(1);
  });

  it('should filter tasks by status IN_PROGRESS', () => {
    component.tasks = mockTasks as any;
    component.filterByStatus('IN_PROGRESS');
    expect(component.filteredTasks.length).toBe(1);
  });

  it('should filter tasks by status DONE', () => {
    component.tasks = mockTasks as any;
    component.filterByStatus('DONE');
    expect(component.filteredTasks.length).toBe(1);
  });

  it('should show all tasks when filter is ALL', () => {
    component.tasks = mockTasks as any;
    component.filterByStatus('ALL');
    expect(component.filteredTasks.length).toBe(3);
  });

  it('should handle empty task list', () => {
    component.tasks = [];
    component.filterByStatus('TODO');
    expect(component.filteredTasks.length).toBe(0);
  });

  it('should handle API error gracefully', () => {
    taskServiceSpy.getAllTasks.and.returnValue(throwError(() => new Error('API Error')));
    component.ngOnInit();
    expect(component.tasks.length).toBe(0);
  });
});