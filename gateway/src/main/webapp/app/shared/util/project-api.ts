import taskManagerApi from './TaskManagerApiService';
import { Project } from '../model/project.model';
import { Task } from '../model/task.model';
import { ProjectMember } from '../model/project-member.model';

export const getProjects = async (): Promise<Project[]> => {
  const response = await taskManagerApi.api.get<Project[]>('/projects');
  return response.data;
};

export const createProject = async (project: Project): Promise<Project> => {
  const response = await taskManagerApi.api.post<Project>('/projects', project);
  return response.data;
};

export const updateProject = async (id: number, project: Project): Promise<Project> => {
  const response = await taskManagerApi.api.put<Project>(`/projects/${id}`, project);
  return response.data;
};

export const deleteProject = async (id: number): Promise<void> => {
  await taskManagerApi.api.delete(`/projects/${id}`);
};

export const getProjectTasks = async (id: number): Promise<Task[]> => {
  const response = await taskManagerApi.api.get<Task[]>(`/projects/${id}/tasks`);
  return response.data;
};

export const addTaskToProject = async (id: number, task: Task): Promise<Task> => {
  const response = await taskManagerApi.api.post<Task>(`/projects/${id}/add-task`, task);
  return response.data;
};

export const removeTaskFromProject = async (id: number, taskId: number): Promise<void> => {
  await taskManagerApi.api.delete(`/projects/${id}/remove-task/${taskId}`);
};

export const assignUserToProject = async (id: number, userLogin: string): Promise<void> => {
  await taskManagerApi.api.post(`/projects/${id}/assign-user`, { userLogin });
};

export const getProjectsByWorkGroupId = async (workGroupId: number): Promise<Project[]> => {
  const response = await taskManagerApi.api.get<Project[]>(`/projects/by-workgroup/${workGroupId}`);
  return response.data;
};

export const getProjectMembers = async (projectId: number): Promise<ProjectMember[]> => {
  const response = await taskManagerApi.api.get<ProjectMember[]>(`/projects/${projectId}/members`);
  return response.data;
};
