export interface User {
  userId: number;
  name: string;
  email: string;
  contactNumber?: string;
  isActive: boolean;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  contactNumber?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}
