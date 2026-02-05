import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, CreateTaskRequest, TaskHistory } from '../models/task.model';
import { ApiResponse } from '../models/api-response.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = `${environment.apiUrl}/task`;

  constructor(private http: HttpClient) {}

  getAllTasks(): Observable<ApiResponse<Task[]>> {
    return this.http.get<ApiResponse<Task[]>>(`${this.apiUrl}/all`);
  }

  getTaskById(taskId: number): Observable<ApiResponse<Task>> {
    return this.http.get<ApiResponse<Task>>(`${this.apiUrl}/${taskId}`);
  }

  getTasksByProject(projectId: number): Observable<ApiResponse<Task[]>> {
    return this.http.get<ApiResponse<Task[]>>(`${this.apiUrl}/project/${projectId}`);
  }

  getTasksByProjectName(projectName: string): Observable<ApiResponse<Task[]>> {
    return this.http.get<ApiResponse<Task[]>>(`${this.apiUrl}/project/name/${projectName}`);
  }

  getTasksByUser(userId: number): Observable<ApiResponse<Task[]>> {
    return this.http.get<ApiResponse<Task[]>>(`${this.apiUrl}/user/${userId}`);
  }

  getTasksByStatus(status: string): Observable<ApiResponse<Task[]>> {
    return this.http.get<ApiResponse<Task[]>>(`${this.apiUrl}/status/${status}`);
  }

  createTask(request: CreateTaskRequest): Observable<ApiResponse<Task>> {
    return this.http.post<ApiResponse<Task>>(`${this.apiUrl}/create`, request);
  }

  updateTask(taskId: number, request: Partial<CreateTaskRequest>, updatedBy: number): Observable<ApiResponse<Task>> {
    const params = new HttpParams().set('updatedBy', updatedBy.toString());
    return this.http.put<ApiResponse<Task>>(`${this.apiUrl}/${taskId}`, request, { params });
  }

  deleteTask(taskId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${taskId}`);
  }

  getTaskHistory(taskId: number): Observable<ApiResponse<TaskHistory[]>> {
    return this.http.get<ApiResponse<TaskHistory[]>>(`${this.apiUrl}/${taskId}/history`);
  }

  getDashboardStats(email: string): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/dashboard/${email}`);
  }
}
