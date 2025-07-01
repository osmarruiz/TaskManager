import taskManagerApi from './TaskManagerApiService';
import { WorkGroup } from '../model/work-group.model';

export const getWorkGroups = async (): Promise<WorkGroup[]> => {
  const response = await taskManagerApi.api.get<WorkGroup[]>('/api/work-groups');
  return response.data;
};
