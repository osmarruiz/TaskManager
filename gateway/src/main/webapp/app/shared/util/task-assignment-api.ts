import taskManagerApi from './TaskManagerApiService';
import { TaskAssignment } from '../model/task-assignment.model';

export const getTaskAssignments = async (taskId: number): Promise<TaskAssignment[]> => {
  const response = await taskManagerApi.api.get<TaskAssignment[]>(`/tasks/${taskId}/assignments`);
  return response.data;
};

export const assignTask = async (taskId: number, userLogin: string): Promise<void> => {
  await taskManagerApi.api.post(`/tasks/${taskId}/assign/${userLogin}`);
};

export const unassignTask = async (taskId: number, userLogin: string): Promise<void> => {
  await taskManagerApi.api.post(`/tasks/${taskId}/unassign/${userLogin}`);
};
