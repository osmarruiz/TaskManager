import taskManagerApi from './TaskManagerApiService';
import { IUser } from '../model/user.model';

export const getUsers = async (): Promise<IUser[]> => {
  const response = await taskManagerApi.api.get<IUser[]>('/users');
  return response.data;
};
