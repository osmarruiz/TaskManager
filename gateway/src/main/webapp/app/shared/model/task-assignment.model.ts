import { Task } from './task.model';
import { IUser } from './user.model';

export interface TaskAssignment {
  id?: number;
  assignedAt: string;
  task: Task;
  user: IUser;
}
