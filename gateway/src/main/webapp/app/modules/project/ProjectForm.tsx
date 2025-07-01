import React, { useEffect, useState } from 'react';
import { getWorkGroups } from '../../shared/util/work-group-api';
import { createProject, updateProject } from '../../shared/util/project-api';
import { Project } from '../../shared/model/project.model';
import { WorkGroup } from '../../shared/model/work-group.model';

interface Props {
  onSuccess: () => void;
  projectToEdit?: Project | null;
}

const ProjectForm: React.FC<Props> = ({ onSuccess, projectToEdit }) => {
  const [form, setForm] = useState<Partial<Project>>(projectToEdit || {});
  const [workGroups, setWorkGroups] = useState<WorkGroup[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getWorkGroups().then(data => {
      setWorkGroups(data);
      setLoading(false);
    });
  }, []);

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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      if (projectToEdit && projectToEdit.id) {
        await updateProject(projectToEdit.id, form as Project);
      } else {
        await createProject(form as Project);
      }
      onSuccess();
    } catch (err) {
      setError('Error al guardar el proyecto');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div>Cargando formulario...</div>;

  return (
    <form onSubmit={handleSubmit}>
      <h3>{projectToEdit ? 'Editar Proyecto' : 'Crear Proyecto'}</h3>
      {error && <div className="alert alert-danger">{error}</div>}
      <div className="mb-3">
        <label>Título</label>
        <input name="title" className="form-control" required value={form.title || ''} onChange={handleChange} />
      </div>
      <div className="mb-3">
        <label>Descripción</label>
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
        <label>Grupo de trabajo</label>
        <select
          name="workGroup"
          className="form-control"
          required
          value={form.workGroup?.id || ''}
          onChange={e => handleSelectWorkGroup(e.target.value)}
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
    </form>
  );
};

export default ProjectForm;
