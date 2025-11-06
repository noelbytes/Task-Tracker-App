export interface Task {
  id?: number;
  title: string;
  description: string;
  status: TaskStatus;
  priority: TaskPriority;
  createdAt?: string;
  completedAt?: string;
}

export enum TaskStatus {
  TODO = 'TODO',
  IN_PROGRESS = 'IN_PROGRESS',
  DONE = 'DONE'
}

export enum TaskPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH'
}

export interface TaskStats {
  totalTasks: number;
  completedTasks: number;
  pendingTasks: number;
  averageCompletionTimeHours: number;
  todoTasks: number;
  inProgressTasks: number;
}

