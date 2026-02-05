import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project, ProjectMember, InviteRequest } from '../models/project.model';
import { ApiResponse } from '../models/api-response.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = `${environment.apiUrl}/project`;

  constructor(private http: HttpClient) {}

  getAllProjects(): Observable<ApiResponse<Project[]>> {
    return this.http.get<ApiResponse<Project[]>>(`${this.apiUrl}/all`);
  }

  getProjectByName(projectName: string): Observable<ApiResponse<Project>> {
    return this.http.get<ApiResponse<Project>>(`${this.apiUrl}/name/${projectName}`);
  }

  getUserProjects(email: string): Observable<ApiResponse<Project[]>> {
    return this.http.get<ApiResponse<Project[]>>(`${this.apiUrl}/user/${email}`);
  }

  createProject(project: Partial<Project>, creatorEmail: string): Observable<ApiResponse<Project>> {
    const params = new HttpParams().set('creatorEmail', creatorEmail);
    return this.http.post<ApiResponse<Project>>(`${this.apiUrl}/create`, project, { params });
  }

  updateProject(projectName: string, project: Partial<Project>): Observable<ApiResponse<Project>> {
    return this.http.put<ApiResponse<Project>>(`${this.apiUrl}/${projectName}`, project);
  }

  deleteProject(projectName: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${projectName}`);
  }

  getProjectMembers(projectName: string): Observable<ApiResponse<ProjectMember[]>> {
    return this.http.get<ApiResponse<ProjectMember[]>>(`${this.apiUrl}/${projectName}/members`);
  }

  getMemberRole(projectName: string, email: string): Observable<ApiResponse<{role: string}>> {
    return this.http.get<ApiResponse<{role: string}>>(`${this.apiUrl}/${projectName}/member-role/${email}`);
  }

  inviteMember(request: InviteRequest): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/invite`, request);
  }

  updateMemberRole(projectName: string, email: string, role: string): Observable<ApiResponse<void>> {
    const params = new HttpParams().set('role', role);
    return this.http.put<ApiResponse<void>>(`${this.apiUrl}/${projectName}/member-role/${email}`, {}, { params });
  }

  removeMember(projectName: string, email: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${projectName}/member/${email}`);
  }
}
