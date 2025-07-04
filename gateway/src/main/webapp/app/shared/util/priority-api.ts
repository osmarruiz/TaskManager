import taskManagerApi from './TaskManagerApiService';
import { Priority } from '../model/priority.model';

export const getPriorities = async (): Promise<Priority[]> => {
  const response = await taskManagerApi.api.get<Priority[]>('/priorities');
  return response.data;
};

export const createPriority = async (priority: Partial<Priority>): Promise<Priority> => {
  const payload = {
    name: priority.name,
    description: priority.description,
  };
  const response = await taskManagerApi.api.post<Priority>('/priorities', payload);
  return response.data;
};

export const updatePriority = async (id: number, priority: Partial<Priority>): Promise<Priority> => {
  const payload = {
    name: priority.name,
    description: priority.description,
  };
  const response = await taskManagerApi.api.put<Priority>(`/priorities/${id}`, payload);
  return response.data;
};

export const deletePriority = async (id: number): Promise<void> => {
  await taskManagerApi.api.delete(`/priorities/${id}`);
};

export const getPriority = async (id: number): Promise<Priority> => {
  const response = await taskManagerApi.api.get<Priority>(`/priorities/${id}`);
  return response.data;
};

export const updatePriorityVisibility = async (id: number, visible: boolean): Promise<Priority> => {
  const response = await taskManagerApi.api.patch<Priority>(`/priorities/${id}/visibility`, { visible });
  return response.data;
};
