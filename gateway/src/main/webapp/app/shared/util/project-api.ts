import taskManagerApi from './TaskManagerApiService';
import { Project } from '../model/project.model';
import { Task } from '../model/task.model';

export const getProjects = async (): Promise<Project[]> => {
  const response = await taskManagerApi.api.get<Project[]>('/api/projects');
  return response.data;
};

export const createProject = async (project: Project): Promise<Project> => {
  const response = await taskManagerApi.api.post<Project>('/api/projects', project);
  return response.data;
};

export const updateProject = async (id: number, project: Project): Promise<Project> => {
  const response = await taskManagerApi.api.put<Project>(`/api/projects/${id}`, project);
  return response.data;
};

export const deleteProject = async (id: number): Promise<void> => {
  await taskManagerApi.api.delete(`/api/projects/${id}`);
};

export const getProjectTasks = async (id: number): Promise<Task[]> => {
  const response = await taskManagerApi.api.get<Task[]>(`/api/projects/${id}/tasks`);
  return response.data;
};

export const addTaskToProject = async (id: number, task: Task): Promise<Task> => {
  const response = await taskManagerApi.api.post<Task>(`/api/projects/${id}/add-task`, task);
  return response.data;
};

export const removeTaskFromProject = async (id: number, taskId: number): Promise<void> => {
  await taskManagerApi.api.delete(`/api/projects/${id}/remove-task/${taskId}`);
};

export const assignUserToProject = async (id: number, userLogin: string): Promise<void> => {
  await taskManagerApi.api.post(`/api/projects/${id}/assign-user`, { userLogin });
};
