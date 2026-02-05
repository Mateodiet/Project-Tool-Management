import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login and store user', () => {
    const mockResponse = {
      status: 'OK',
      message: 'Success',
      data: { userId: 1, email: 'test@test.com', name: 'Test' }
    };

    service.login('test@test.com', 'password123').subscribe(response => {
      expect(response.status).toBe('OK');
      expect(service.currentUser).toBeTruthy();
    });

    const req = httpMock.expectOne('/api/users/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should logout and clear user', () => {
    service.logout();
    expect(service.currentUser).toBeNull();
  });

  it('should check if user is logged in', () => {
    expect(service.isLoggedIn()).toBeFalse();
  });

  it('should register new user', () => {
    const mockResponse = {
      status: 'CREATED',
      message: 'User registered',
      data: { userId: 1, email: 'new@test.com', name: 'New User' }
    };

    const userData = { name: 'New User', email: 'new@test.com', password: 'password123' };

    service.register(userData).subscribe(response => {
      expect(response.status).toBe('CREATED');
    });

    const req = httpMock.expectOne('/api/users/register');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });
});