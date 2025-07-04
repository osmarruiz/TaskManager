import React from 'react';
import { showValidationErrors } from '../util/api-utils';

export interface ValidationRule {
  field: string;
  required?: boolean;
  minLength?: number;
  maxLength?: number;
  pattern?: RegExp;
  custom?: (value: any) => string | null;
}

export interface FormData {
  [key: string]: any;
}

export const validateForm = (data: FormData, rules: ValidationRule[]): string[] => {
  const errors: string[] = [];

  rules.forEach(rule => {
    const value = data[rule.field];
    const fieldName = rule.field.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());

    // Validación de campo requerido
    if (rule.required && (!value || (typeof value === 'string' && value.trim() === ''))) {
      errors.push(`${fieldName} es obligatorio`);
      return;
    }

    // Si el campo no es requerido y está vacío, no validar más
    if (!value || (typeof value === 'string' && value.trim() === '')) {
      return;
    }

    // Validación de longitud mínima
    if (rule.minLength && typeof value === 'string' && value.length < rule.minLength) {
      errors.push(`${fieldName} debe tener al menos ${rule.minLength} caracteres`);
    }

    // Validación de longitud máxima
    if (rule.maxLength && typeof value === 'string' && value.length > rule.maxLength) {
      errors.push(`${fieldName} debe tener máximo ${rule.maxLength} caracteres`);
    }

    // Validación de patrón
    if (rule.pattern && typeof value === 'string' && !rule.pattern.test(value)) {
      errors.push(`${fieldName} tiene un formato inválido`);
    }

    // Validación personalizada
    if (rule.custom) {
      const customError = rule.custom(value);
      if (customError) {
        errors.push(customError);
      }
    }
  });

  return errors;
};

export const useFormValidation = (rules: ValidationRule[]) => {
  const validate = React.useCallback(
    (data: FormData): boolean => {
      const errors = validateForm(data, rules);
      if (errors.length > 0) {
        showValidationErrors(errors);
        return false;
      }
      return true;
    },
    [rules],
  );

  return { validate };
};
