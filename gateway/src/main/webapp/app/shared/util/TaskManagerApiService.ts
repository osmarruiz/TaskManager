import axios, { AxiosInstance } from 'axios';

class TaskManagerApiService {
  public api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: 'http://localhost:8080/services/taskmanager',
    });
  }
}

const taskManagerApi = new TaskManagerApiService();
export default taskManagerApi;
