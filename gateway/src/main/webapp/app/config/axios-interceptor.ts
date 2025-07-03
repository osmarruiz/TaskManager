import axios, { type AxiosError } from 'axios';
import { toast } from 'react-toastify';

const TIMEOUT = 1 * 60 * 1000;
axios.defaults.timeout = TIMEOUT;
axios.defaults.baseURL = SERVER_API_URL;

// Interfaz para los datos de error de la API
interface ApiErrorData {
  message?: string;
  detail?: string;
  error?: string;
  title?: string;
  fieldErrors?: Array<{
    field: string;
    message: string;
  }>;
}

// Función para obtener mensaje de error específico según el código de estado
const getErrorMessage = (status: number, data: any): string => {
  switch (status) {
    case 400:
      return data?.message || data?.detail || 'Solicitud incorrecta. Verifica los datos ingresados.';
    case 401:
      return 'No tienes autorización para realizar esta acción.';
    case 403:
      return 'Acceso denegado. No tienes permisos suficientes.';
    case 404:
      return 'Recurso no encontrado.';
    case 409:
      return 'Conflicto de datos. El recurso ya existe o hay un conflicto.';
    case 422:
      return 'Datos de entrada inválidos. Verifica la información proporcionada.';
    case 500:
      return 'Error interno del servidor. Inténtalo de nuevo más tarde.';
    case 502:
      return 'Error de conexión con el servidor. Inténtalo de nuevo.';
    case 503:
      return 'Servicio temporalmente no disponible. Inténtalo más tarde.';
    case 0:
      return 'No se puede conectar con el servidor. Verifica tu conexión.';
    default:
      return data?.message || data?.detail || data?.error || 'Error inesperado. Inténtalo de nuevo.';
  }
};

// Función para manejar errores de validación de campos
const handleFieldErrors = (fieldErrors: Array<{ field: string; message: string }>): void => {
  if (fieldErrors && Array.isArray(fieldErrors)) {
    fieldErrors.forEach(fieldError => {
      const fieldName = fieldError.field?.replace(/\[\d*\]/g, '[]') || 'Campo';
      const message = fieldError.message || 'Error de validación';
      toast.error(`${fieldName}: ${message}`);
    });
  }
};

const setupAxiosInterceptors = onUnauthenticated => {
  const onRequestSuccess = config => {
    return config;
  };

  const onResponseSuccess = response => response;

  const onResponseError = (err: AxiosError) => {
    const status = err.status || (err.response ? err.response.status : 0);
    const data = err.response?.data as ApiErrorData;

    // Manejar errores de autenticación
    if (status === 401) {
      onUnauthenticated();
      return Promise.reject(err);
    }

    // Ignorar errores de verificación de cuenta y autenticación
    if (err.config?.url?.endsWith('api/account') || err.config?.url?.endsWith('api/authenticate')) {
      return Promise.reject(err);
    }

    // Manejar errores de respuesta del servidor
    if (err.response) {
      const { response } = err;

      // Manejar errores de validación de campos
      if (data?.fieldErrors && Array.isArray(data.fieldErrors)) {
        handleFieldErrors(data.fieldErrors);
      } else {
        // Mostrar mensaje de error general
        const errorMessage = getErrorMessage(status, data);
        toast.error(errorMessage);
      }
    } else if (err.request) {
      // Error de red (sin respuesta del servidor)
      toast.error('Error de conexión. Verifica tu conexión a internet.');
    } else {
      // Error en la configuración de la petición
      toast.error('Error en la configuración de la petición.');
    }

    return Promise.reject(err);
  };

  axios.interceptors.request.use(onRequestSuccess);
  axios.interceptors.response.use(onResponseSuccess, onResponseError);
};

export default setupAxiosInterceptors;
