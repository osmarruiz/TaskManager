import { WorkGroup } from './work-group.model';
import { Priority } from './priority.model';
import { TaskStatusCatalog } from './task-status.model';
import { Project } from './project.model';

export interface Task {
  id?: number;
  title: string;
  description: string;
  createTime: string;
  updateTime: string;
  deadline?: string;
  archived?: boolean;
  archivedDate?: string;
  workGroup: WorkGroup;
  priority: Priority;
  status: TaskStatusCatalog;
  parentProject?: Project;
}
