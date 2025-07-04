import { toast } from 'react-toastify';

export interface ApiError {
  status?: number;
  message?: string;
  detail?: string;
  fieldErrors?: Array<{
    field: string;
    message: string;
  }>;
}

// Mensajes de error específicos para diferentes operaciones
export const ERROR_MESSAGES = {
  // Operaciones CRUD
  CREATE: {
    SUCCESS: 'Registro creado exitosamente',
    ERROR: 'Error al crear el registro',
    VALIDATION: 'Verifica los datos ingresados',
  },
  UPDATE: {
    SUCCESS: 'Registro actualizado exitosamente',
    ERROR: 'Error al actualizar el registro',
    VALIDATION: 'Verifica los datos ingresados',
  },
  DELETE: {
    SUCCESS: 'Registro eliminado exitosamente',
    ERROR: 'Error al eliminar el registro',
    CONFIRM: '¿Estás seguro de que quieres eliminar este registro?',
  },
  FETCH: {
    ERROR: 'Error al cargar los datos',
    NOT_FOUND: 'No se encontraron registros',
  },

  // Operaciones específicas
  AUTH: {
    LOGIN_ERROR: 'Error al iniciar sesión',
    LOGOUT_ERROR: 'Error al cerrar sesión',
    UNAUTHORIZED: 'No tienes permisos para realizar esta acción',
  },

  // Validaciones comunes
  VALIDATION: {
    REQUIRED_FIELD: 'Este campo es obligatorio',
    INVALID_EMAIL: 'Email inválido',
    INVALID_FORMAT: 'Formato inválido',
    MIN_LENGTH: 'Mínimo de caracteres no alcanzado',
    MAX_LENGTH: 'Máximo de caracteres excedido',
  },
};

// Función para mostrar errores de validación de campos
export const showFieldErrors = (fieldErrors: Array<{ field: string; message: string }>): void => {
  fieldErrors.forEach(fieldError => {
    const fieldName =
      fieldError.field
        .replace(/\[\d*\]/g, '[]')
        .split('.')
        .pop()
        ?.replace(/([A-Z])/g, ' $1')
        .replace(/^./, str => str.toUpperCase()) || 'Campo';

    toast.error(`${fieldName}: ${fieldError.message}`);
  });
};

// Función para mostrar errores de API
export const showApiError = (error: ApiError, context?: string): void => {
  if (error.fieldErrors && error.fieldErrors.length > 0) {
    showFieldErrors(error.fieldErrors);
  } else {
    const message = error.message || error.detail || 'Error inesperado';
    const contextMessage = context ? `${context}: ${message}` : message;
    toast.error(contextMessage);
  }
};

// Función para mostrar mensajes de éxito
export const showSuccessMessage = (message: string): void => {
  toast.success(message);
};

// Función para mostrar mensajes de información
export const showInfoMessage = (message: string): void => {
  toast.info(message);
};

// Función para mostrar mensajes de advertencia
export const showWarningMessage = (message: string): void => {
  toast.warning(message);
};

// Función para manejar errores de red
export const handleNetworkError = (error: any): void => {
  if (error.code === 'NETWORK_ERROR' || error.code === 'ERR_NETWORK') {
    toast.error('Error de conexión. Verifica tu conexión a internet.');
  } else if (error.code === 'ECONNABORTED') {
    toast.error('La petición tardó demasiado. Inténtalo de nuevo.');
  } else {
    toast.error('Error de conexión con el servidor.');
  }
};

// Función para manejar errores de timeout
export const handleTimeoutError = (): void => {
  toast.error('La petición tardó demasiado. Inténtalo de nuevo.');
};

// Función para mostrar errores de validación
export const showValidationErrors = (errors: string[]): void => {
  errors.forEach(error => {
    toast.error(error);
  });
};

// Función para obtener mensaje de error según el código de estado HTTP
export const getHttpErrorMessage = (status: number): string => {
  const errorMessages: { [key: number]: string } = {
    400: 'Solicitud incorrecta',
    401: 'No autorizado',
    403: 'Acceso denegado',
    404: 'Recurso no encontrado',
    409: 'Conflicto de datos',
    422: 'Datos de entrada inválidos',
    500: 'Error interno del servidor',
    502: 'Error de conexión con el servidor',
    503: 'Servicio no disponible',
    0: 'No se puede conectar con el servidor',
  };

  return errorMessages[status] || 'Error inesperado';
};
