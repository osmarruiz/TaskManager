import taskManagerApi from './TaskManagerApiService';
import { Project } from '../model/project.model';

export const getProjects = async (): Promise<Project[]> => {
  const response = await taskManagerApi.api.get<Project[]>('/api/projects');
  return response.data;
};
