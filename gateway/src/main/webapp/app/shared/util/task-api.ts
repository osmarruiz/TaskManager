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
  await taskManagerApi.api.delete(`/archived-tasks/${id}`);
};

export const archiveTask = async (id: number): Promise<void> => {
  await taskManagerApi.api.post(`/tasks/${id}/archive`);
};

export const unarchiveTask = async (id: number): Promise<void> => {
  await taskManagerApi.api.post(`/tasks/${id}/unarchive`);
};

export const changeTaskPriority = async (id: number, priority: string): Promise<void> => {
  await taskManagerApi.api.put(`/tasks/${id}/priority?priority=${encodeURIComponent(priority)}`);
};

export const changeTaskStatus = async (id: number, status: string): Promise<void> => {
  await taskManagerApi.api.put(`/tasks/${id}/status?status=${encodeURIComponent(status)}`);
};

export const getMyTasks = async (): Promise<Task[]> => {
  const response = await taskManagerApi.api.get<Task[]>('/tasks/my-tasks');
  return response.data;
};
