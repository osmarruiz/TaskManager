import { IUser } from './user.model';

export interface TaskStatusCatalog {
  id?: number;
  name: string;
  description?: string;
  createdAt: string;
  updatedAt?: string;
  createdBy: IUser;
}
