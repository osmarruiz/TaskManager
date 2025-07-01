import taskManagerApi from './TaskManagerApiService';
import { Comment } from '../model/comment.model';

export const getTaskComments = async (taskId: number): Promise<Comment[]> => {
  const response = await taskManagerApi.api.get<Comment[]>(`/api/tasks/${taskId}/comments`);
  return response.data;
};

export const addCommentToTask = async (taskId: number, content: string): Promise<Comment> => {
  const response = await taskManagerApi.api.post<Comment>(`/api/tasks/${taskId}/comments`, content, {
    headers: { 'Content-Type': 'text/plain' },
  });
  return response.data;
};
