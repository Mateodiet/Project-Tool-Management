import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', [], { isLoggedIn: false });
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    guard = TestBed.inject(AuthGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to login when not logged in', () => {
    const result = guard.canActivate();
    expect(result).toBeFalse();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should allow access when logged in', () => {
    (Object.getOwnPropertyDescriptor(authServiceSpy, 'isLoggedIn')?.get as jasmine.Spy).and.returnValue(true);
    
    const newGuard = new AuthGuard(authServiceSpy, routerSpy);
    const result = newGuard.canActivate();
    
    expect(result).toBeTrue();
  });
});
