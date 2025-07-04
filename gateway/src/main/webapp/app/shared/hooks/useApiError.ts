import { useCallback } from 'react';
import { showApiError } from '../util/error-utils';

export const useApiError = () => {
  const handleError = useCallback((error: any, context?: string) => {
    if (error.response) {
      const { status, data } = error.response;
      showApiError({ status, message: data?.message || data?.detail }, context);
    } else if (error.request) {
      showApiError({ message: 'Error de conexión. Verifica tu conexión a internet.' }, context);
    } else {
      showApiError({ message: error.message || 'Error inesperado' }, context);
    }
  }, []);

  return { handleError };
};
