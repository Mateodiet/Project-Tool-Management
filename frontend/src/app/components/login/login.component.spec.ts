import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { LoginRequest } from '../../models/user.model';
import { ApiResponse } from '../../models/api-response.model';
import { User } from '../../models/user.model';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login'], {
      currentUser: null,
      isLoggedIn: false
    });

    await TestBed.configureTestingModule({
      imports: [LoginComponent, HttpClientTestingModule, RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show error when fields are empty', () => {
    component.email = '';
    component.password = '';
    component.onSubmit();
    expect(component.error).toBe('Please fill in all fields');
  });

  it('should call authService.login on submit', () => {
    const mockResponse: ApiResponse<User> = {
      status: 'OK',
      message: 'Success',
      data: { userId: 1, name: 'Test', email: 'test@test.com', isActive: true }
    };
    authServiceSpy.login.and.returnValue(of(mockResponse));

    component.email = 'test@test.com';
    component.password = 'password';
    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalled();
  });

  it('should show error on login failure', () => {
    authServiceSpy.login.and.returnValue(throwError(() => ({ error: { message: 'Invalid credentials' } })));

    component.email = 'test@test.com';
    component.password = 'wrongpassword';
    component.onSubmit();

    expect(component.error).toBe('Invalid credentials');
  });

  it('should have loading false initially', () => {
    expect(component.loading).toBeFalse();
  });
});