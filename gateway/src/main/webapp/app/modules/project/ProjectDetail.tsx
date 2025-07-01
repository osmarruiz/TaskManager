import React, { useEffect, useState } from 'react';
import { Project } from '../../shared/model/project.model';
import { getProjects } from '../../shared/util/project-api';
import { getTasks } from '../../shared/util/task-api';
import { Task } from '../../shared/model/task.model';
import { getProjectTasks, addTaskToProject, removeTaskFromProject, assignUserToProject } from '../../shared/util/project-api';
import { getUsers } from '../../shared/util/user-api';
import { IUser } from '../../shared/model/user.model';

interface Props {
  id: number;
  onBack: () => void;
}

const ProjectDetail: React.FC<Props> = ({ id, onBack }) => {
  const [project, setProject] = useState<Project | null>(null);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [projectTasks, setProjectTasks] = useState<Task[]>([]);
  const [allUsers, setAllUsers] = useState<IUser[]>([]);
  const [selectedUser, setSelectedUser] = useState('');
  const [newTaskTitle, setNewTaskTitle] = useState('');
  const [newTaskDescription, setNewTaskDescription] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    getProjects().then(data => {
      const found = data.find(p => p.id === id);
      setProject(found || null);
      setLoading(false);
    });
    getProjectTasks(id).then(setProjectTasks);
    getUsers().then(setAllUsers);
  }, [id]);

  const handleAddTask = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    await addTaskToProject(id, { title: newTaskTitle, description: newTaskDescription, workGroup: project?.workGroup } as Task);
    setNewTaskTitle('');
    setNewTaskDescription('');
    setSaving(false);
    getProjectTasks(id).then(setProjectTasks);
  };

  const handleRemoveTask = async (taskId: number) => {
    if (window.confirm('¿Seguro que deseas quitar esta tarea del proyecto?')) {
      await removeTaskFromProject(id, taskId);
      getProjectTasks(id).then(setProjectTasks);
    }
  };

  const handleAssignUser = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedUser) return;
    setSaving(true);
    await assignUserToProject(id, selectedUser);
    setSelectedUser('');
    setSaving(false);
    // Aquí podrías recargar miembros si implementas esa vista
  };

  if (loading) return <div>Cargando detalle...</div>;
  if (!project) return <div>No se encontró el proyecto.</div>;

  return (
    <div>
      <h3>Detalle de Proyecto</h3>
      <button className="btn btn-secondary mb-3" onClick={onBack}>
        Volver
      </button>
      <ul className="list-group mb-3">
        <li className="list-group-item">
          <b>ID:</b> {project.id}
        </li>
        <li className="list-group-item">
          <b>Título:</b> {project.title}
        </li>
        <li className="list-group-item">
          <b>Descripción:</b> {project.description}
        </li>
        <li className="list-group-item">
          <b>Fecha inicio:</b> {project.startDate}
        </li>
        <li className="list-group-item">
          <b>Fecha fin:</b> {project.endDate}
        </li>
        <li className="list-group-item">
          <b>Grupo de trabajo:</b> {project.workGroup?.name}
        </li>
      </ul>
      <h5>Tareas asociadas</h5>
      <form onSubmit={handleAddTask} className="mb-3 d-flex">
        <input
          className="form-control me-2"
          placeholder="Título de la tarea"
          value={newTaskTitle}
          onChange={e => setNewTaskTitle(e.target.value)}
          required
        />
        <input
          className="form-control me-2"
          placeholder="Descripción"
          value={newTaskDescription}
          onChange={e => setNewTaskDescription(e.target.value)}
          required
        />
        <button className="btn btn-success" type="submit" disabled={saving}>
          Agregar tarea
        </button>
      </form>
      <ul className="list-group mb-3">
        {projectTasks.map(t => (
          <li key={t.id} className="list-group-item d-flex justify-content-between align-items-center">
            <span>
              {t.title} - {t.status?.name}
            </span>
            <button className="btn btn-danger btn-sm" onClick={() => handleRemoveTask(t.id)}>
              Quitar
            </button>
          </li>
        ))}
        {projectTasks.length === 0 && <li className="list-group-item">Sin tareas asociadas</li>}
      </ul>
      <h5>Asignar usuario al proyecto</h5>
      <form onSubmit={handleAssignUser} className="mb-3 d-flex">
        <select className="form-control me-2" value={selectedUser} onChange={e => setSelectedUser(e.target.value)} required>
          <option value="">Selecciona usuario</option>
          {allUsers.map(u => (
            <option key={u.login} value={u.login}>
              {u.login} ({u.email})
            </option>
          ))}
        </select>
        <button className="btn btn-primary" type="submit" disabled={saving || !selectedUser}>
          Asignar
        </button>
      </form>
    </div>
  );
};

export default ProjectDetail;
