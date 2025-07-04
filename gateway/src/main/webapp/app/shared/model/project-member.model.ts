import { IUser } from './user.model';

export interface ProjectMember {
  id?: number;
  user: IUser;
  assignedAt: string;
}
