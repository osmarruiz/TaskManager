import taskManagerApi from './TaskManagerApiService';
import { TaskStatusCatalog, CreateTaskStatusCatalog } from '../model/status.model';

export const getStatuses = async (): Promise<TaskStatusCatalog[]> => {
  const response = await taskManagerApi.api.get<TaskStatusCatalog[]>('/api/task-status-catalogs');
  return response.data;
};

export const createStatus = async (status: CreateTaskStatusCatalog): Promise<TaskStatusCatalog> => {
  const response = await taskManagerApi.api.post<TaskStatusCatalog>('/api/task-status-catalogs', status);
  return response.data;
};

export const updateStatus = async (id: number, status: CreateTaskStatusCatalog): Promise<TaskStatusCatalog> => {
  const response = await taskManagerApi.api.put<TaskStatusCatalog>(`/api/task-status-catalogs/${id}`, status);
  return response.data;
};

export const deleteStatus = async (id: number): Promise<void> => {
  await taskManagerApi.api.delete(`/api/task-status-catalogs/${id}`);
};
