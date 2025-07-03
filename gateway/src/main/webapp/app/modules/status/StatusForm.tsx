import React, { useState, useEffect } from 'react';
import { createStatus, updateStatus } from '../../shared/util/status-api';
import { TaskStatusCatalog, CreateTaskStatusCatalog } from '../../shared/model/status.model';
import { showApiError, showSuccessMessage, ERROR_MESSAGES } from '../../shared/util/error-utils';
import { validateRequiredFields } from '../../shared/util/api-utils';

interface Props {
  onSuccess: () => void;
  statusToEdit?: TaskStatusCatalog | null;
}

const StatusForm: React.FC<Props> = ({ onSuccess, statusToEdit }) => {
  const [form, setForm] = useState<CreateTaskStatusCatalog>({ name: '', description: '' });
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (statusToEdit) setForm({ name: statusToEdit.name, description: statusToEdit.description });
  }, [statusToEdit]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const validateForm = (): boolean => {
    const requiredFields = {
      name: form.name,
    };

    const errors = validateRequiredFields(requiredFields);

    if (errors.length > 0) {
      errors.forEach(error => {
        showApiError({ message: error }, 'Error de validación');
      });
      return false;
    }

    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setSaving(true);

    try {
      if (statusToEdit && statusToEdit.id) {
        await updateStatus(statusToEdit.id, form);
        showSuccessMessage('Estado actualizado exitosamente');
      } else {
        await createStatus(form);
        showSuccessMessage('Estado creado exitosamente');
      }
      onSuccess();
    } catch (error: any) {
      // Manejar específicamente errores de acceso denegado
      if (error.response?.status === 403) {
        showApiError(
          {
            message:
              'No tienes permisos para realizar esta acción. Solo los administradores, propietarios y moderadores de grupos de trabajo pueden gestionar estados de tarea.',
          },
          'Acceso Denegado',
        );
      } else {
        // El error ya se maneja en el interceptor de axios para otros casos
        console.error('Error en formulario:', error);
      }
    } finally {
      setSaving(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h3>{statusToEdit ? 'Editar Estado' : 'Crear Estado'}</h3>
      <div className="mb-3">
        <label>Nombre *</label>
        <input name="name" className="form-control" required value={form.name} onChange={handleChange} />
      </div>
      <div className="mb-3">
        <label>Descripción</label>
        <textarea name="description" className="form-control" value={form.description} onChange={handleChange} />
      </div>
      <button className="btn btn-success" type="submit" disabled={saving}>
        {saving ? 'Guardando...' : 'Guardar'}
      </button>
    </form>
  );
};

export default StatusForm;
