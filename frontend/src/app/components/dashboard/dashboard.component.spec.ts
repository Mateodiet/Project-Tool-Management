import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DashboardComponent } from './dashboard.component';
import { TaskService } from '../../services/task.service';
import { AuthService } from '../../services/auth.service';
import { of } from 'rxjs';
import { ApiResponse } from '../../models/api-response.model';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let taskServiceSpy: jasmine.SpyObj<TaskService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    taskServiceSpy = jasmine.createSpyObj('TaskService', ['getDashboardStats']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout'], {
      currentUser: { userId: 1, email: 'test@test.com', name: 'Test', isActive: true },
      isLoggedIn: true
    });

    const mockDashboardResponse: ApiResponse<any> = {
      status: 'OK',
      message: 'Success',
      data: {
        totalProjects: 5,
        todoTasks: 3,
        inProgressTasks: 2,
        completedTasks: 7,
        totalTasks: 12,
        tasksByStatus: {
          TODO: [{ taskId: 1, taskName: 'Task 1', taskStatus: 'TODO', taskPriority: 'HIGH', taskDescription: 'Test' }],
          IN_PROGRESS: [],
          COMPLETED: []
        }
      }
    };

    taskServiceSpy.getDashboardStats.and.returnValue(of(mockDashboardResponse));

    await TestBed.configureTestingModule({
      imports: [DashboardComponent, HttpClientTestingModule],
      providers: [
        { provide: TaskService, useValue: taskServiceSpy },
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

  it('should load dashboard stats on init', () => {
    expect(taskServiceSpy.getDashboardStats).toHaveBeenCalled();
    expect(component.stats.totalProjects).toBe(5);
    expect(component.stats.todoTasks).toBe(3);
  });

  it('should populate todo tasks', () => {
    expect(component.todoTasks.length).toBe(1);
    expect(component.todoTasks[0].taskName).toBe('Task 1');
  });

  it('should have empty arrays for in progress and completed initially', () => {
    expect(component.inProgressTasks.length).toBe(0);
    expect(component.completedTasks.length).toBe(0);
  });
});