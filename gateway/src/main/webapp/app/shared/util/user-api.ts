import taskManagerApi from './TaskManagerApiService';
import { IUser } from '../model/user.model';

export const getUsers = async (): Promise<IUser[]> => {
  const response = await taskManagerApi.api.get<IUser[]>('/api/users');
  return response.data;
};
