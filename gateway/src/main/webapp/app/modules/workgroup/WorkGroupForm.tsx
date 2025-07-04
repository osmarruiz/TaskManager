import React, { useState, useEffect } from 'react';
import { createWorkGroup, updateWorkGroup } from '../../shared/util/work-group-api';
import { WorkGroup } from '../../shared/model/work-group.model';

interface Props {
  onSuccess: () => void;
  workGroupToEdit?: WorkGroup | null;
}

const WorkGroupForm: React.FC<Props> = ({ onSuccess, workGroupToEdit }) => {
  const [form, setForm] = useState<Partial<WorkGroup>>(workGroupToEdit || {});
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (workGroupToEdit) setForm(workGroupToEdit);
  }, [workGroupToEdit]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      if (workGroupToEdit && workGroupToEdit.id) {
        await updateWorkGroup(workGroupToEdit.id, form as WorkGroup);
      } else {
        await createWorkGroup(form as WorkGroup);
      }
      onSuccess();
    } catch (err) {
      setError('Error al guardar el grupo');
    } finally {
      setSaving(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h3>{workGroupToEdit ? 'Editar Grupo' : 'Crear Grupo'}</h3>
      {error && <div className="alert alert-danger">{error}</div>}
      <div className="mb-3">
        <label>Nombre</label>
        <input name="name" className="form-control" required value={form.name || ''} onChange={handleChange} />
      </div>
      <div className="mb-3">
        <label>Descripción</label>
        <textarea name="description" className="form-control" value={form.description || ''} onChange={handleChange} />
      </div>
      <button className="btn btn-success" type="submit" disabled={saving}>
        {saving ? 'Guardando...' : 'Guardar'}
      </button>
    </form>
  );
};

export default WorkGroupForm;
