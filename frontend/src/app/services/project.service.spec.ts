import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProjectService } from './project.service';
import { environment } from '../../environments/environment';

describe('ProjectService', () => {
  let service: ProjectService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProjectService]
    });
    service = TestBed.inject(ProjectService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all projects', () => {
    const mockResponse = {
      status: 'OK',
      message: 'Success',
      data: [{ projectId: 1, projectName: 'Test Project' }]
    };

    service.getAllProjects().subscribe(response => {
      expect(response.data.length).toBe(1);
      expect(response.data[0].projectName).toBe('Test Project');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/project/all`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should create a project', () => {
    const mockResponse = {
      status: 'OK',
      message: 'Project created',
      data: { projectId: 1, projectName: 'New Project' }
    };

    service.createProject({ projectName: 'New Project' }, 'test@test.com').subscribe(response => {
      expect(response.data.projectName).toBe('New Project');
    });

    const req = httpMock.expectOne(r => r.url.includes('/project/create'));
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should get project by name', () => {
    const mockResponse = {
      status: 'OK',
      message: 'Success',
      data: { projectId: 1, projectName: 'Test Project' }
    };

    service.getProjectByName('Test Project').subscribe(response => {
      expect(response.data.projectName).toBe('Test Project');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/project/name/Test Project`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should delete a project', () => {
    const mockResponse = { status: 'OK', message: 'Project deleted', data: null };

    service.deleteProject('Test Project').subscribe(response => {
      expect(response.message).toBe('Project deleted');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/project/Test Project`);
    expect(req.request.method).toBe('DELETE');
    req.flush(mockResponse);
  });

  it('should invite member', () => {
    const mockResponse = { status: 'OK', message: 'Invitation sent', data: {} };

    service.inviteMember({
      email: 'user@test.com',
      projectName: 'Test',
      role: 'MEMBER',
      invitedBy: 'admin@test.com'
    }).subscribe(response => {
      expect(response.message).toBe('Invitation sent');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/project/invite`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should get project members', () => {
    const mockResponse = {
      status: 'OK',
      message: 'Success',
      data: [{ userId: 1, email: 'user@test.com', role: 'ADMIN' }]
    };

    service.getProjectMembers('Test').subscribe(response => {
      expect(response.data.length).toBe(1);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/project/Test/members`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });
});
