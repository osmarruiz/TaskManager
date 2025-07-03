import taskManagerApi from './TaskManagerApiService';
import { TaskStatusCatalog } from '../model/task-status.model';

export const getTaskStatuses = async (): Promise<TaskStatusCatalog[]> => {
  const response = await taskManagerApi.api.get<TaskStatusCatalog[]>('/task-status-catalogs');
  return response.data;
};
