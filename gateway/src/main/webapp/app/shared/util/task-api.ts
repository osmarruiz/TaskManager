import taskManagerApi from './TaskManagerApiService';
import { Task } from '../model/task.model';

export const getTasks = async (): Promise<Task[]> => {
  const response = await taskManagerApi.api.get<Task[]>('/api/tasks');
  return response.data;
};

export const getTask = async (id: number): Promise<Task> => {
  const response = await taskManagerApi.api.get<Task>(`/api/tasks/${id}`);
  return response.data;
};

export const createTask = async (task: Task): Promise<Task> => {
  const response = await taskManagerApi.api.post<Task>('/api/tasks', task);
  return response.data;
};

export const updateTask = async (id: number, task: Task): Promise<Task> => {
  const response = await taskManagerApi.api.put<Task>(`/api/tasks/${id}`, task);
  return response.data;
};

export const deleteTask = async (id: number): Promise<void> => {
  await taskManagerApi.api.delete(`/api/tasks/${id}`);
};

export const archiveTask = async (id: number): Promise<void> => {
  await taskManagerApi.api.post(`/api/tasks/${id}/archive`);
};
