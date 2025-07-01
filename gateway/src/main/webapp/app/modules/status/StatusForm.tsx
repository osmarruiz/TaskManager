import React, { useState, useEffect } from 'react';
import { createStatus, updateStatus } from '../../shared/util/status-api';
import { TaskStatusCatalog, CreateTaskStatusCatalog } from '../../shared/model/status.model';

interface Props {
  onSuccess: () => void;
  statusToEdit?: TaskStatusCatalog | null;
}

const StatusForm: React.FC<Props> = ({ onSuccess, statusToEdit }) => {
  const [form, setForm] = useState<CreateTaskStatusCatalog>({ name: '', description: '' });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (statusToEdit) setForm({ name: statusToEdit.name, description: statusToEdit.description });
  }, [statusToEdit]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      if (statusToEdit && statusToEdit.id) {
        await updateStatus(statusToEdit.id, form);
      } else {
        await createStatus(form);
      }
      onSuccess();
    } catch (err) {
      setError('Error al guardar el estado');
    } finally {
      setSaving(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h3>{statusToEdit ? 'Editar Estado' : 'Crear Estado'}</h3>
      {error && <div className="alert alert-danger">{error}</div>}
      <div className="mb-3">
        <label>Nombre</label>
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
