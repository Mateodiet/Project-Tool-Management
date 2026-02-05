import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ProjectsComponent } from './projects.component';
import { ProjectService } from '../../services/project.service';
import { AuthService } from '../../services/auth.service';
import { of } from 'rxjs';

describe('ProjectsComponent', () => {
  let component: ProjectsComponent;
  let fixture: ComponentFixture<ProjectsComponent>;
  let projectServiceSpy: jasmine.SpyObj<ProjectService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    projectServiceSpy = jasmine.createSpyObj('ProjectService', ['getAllProjects', 'createProject', 'deleteProject']);
    authServiceSpy = jasmine.createSpyObj('AuthService', [], {
      currentUser: { userId: 1, email: 'test@test.com', name: 'Test', isActive: true }
    });

    projectServiceSpy.getAllProjects.and.returnValue(of({
      status: 'OK',
      message: 'Success',
      data: [
        { projectId: 1, projectName: 'Project 1', projectStatus: 'ACTIVE' },
        { projectId: 2, projectName: 'Project 2', projectStatus: 'ACTIVE' }
      ]
    }));

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
    projectServiceSpy.createProject.and.returnValue(of({
      status: 'OK',
      message: 'Created',
      data: { projectId: 3, projectName: 'New Project' }
    }));

    component.newProject = { projectName: 'New Project', projectDescription: 'Desc' };
    component.createProject();

    expect(projectServiceSpy.createProject).toHaveBeenCalled();
  });
});
