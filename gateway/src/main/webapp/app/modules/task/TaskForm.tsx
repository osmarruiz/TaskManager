import React, { useEffect, useState } from 'react';
import { createTask, updateTask } from '../../shared/util/task-api';
import { getPriorities } from '../../shared/util/priority-api';
import { getTaskStatuses } from '../../shared/util/task-status-api';
import { getWorkGroups } from '../../shared/util/work-group-api';
import { getProjects } from '../../shared/util/project-api';
import { Task } from '../../shared/model/task.model';
import { Priority } from '../../shared/model/priority.model';
import { TaskStatusCatalog } from '../../shared/model/task-status.model';
import { WorkGroup } from '../../shared/model/work-group.model';
import { Project } from '../../shared/model/project.model';

interface Props {
  onSuccess: () => void;
  taskToEdit?: Task | null;
}

const TaskForm: React.FC<Props> = ({ onSuccess, taskToEdit }) => {
  const [form, setForm] = useState<Partial<Task>>(taskToEdit || {});
  const [priorities, setPriorities] = useState<Priority[]>([]);
  const [statuses, setStatuses] = useState<TaskStatusCatalog[]>([]);
  const [workGroups, setWorkGroups] = useState<WorkGroup[]>([]);
  const [projects, setProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([getPriorities(), getTaskStatuses(), getWorkGroups(), getProjects()]).then(
      ([prioritiesData, statusesData, workGroupsData, projectsData]) => {
        setPriorities(prioritiesData);
        setStatuses(statusesData);
        setWorkGroups(workGroupsData);
        setProjects(projectsData);
        setLoading(false);
      },
    );
  }, []);

  useEffect(() => {
    if (taskToEdit) setForm(taskToEdit);
  }, [taskToEdit]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSelectObject = (name: string, list: any[], id: string) => {
    const obj = list.find(item => String(item.id) === id);
    setForm(prev => ({ ...prev, [name]: obj }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      if (taskToEdit && taskToEdit.id) {
        await updateTask(taskToEdit.id, form as Task);
      } else {
        await createTask(form as Task);
      }
      onSuccess();
    } catch (err) {
      setError('Error al guardar la tarea');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div>Cargando formulario...</div>;

  return (
    <form onSubmit={handleSubmit}>
      <h3>{taskToEdit ? 'Editar Tarea' : 'Crear Tarea'}</h3>
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
        <label>Fecha límite</label>
        <input name="deadline" type="datetime-local" className="form-control" value={form.deadline || ''} onChange={handleChange} />
      </div>
      <div className="mb-3">
        <label>Prioridad</label>
        <select
          name="priority"
          className="form-control"
          required
          value={form.priority?.id || ''}
          onChange={e => handleSelectObject('priority', priorities, e.target.value)}
        >
          <option value="">Seleccione</option>
          {priorities.map(p => (
            <option key={p.id} value={p.id}>
              {p.name}
            </option>
          ))}
        </select>
      </div>
      <div className="mb-3">
        <label>Estado</label>
        <select
          name="status"
          className="form-control"
          required
          value={form.status?.id || ''}
          onChange={e => handleSelectObject('status', statuses, e.target.value)}
        >
          <option value="">Seleccione</option>
          {statuses.map(s => (
            <option key={s.id} value={s.id}>
              {s.name}
            </option>
          ))}
        </select>
      </div>
      <div className="mb-3">
        <label>Grupo de trabajo</label>
        <select
          name="workGroup"
          className="form-control"
          required
          value={form.workGroup?.id || ''}
          onChange={e => handleSelectObject('workGroup', workGroups, e.target.value)}
        >
          <option value="">Seleccione</option>
          {workGroups.map(wg => (
            <option key={wg.id} value={wg.id}>
              {wg.name}
            </option>
          ))}
        </select>
      </div>
      <div className="mb-3">
        <label>Proyecto</label>
        <select
          name="parentProject"
          className="form-control"
          value={form.parentProject?.id || ''}
          onChange={e => handleSelectObject('parentProject', projects, e.target.value)}
        >
          <option value="">Sin proyecto</option>
          {projects.map(p => (
            <option key={p.id} value={p.id}>
              {p.title}
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

export default TaskForm;
