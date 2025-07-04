import React, { useState, useEffect } from 'react';
import { createPriority, updatePriority } from '../../shared/util/priority-api';
import { Priority } from '../../shared/model/priority.model';

interface Props {
  onSuccess: () => void;
  priorityToEdit?: Priority | null;
}

const PriorityForm: React.FC<Props> = ({ onSuccess, priorityToEdit }) => {
  const [form, setForm] = useState<Partial<Priority>>(priorityToEdit || {});
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (priorityToEdit) setForm(priorityToEdit);
  }, [priorityToEdit]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target;
    let newValue: any = value;
    if (type === 'checkbox' && e.target instanceof HTMLInputElement) {
      newValue = e.target.checked;
    }
    setForm(prev => ({ ...prev, [name]: newValue }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      if (priorityToEdit && priorityToEdit.id) {
        await updatePriority(priorityToEdit.id, form as Priority);
      } else {
        await createPriority(form as Priority);
      }
      onSuccess();
    } catch (err) {
      setError('Error al guardar la prioridad');
    } finally {
      setSaving(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h3>{priorityToEdit ? 'Editar Prioridad' : 'Crear Prioridad'}</h3>
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

export default PriorityForm;
