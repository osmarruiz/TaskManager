import axios, { AxiosResponse, AxiosError } from 'axios';
import { showApiError, showSuccessMessage, ERROR_MESSAGES, handleNetworkError } from './error-utils';

// Interfaz para los datos de error de la API
interface ApiErrorData {
  message?: string;
  detail?: string;
  error?: string;
  title?: string;
}

// Función genérica para manejar respuestas de API
export const handleApiResponse = <T>(response: AxiosResponse<T>, successMessage?: string, context?: string): T => {
  if (successMessage) {
    showSuccessMessage(successMessage);
  }
  return response.data;
};

// Función genérica para manejar errores de API
export const handleApiError = (error: AxiosError, context?: string): void => {
  if (error.response) {
    const { status, data } = error.response;
    const errorData = data as ApiErrorData;
    showApiError({ status, message: errorData?.message || errorData?.detail }, context);
  } else if (error.request) {
    handleNetworkError(error);
  } else {
    showApiError({ message: error.message }, context);
  }
};

// Función para crear un registro
export const createEntity = async <T>(url: string, entity: any, context?: string): Promise<T> => {
  try {
    const response = await axios.post<T>(url, entity);
    return handleApiResponse(response, ERROR_MESSAGES.CREATE.SUCCESS, context);
  } catch (error) {
    handleApiError(error as AxiosError, context || ERROR_MESSAGES.CREATE.ERROR);
    throw error;
  }
};

// Función para actualizar un registro
export const updateEntity = async <T>(url: string, entity: any, context?: string): Promise<T> => {
  try {
    const response = await axios.put<T>(url, entity);
    return handleApiResponse(response, ERROR_MESSAGES.UPDATE.SUCCESS, context);
  } catch (error) {
    handleApiError(error as AxiosError, context || ERROR_MESSAGES.UPDATE.ERROR);
    throw error;
  }
};

// Función para eliminar un registro
export const deleteEntity = async (url: string, context?: string): Promise<void> => {
  try {
    const response = await axios.delete(url);
    handleApiResponse(response, ERROR_MESSAGES.DELETE.SUCCESS, context);
  } catch (error) {
    handleApiError(error as AxiosError, context || ERROR_MESSAGES.DELETE.ERROR);
    throw error;
  }
};

// Función para obtener registros
export const getEntities = async <T>(url: string, context?: string): Promise<T> => {
  try {
    const response = await axios.get<T>(url);
    return handleApiResponse(response, undefined, context);
  } catch (error) {
    handleApiError(error as AxiosError, context || ERROR_MESSAGES.FETCH.ERROR);
    throw error;
  }
};

// Función para obtener un registro específico
export const getEntity = async <T>(url: string, context?: string): Promise<T> => {
  try {
    const response = await axios.get<T>(url);
    return handleApiResponse(response, undefined, context);
  } catch (error) {
    handleApiError(error as AxiosError, context || ERROR_MESSAGES.FETCH.ERROR);
    throw error;
  }
};

// Función para manejar operaciones con confirmación
export const confirmOperation = (message: string, onConfirm: () => void, onCancel?: () => void): void => {
  if (window.confirm(message)) {
    onConfirm();
  } else if (onCancel) {
    onCancel();
  }
};

// Función para validar campos requeridos
export const validateRequiredFields = (fields: { [key: string]: any }): string[] => {
  const errors: string[] = [];

  Object.entries(fields).forEach(([fieldName, value]) => {
    if (!value || (typeof value === 'string' && value.trim() === '')) {
      const displayName = fieldName.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());
      errors.push(`${displayName} es obligatorio`);
    }
  });

  return errors;
};

// Función para mostrar errores de validación
export const showValidationErrors = (errors: string[]): void => {
  errors.forEach(error => {
    showApiError({ message: error }, 'Error de validación');
  });
};
