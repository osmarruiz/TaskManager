import React, { useEffect, useState } from 'react';
import { getWorkGroups } from '../../shared/util/work-group-api';
import { createProject, updateProject } from '../../shared/util/project-api';
import { Project } from '../../shared/model/project.model';
import { WorkGroup } from '../../shared/model/work-group.model';
import { showApiError, showSuccessMessage, ERROR_MESSAGES, showValidationErrors } from '../../shared/util/error-utils';
import { validateRequiredFields } from '../../shared/util/api-utils';

interface CreateProjectDTO {
  title: string;
  description: string;
  startDate?: string;
  endDate?: string;
  workGroupId: number;
}

interface Props {
  onSuccess: () => void;
  projectToEdit?: Project | null;
  workGroupId?: number;
  onCancel?: () => void;
}

const ProjectForm: React.FC<Props> = ({ onSuccess, projectToEdit, workGroupId, onCancel }) => {
  const [form, setForm] = useState<Partial<Project>>(projectToEdit || {});
  const [workGroups, setWorkGroups] = useState<WorkGroup[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    getWorkGroups()
      .then(data => {
        setWorkGroups(data);
        setLoading(false);
        if (workGroupId) {
          const grupo = data.find(g => g.id === workGroupId);
          if (grupo) setForm(prev => ({ ...prev, workGroup: grupo }));
        }
      })
      .catch(error => {
        showApiError(error, 'Error al cargar grupos de trabajo');
        setLoading(false);
      });
  }, [workGroupId]);

  useEffect(() => {
    if (projectToEdit) setForm(projectToEdit);
  }, [projectToEdit]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSelectWorkGroup = (id: string) => {
    const grupo = workGroups.find(g => String(g.id) === id);
    setForm(prev => ({ ...prev, workGroup: grupo }));
  };

  const toIsoStringWithTZ = (value: string | undefined) => {
    if (!value) return undefined;
    // Si ya tiene zona horaria, no modificar
    if (value.includes('Z') || value.includes('+')) return value;
    // Si viene de input datetime-local: 'YYYY-MM-DDTHH:mm'
    // Convertir a 'YYYY-MM-DDTHH:mm:00Z'
    return value.length === 16 ? value + ':00Z' : value + 'Z';
  };

  const validateForm = (): boolean => {
    const requiredFields = {
      title: form.title,
      description: form.description,
      workGroup: form.workGroup,
    };

    const errors = validateRequiredFields(requiredFields);

    if (errors.length > 0) {
      showValidationErrors(errors);
      return false;
    }

    if (!form.workGroup) {
      showApiError({ message: 'Debe seleccionar un grupo de trabajo' }, 'Error de validación');
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
      const payload: CreateProjectDTO = {
        title: form.title || '',
        description: form.description || '',
        startDate: toIsoStringWithTZ(form.startDate),
        endDate: toIsoStringWithTZ(form.endDate),
        workGroupId: form.workGroup.id,
      };

      if (projectToEdit && projectToEdit.id) {
        await updateProject(projectToEdit.id, payload as any);
        showSuccessMessage('Proyecto actualizado exitosamente');
      } else {
        await createProject(payload as any);
        showSuccessMessage('Proyecto creado exitosamente');
      }

      onSuccess();
    } catch (error) {
      // El error ya se maneja en el interceptor de axios
      console.error('Error en formulario:', error);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div>Cargando formulario...</div>;

  return (
    <form onSubmit={handleSubmit}>
      <h3>{projectToEdit ? 'Editar Proyecto' : 'Crear Proyecto'}</h3>
      <div className="mb-3">
        <label>Título *</label>
        <input name="title" className="form-control" required value={form.title || ''} onChange={handleChange} />
      </div>
      <div className="mb-3">
        <label>Descripción *</label>
        <textarea name="description" className="form-control" required value={form.description || ''} onChange={handleChange} />
      </div>
      <div className="mb-3">
        <label>Fecha inicio</label>
        <input name="startDate" type="datetime-local" className="form-control" value={form.startDate || ''} onChange={handleChange} />
      </div>
      <div className="mb-3">
        <label>Fecha fin</label>
        <input name="endDate" type="datetime-local" className="form-control" value={form.endDate || ''} onChange={handleChange} />
      </div>
      <div className="mb-3">
        <label>Grupo de trabajo *</label>
        <select
          name="workGroup"
          className="form-control"
          required
          value={form.workGroup?.id || ''}
          onChange={e => handleSelectWorkGroup(e.target.value)}
          disabled={!!workGroupId}
        >
          <option value="">Seleccione</option>
          {workGroups.map(grupo => (
            <option key={grupo.id} value={grupo.id}>
              {grupo.name}
            </option>
          ))}
        </select>
      </div>
      <button className="btn btn-success" type="submit" disabled={saving}>
        {saving ? 'Guardando...' : 'Guardar'}
      </button>
      {onCancel && (
        <button type="button" className="btn btn-secondary ms-2" onClick={onCancel}>
          Cancelar
        </button>
      )}
    </form>
  );
};

export default ProjectForm;
