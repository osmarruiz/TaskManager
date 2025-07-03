import { toast } from 'react-toastify';
import { isFulfilledAction, isRejectedAction } from 'app/shared/reducers/reducer.utils';
import { isAxiosError } from 'axios';
import { FieldErrorVM, isProblemWithMessage } from 'app/shared/jhipster/problem-details';
import { getMessageFromHeaders } from 'app/shared/jhipster/headers';
import { showApiError, showSuccessMessage, handleNetworkError, getHttpErrorMessage } from 'app/shared/util/error-utils';

type ToastMessage = {
  message?: string;
};

const addErrorAlert = (message: ToastMessage) => {
  toast.error(message.message);
};

const getFieldErrorsToasts = (fieldErrors: FieldErrorVM[]): ToastMessage[] =>
  fieldErrors.map(fieldError => {
    if (['Min', 'Max', 'DecimalMin', 'DecimalMax'].includes(fieldError.message)) {
      fieldError.message = 'Size';
    }
    // convert 'something[14].other[4].id' to 'something[].other[].id' so translations can be written to it
    const convertedField = fieldError.field.replace(/\[\d*\]/g, '[]');
    const fieldName = convertedField.charAt(0).toUpperCase() + convertedField.slice(1);
    return { message: `Error en el campo "${fieldName}"` };
  });

// eslint-disable-next-line complexity
export default () => next => action => {
  const { error, payload } = action;

  /**
   *
   * The notification middleware serves to add success and error notifications
   */
  if (isFulfilledAction(action) && payload?.headers) {
    const { alert } = getMessageFromHeaders(payload.headers);
    if (alert) {
      showSuccessMessage(alert);
    }
  }

  if (isRejectedAction(action) && isAxiosError(error)) {
    if (error.response) {
      const { response } = error;
      if (response.status === 401) {
        // Ignore, page will be redirected to login.
      } else if (error.config?.url?.endsWith('api/account') || error.config?.url?.endsWith('api/authenticate')) {
        // Ignore, authentication status check and authentication are treated differently.
      } else if (response.status === 0) {
        // connection refused, server not reachable
        handleNetworkError(error);
      } else if (response.status === 404) {
        showApiError({ status: 404, message: 'Recurso no encontrado' });
      } else {
        const { data } = response;
        const problem = isProblemWithMessage(data) ? data : null;
        if (problem?.fieldErrors) {
          getFieldErrorsToasts(problem.fieldErrors).forEach(message => addErrorAlert(message));
        } else {
          const { error: toastError } = getMessageFromHeaders((response.headers as any) ?? {});
          if (toastError) {
            addErrorAlert({ message: toastError });
          } else if (typeof data === 'string' && data !== '') {
            addErrorAlert({ message: data });
          } else {
            const errorMessage = getHttpErrorMessage(response.status);
            showApiError({
              status: response.status,
              message: data?.detail ?? data?.message ?? data?.error ?? data?.title ?? errorMessage,
            });
          }
        }
      }
    } else if (error.config?.url?.endsWith('api/account') && error.config?.method === 'get') {
      /* eslint-disable no-console */
      console.log('Authentication Error: Trying to access url api/account with GET.');
    } else {
      handleNetworkError(error);
    }
  } else if (error) {
    addErrorAlert({ message: error.message ?? 'Error inesperado!' });
  }

  return next(action);
};
