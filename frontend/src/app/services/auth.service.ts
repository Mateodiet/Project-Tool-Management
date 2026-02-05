import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { User, LoginRequest, RegisterRequest } from '../models/user.model';
import { ApiResponse } from '../models/api-response.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/user`;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }

  get currentUser(): User | null {
    return this.currentUserSubject.value;
  }

  get isLoggedIn(): boolean {
    return !!this.currentUser;
  }

  login(request: LoginRequest): Observable<ApiResponse<User>> {
    return this.http.post<ApiResponse<User>>(`${this.apiUrl}/login`, request).pipe(
      tap(response => {
        if (response.data) {
          localStorage.setItem('currentUser', JSON.stringify(response.data));
          this.currentUserSubject.next(response.data);
        }
      })
    );
  }

  register(request: RegisterRequest): Observable<ApiResponse<User>> {
    return this.http.post<ApiResponse<User>>(`${this.apiUrl}/register`, request);
  }

  logout(): void {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  updateProfile(userId: number, request: Partial<RegisterRequest>): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>(`${this.apiUrl}/${userId}`, request).pipe(
      tap(response => {
        if (response.data) {
          localStorage.setItem('currentUser', JSON.stringify(response.data));
          this.currentUserSubject.next(response.data);
        }
      })
    );
  }
}
