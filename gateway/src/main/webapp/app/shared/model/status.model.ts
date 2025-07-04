export interface TaskStatusCatalog {
  id?: number;
  name: string;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: any;
}

export interface CreateTaskStatusCatalog {
  name: string;
  description?: string;
}
