export interface Project {
  projectId: number;
  projectName: string;
  projectDescription: string;
  projectStartDate: string;
  projectStatus: string;
  createdBy: number;
  creatorEmail?: string;
}

export interface ProjectMember {
  userId: number;
  email: string;
  name: string;
  role: string;
  status: string;
  joinedAt: string;
}

export interface InviteRequest {
  email: string;
  projectName: string;
  role: string;
  invitedBy: string;
}
