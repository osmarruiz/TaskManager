import taskManagerApi from './TaskManagerApiService';
import { Task } from '../model/task.model';

export const getTasks = async (): Promise<Task[]> => {
  const response = await taskManagerApi.api.get<Task[]>('/tasks');
  return response.data;
};

export const getTask = async (id: number): Promise<Task> => {
  const response = await taskManagerApi.api.get<Task>(`/tasks/${id}`);
  return response.data;
};

export const createTask = async (task: Task): Promise<Task> => {
  const response = await taskManagerApi.api.post<Task>('/tasks', task);
  return response.data;
};

export const updateTask = async (id: number, task: Task): Promise<Task> => {
  const response = await taskManagerApi.api.put<Task>(`/tasks/${id}`, task);
  return response.data;
};

export const deleteTask = async (id: number): Promise<void> => {
  await taskManagerApi.api.delete(`/tasks/${id}`);
};

export const archiveTask = async (id: number): Promise<void> => {
  await taskManagerApi.api.post(`/tasks/${id}/archive`);
};
