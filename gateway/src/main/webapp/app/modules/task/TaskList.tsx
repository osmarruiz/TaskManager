import React, { useEffect, useState, useMemo } from 'react';
import { getTasks, getMyTasks } from '../../shared/util/task-api';
import { Task } from '../../shared/model/task.model';
import TaskDetail from './TaskDetail';
import { useUserRole } from '../../shared/hooks/useUserRole';
import { useLoading } from '../../shared/hooks/useLoading';
import { useApiError } from '../../shared/hooks/useApiError';

const TaskList: React.FC = () => {
  const { isAdmin } = useUserRole();
  const { loading, error, startLoading, stopLoading, setLoadingError } = useLoading(true);
  const { handleError } = useApiError();
  const [tasks, setTasks] = useState<Task[]>([]);
  const [selectedTaskId, setSelectedTaskId] = useState<number | null>(null);

  const loadTasks = () => {
    startLoading();
    // Si es ADMIN, cargar todas las tareas; si no, cargar solo las asignadas al usuario
    const taskLoader = isAdmin ? getTasks() : getMyTasks();
    taskLoader
      .then(data => {
        setTasks(data);
        stopLoading();
      })
      .catch(err => {
        handleError(err, 'Error al cargar tareas');
        setTasks([]);
        setLoadingError('No se pudieron cargar las tareas');
      });
  };

  useEffect(() => {
    loadTasks();
  }, [isAdmin]);

  // Optimizar renderizado con useMemo
  const filteredTasks = useMemo(() => tasks.filter(task => !task.archived), [tasks]);

  if (loading)
    return (
      <div className="text-center py-4">
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
        <p className="mt-2">Cargando tareas...</p>
      </div>
    );

  if (error)
    return (
      <div className="alert alert-danger">
        <i className="fas fa-exclamation-triangle me-2"></i>
        {error}
      </div>
    );

  if (selectedTaskId) {
    return <TaskDetail id={selectedTaskId} onBack={() => setSelectedTaskId(null)} />;
  }

  return (
    <div>
      <h2>Lista de Tareas</h2>
      {!isAdmin && (
        <div className="alert alert-info mb-3" role="alert">
          <i className="fas fa-info-circle me-2" aria-hidden="true"></i>
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
            <th>Proyecto</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {filteredTasks.map(task => (
            <tr key={task.id}>
              <td>{task.id}</td>
              <td>{task.title}</td>
              <td>{task.description}</td>
              <td>{task.status?.name}</td>
              <td>{task.priority?.name}</td>
              <td>{task.parentProject?.title || <span className="text-muted">Sin proyecto asignado</span>}</td>
              <td>
                <button
                  className="btn btn-info btn-sm me-2"
                  onClick={() => setSelectedTaskId(task.id)}
                  aria-label={`Ver detalles de tarea ${task.title}`}
                  title="Ver detalles"
                >
                  <i className="fas fa-eye" aria-hidden="true"></i>
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
