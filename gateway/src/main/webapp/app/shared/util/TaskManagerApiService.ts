import axios, { AxiosInstance } from 'axios';

class TaskManagerApiService {
  public api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: '/services/taskmanager/api',
    });
  }
}

const taskManagerApi = new TaskManagerApiService();
export default taskManagerApi;
