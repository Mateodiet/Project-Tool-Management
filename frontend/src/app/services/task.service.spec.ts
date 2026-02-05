import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskService } from './task.service';
import { environment } from '../../environments/environment';

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TaskService]
    });
    service = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all tasks', () => {
    const mockResponse = {
      status: 'OK',
      message: 'Success',
      data: [{ taskId: 1, taskName: 'Test Task' }]
    };

    service.getAllTasks().subscribe(response => {
      expect(response.data.length).toBe(1);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/task/all`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should create a task', () => {
    const mockResponse = {
      status: 'OK',
      message: 'Task created',
      data: { taskId: 1, taskName: 'New Task' }
    };

    service.createTask({
      taskName: 'New Task',
      taskDescription: 'Description',
      taskStatus: 'TODO',
      taskPriority: 'MEDIUM',
      dueDate: '2024-12-31',
      projectId: 1,
      createdBy: 1
    }).subscribe(response => {
      expect(response.data.taskName).toBe('New Task');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/task/create`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should get tasks by project', () => {
    const mockResponse = {
      status: 'OK',
      message: 'Success',
      data: [{ taskId: 1, taskName: 'Task 1' }]
    };

    service.getTasksByProject(1).subscribe(response => {
      expect(response.data.length).toBe(1);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/task/project/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should delete a task', () => {
    const mockResponse = { status: 'OK', message: 'Task deleted', data: null };

    service.deleteTask(1).subscribe(response => {
      expect(response.message).toBe('Task deleted');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/task/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(mockResponse);
  });

  it('should get task history', () => {
    const mockResponse = {
      status: 'OK',
      message: 'Success',
      data: [{ historyId: 1, fieldChanged: 'taskStatus' }]
    };

    service.getTaskHistory(1).subscribe(response => {
      expect(response.data.length).toBe(1);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/task/1/history`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should get dashboard stats', () => {
    const mockResponse = {
      status: 'OK',
      message: 'Success',
      data: { totalProjects: 5, todoTasks: 3 }
    };

    service.getDashboardStats('test@test.com').subscribe(response => {
      expect(response.data.totalProjects).toBe(5);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/task/dashboard/test@test.com`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });
});
