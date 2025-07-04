import React, { useEffect, useState } from 'react';
import { getTasks, getMyTasks } from '../../shared/util/task-api';
import { Task } from '../../shared/model/task.model';
import TaskDetail from './TaskDetail';
import { useAppSelector } from '../../config/store';
import { hasAnyAuthority } from '../../shared/auth/private-route';
import { AUTHORITIES } from '../../config/constants';

const TaskList: React.FC = () => {
  const account = useAppSelector(state => state.authentication.account);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [selectedTaskId, setSelectedTaskId] = useState<number | null>(null);

  // Verificar si el usuario es ADMIN
  const isAdmin = hasAnyAuthority(account.authorities, [AUTHORITIES.ADMIN]);

  const loadTasks = () => {
    setLoading(true);
    // Si es ADMIN, cargar todas las tareas; si no, cargar solo las asignadas al usuario
    const taskLoader = isAdmin ? getTasks() : getMyTasks();
    taskLoader
      .then(data => {
        setTasks(data);
        setLoading(false);
      })
      .catch(error => {
        console.error('Error loading tasks:', error);
        setTasks([]);
        setLoading(false);
      });
  };

  useEffect(() => {
    loadTasks();
  }, [isAdmin]);

  if (loading) return <div>Cargando tareas...</div>;

  if (selectedTaskId) {
    return <TaskDetail id={selectedTaskId} onBack={() => setSelectedTaskId(null)} />;
  }

  return (
    <div>
      <h2>Lista de Tareas</h2>
      {!isAdmin && (
        <div className="alert alert-info mb-3">
          <i className="fas fa-info-circle me-2"></i>
          Solo se muestran las tareas asignadas a tu usuario.
        </div>
      )}
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Título</th>
            <th>Descripción</th>
            <th>Estado</th>
            <th>Prioridad</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {tasks.map(task => (
            <tr key={task.id}>
              <td>{task.id}</td>
              <td>{task.title}</td>
              <td>{task.description}</td>
              <td>{task.status?.name}</td>
              <td>{task.priority?.name}</td>
              <td>
                <button className="btn btn-info btn-sm me-2" onClick={() => setSelectedTaskId(task.id)}>
                  Ver
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TaskList;
