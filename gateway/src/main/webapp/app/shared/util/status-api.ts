import taskManagerApi from './TaskManagerApiService';
import { TaskStatusCatalog, CreateTaskStatusCatalog } from '../model/status.model';

export const getStatuses = async (): Promise<TaskStatusCatalog[]> => {
  const response = await taskManagerApi.api.get<TaskStatusCatalog[]>('/task-status-catalogs');
  return response.data;
};

export const createStatus = async (status: Partial<CreateTaskStatusCatalog>): Promise<TaskStatusCatalog> => {
  const payload = {
    name: status.name,
    description: status.description,
  };
  const response = await taskManagerApi.api.post<TaskStatusCatalog>('/task-status-catalogs', payload);
  return response.data;
};

export const updateStatus = async (id: number, status: Partial<CreateTaskStatusCatalog>): Promise<TaskStatusCatalog> => {
  const payload = {
    name: status.name,
    description: status.description,
  };
  const response = await taskManagerApi.api.put<TaskStatusCatalog>(`/task-status-catalogs/${id}`, payload);
  return response.data;
};

export const deleteStatus = async (id: number): Promise<void> => {
  await taskManagerApi.api.delete(`/task-status-catalogs/${id}`);
};
