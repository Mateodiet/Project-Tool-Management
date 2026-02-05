import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { ApiResponse } from '../../models/api-response.model';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent, HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show error when email is empty', () => {
    component.email = '';
    component.password = 'password';
    component.onSubmit();
    expect(component.error).toBeTruthy();
  });

  it('should show error when password is empty', () => {
    component.email = 'test@test.com';
    component.password = '';
    component.onSubmit();
    expect(component.error).toBeTruthy();
  });

  it('should call login service with valid credentials', () => {
    authServiceSpy.login.and.returnValue(of({
      status: 'OK',
      message: 'Success',
      data: { userId: 1, email: 'test@test.com', name: 'Test' }
    } as ApiResponse<any>));

    component.email = 'test@test.com';
    component.password = 'password123';
    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalledWith('test@test.com', 'password123');
  });

  it('should navigate to home on successful login', () => {
    spyOn(router, 'navigate');
    authServiceSpy.login.and.returnValue(of({
      status: 'OK',
      message: 'Success',
      data: { userId: 1, email: 'test@test.com', name: 'Test' }
    } as ApiResponse<any>));

    component.email = 'test@test.com';
    component.password = 'password123';
    component.onSubmit();

    expect(router.navigate).toHaveBeenCalledWith(['/home']);
  });

  it('should show error on invalid credentials', () => {
    authServiceSpy.login.and.returnValue(of({
      status: 'UNAUTHORIZED',
      message: 'Invalid credentials',
      data: null
    } as ApiResponse<any>));

    component.email = 'test@test.com';
    component.password = 'wrongpassword';
    component.onSubmit();

    expect(component.error).toBe('Invalid credentials');
  });

  it('should handle API error', () => {
    authServiceSpy.login.and.returnValue(throwError(() => ({ error: { message: 'Server error' } })));

    component.email = 'test@test.com';
    component.password = 'password123';
    component.onSubmit();

    expect(component.error).toBeTruthy();
  });

  it('should clear error on new attempt', () => {
    component.error = 'Previous error';
    authServiceSpy.login.and.returnValue(of({
      status: 'OK',
      message: 'Success',
      data: { userId: 1, email: 'test@test.com', name: 'Test' }
    } as ApiResponse<any>));

    component.email = 'test@test.com';
    component.password = 'password123';
    component.onSubmit();

    expect(component.error).toBe('');
  });
});