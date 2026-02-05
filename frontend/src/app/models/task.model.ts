export interface Task {
  taskId: number;
  taskName: string;
  taskDescription: string;
  taskStatus: string;
  taskPriority: string;
  dueDate: string;
  projectId: number;
  projectName?: string;
  assignedTo: number;
  assignedToName?: string;
  createdBy: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTaskRequest {
  taskName: string;
  taskDescription: string;
  taskStatus: string;
  taskPriority: string;
  dueDate: string;
  projectId: number;
  assignedTo?: number;
  createdBy: number;
}

export interface TaskHistory {
  historyId: number;
  taskId: number;
  fieldChanged: string;
  oldValue: string;
  newValue: string;
  changedBy: number;
  changedAt: string;
}
