import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ProjectsComponent } from './projects.component';
import { ProjectService } from '../../services/project.service';
import { AuthService } from '../../services/auth.service';
import { of } from 'rxjs';
import { ApiResponse } from '../../models/api-response.model';
import { Project } from '../../models/project.model';

describe('ProjectsComponent', () => {
  let component: ProjectsComponent;
  let fixture: ComponentFixture<ProjectsComponent>;
  let projectServiceSpy: jasmine.SpyObj<ProjectService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockProjects: Project[] = [
    { projectId: 1, projectName: 'Project 1', projectDescription: '', projectStartDate: '', projectStatus: 'ACTIVE', createdBy: 1 },
    { projectId: 2, projectName: 'Project 2', projectDescription: '', projectStartDate: '', projectStatus: 'ACTIVE', createdBy: 1 }
  ];

  beforeEach(async () => {
    projectServiceSpy = jasmine.createSpyObj('ProjectService', [
      'getAllProjects', 
      'createProject', 
      'deleteProject',
      'getMemberRole'
    ]);
    authServiceSpy = jasmine.createSpyObj('AuthService', [], {
      currentUser: { userId: 1, email: 'test@test.com', name: 'Test', isActive: true }
    });

    projectServiceSpy.getAllProjects.and.returnValue(of({
      status: 'OK',
      message: 'Success',
      data: mockProjects
    } as ApiResponse<Project[]>));

    projectServiceSpy.getMemberRole.and.returnValue(of({
      status: 'OK',
      message: 'Success',
      data: { role: 'ADMIN', email: 'test@test.com', projectName: 'Project 1' }
    } as ApiResponse<any>));

    projectServiceSpy.createProject.and.returnValue(of({
      status: 'OK',
      message: 'Created',
      data: { projectId: 3, projectName: 'New Project', projectDescription: '', projectStartDate: '', projectStatus: 'ACTIVE', createdBy: 1 }
    } as ApiResponse<Project>));

    projectServiceSpy.deleteProject.and.returnValue(of({
      status: 'OK',
      message: 'Deleted',
      data: null
    } as ApiResponse<any>));

    await TestBed.configureTestingModule({
      imports: [ProjectsComponent, HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: ProjectService, useValue: projectServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load projects on init', () => {
    expect(projectServiceSpy.getAllProjects).toHaveBeenCalled();
    expect(component.projects.length).toBe(2);
  });

  it('should load user roles for projects', () => {
    expect(projectServiceSpy.getMemberRole).toHaveBeenCalled();
  });

  it('should show create modal when button clicked', () => {
    component.showCreateModal = true;
    expect(component.showCreateModal).toBeTrue();
  });

  it('should validate project name before creating', () => {
    component.newProject = { projectName: '' };
    component.createProject();
    expect(component.error).toBe('Project name is required');
  });

  it('should create project when form is valid', () => {
    component.newProject = { projectName: 'New Project', projectDescription: 'Desc' };
    component.createProject();
    expect(projectServiceSpy.createProject).toHaveBeenCalled();
  });

  it('should check if user is project admin', () => {
    component.projectRoles.set('Project 1', 'ADMIN');
    const isAdmin = component.isProjectAdmin({ projectId: 1, projectName: 'Project 1' } as Project);
    expect(isAdmin).toBeTrue();
  });

  it('should return false for non-admin users', () => {
    component.projectRoles.set('Project 2', 'MEMBRE');
    const isAdmin = component.isProjectAdmin({ projectId: 2, projectName: 'Project 2' } as Project);
    expect(isAdmin).toBeFalse();
  });
});