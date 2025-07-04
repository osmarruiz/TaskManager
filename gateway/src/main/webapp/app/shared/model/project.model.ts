import { WorkGroup } from './work-group.model';

export interface Project {
  id?: number;
  title: string;
  description: string;
  startDate?: string;
  endDate?: string;
  workGroup: WorkGroup;
}
