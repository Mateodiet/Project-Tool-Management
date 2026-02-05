import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

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
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login successfully', () => {
    const mockResponse = {
      status: 'OK',
      message: 'Login successful',
      data: { userId: 1, name: 'Test', email: 'test@test.com', isActive: true }
    };

    service.login({ email: 'test@test.com', password: 'password' }).subscribe(response => {
      expect(response.data).toBeTruthy();
      expect(response.data.email).toBe('test@test.com');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/user/login`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should register successfully', () => {
    const mockResponse = {
      status: 'OK',
      message: 'User registered',
      data: { userId: 1, name: 'Test', email: 'test@test.com', isActive: true }
    };

    service.register({ name: 'Test', email: 'test@test.com', password: 'password' }).subscribe(response => {
      expect(response.data).toBeTruthy();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/user/register`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should logout and clear localStorage', () => {
    localStorage.setItem('currentUser', JSON.stringify({ userId: 1 }));
    service.logout();
    expect(localStorage.getItem('currentUser')).toBeNull();
    expect(service.currentUser).toBeNull();
  });

  it('should return isLoggedIn false when no user', () => {
    expect(service.isLoggedIn).toBeFalse();
  });

  it('should store user in localStorage on login', () => {
    const mockResponse = {
      status: 'OK',
      message: 'Login successful',
      data: { userId: 1, name: 'Test', email: 'test@test.com', isActive: true }
    };

    service.login({ email: 'test@test.com', password: 'password' }).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/user/login`);
    req.flush(mockResponse);

    expect(localStorage.getItem('currentUser')).toBeTruthy();
  });
});
