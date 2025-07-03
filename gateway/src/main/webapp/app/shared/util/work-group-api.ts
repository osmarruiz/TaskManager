import taskManagerApi from './TaskManagerApiService';
import { WorkGroup } from '../model/work-group.model';
import { UserWorkGroup } from '../model/user-work-group.model';

export const getWorkGroups = async (): Promise<WorkGroup[]> => {
  const response = await taskManagerApi.api.get<WorkGroup[]>('/work-groups');
  return response.data;
};

export const createWorkGroup = async (workGroup: WorkGroup): Promise<WorkGroup> => {
  const response = await taskManagerApi.api.post<WorkGroup>('/work-groups', workGroup);
  return response.data;
};

export const updateWorkGroup = async (id: number, workGroup: WorkGroup): Promise<WorkGroup> => {
  const response = await taskManagerApi.api.put<WorkGroup>(`/work-groups/${id}`, workGroup);
  return response.data;
};

export const deleteWorkGroup = async (id: number): Promise<void> => {
  await taskManagerApi.api.delete(`/work-groups/${id}`);
};

export const getWorkGroup = async (id: number): Promise<WorkGroup> => {
  const response = await taskManagerApi.api.get<WorkGroup>(`/work-groups/${id}`);
  return response.data;
};

export const transferOwnership = async (id: number, newOwnerUserId: string): Promise<void> => {
  await taskManagerApi.api.put(`/work-groups/${id}/transfer-ownership/${newOwnerUserId}`);
};

export const addModerator = async (id: number, userId: string): Promise<void> => {
  await taskManagerApi.api.post(`/work-groups/${id}/moderators`, { userId });
};

export const removeModerator = async (id: number, userId: string): Promise<void> => {
  await taskManagerApi.api.delete(`/work-groups/${id}/moderators`, { data: { userId } });
};

export const addMember = async (id: number, userLogin: string): Promise<void> => {
  await taskManagerApi.api.post(`/work-groups/${id}/members`, { userLogin });
};

export const removeMember = async (id: number, userLogin: string): Promise<void> => {
  await taskManagerApi.api.delete(`/work-groups/${id}/members`, { data: { userLogin } });
};

export const getMembers = async (id: number): Promise<any[]> => {
  const response = await taskManagerApi.api.get<any[]>(`/work-groups/${id}/members`);
  return response.data;
};

export const leaveWorkGroup = async (id: number): Promise<void> => {
  await taskManagerApi.api.delete(`/work-groups/${id}/leave`);
};

export const getMyWorkGroups = async (): Promise<UserWorkGroup[]> => {
  const response = await taskManagerApi.api.get<UserWorkGroup[]>('/work-groups/mine');
  return response.data;
};
