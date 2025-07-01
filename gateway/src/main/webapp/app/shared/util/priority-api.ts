import taskManagerApi from './TaskManagerApiService';
import { Priority } from '../model/priority.model';

export const getPriorities = async (): Promise<Priority[]> => {
  const response = await taskManagerApi.api.get<Priority[]>('/api/priorities');
  return response.data;
};

export const createPriority = async (priority: Priority): Promise<Priority> => {
  const response = await taskManagerApi.api.post<Priority>('/api/priorities', priority);
  return response.data;
};

export const updatePriority = async (id: number, priority: Priority): Promise<Priority> => {
  const response = await taskManagerApi.api.put<Priority>(`/api/priorities/${id}`, priority);
  return response.data;
};

export const deletePriority = async (id: number): Promise<void> => {
  await taskManagerApi.api.delete(`/api/priorities/${id}`);
};

export const getPriority = async (id: number): Promise<Priority> => {
  const response = await taskManagerApi.api.get<Priority>(`/api/priorities/${id}`);
  return response.data;
};
