import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { 
    path: 'login', 
    loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent) 
  },
  { 
    path: 'register', 
    loadComponent: () => import('./components/register/register.component').then(m => m.RegisterComponent) 
  },
  { 
    path: 'home', 
    loadComponent: () => import('./components/home/home.component').then(m => m.HomeComponent),
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { 
        path: 'dashboard', 
        loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent) 
      },
      { 
        path: 'users', 
        loadComponent: () => import('./components/users/users.component').then(m => m.UsersComponent) 
      },
      { 
        path: 'projects', 
        loadComponent: () => import('./components/projects/projects.component').then(m => m.ProjectsComponent) 
      },
      { 
        path: 'project/:name', 
        loadComponent: () => import('./components/project-detail/project-detail.component').then(m => m.ProjectDetailComponent) 
      }
    ]
  },
  { path: '**', redirectTo: '/login' }
];
