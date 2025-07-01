import taskManagerApi from './TaskManagerApiService';
import { Priority } from '../model/priority.model';

export const getPriorities = async (): Promise<Priority[]> => {
  const response = await taskManagerApi.api.get<Priority[]>('/api/priorities');
  return response.data;
};
