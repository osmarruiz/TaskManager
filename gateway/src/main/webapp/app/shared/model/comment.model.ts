import { Task } from './task.model';
import { IUser } from './user.model';

export interface Comment {
  id?: number;
  content: string;
  createTime: string;
  lastEditTime?: string;
  task: Task;
  author: IUser;
}
