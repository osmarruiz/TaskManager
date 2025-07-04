import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { getTask } from '../../shared/util/task-api';
import { Task } from '../../shared/model/task.model';
import TaskComments from './TaskComments';
import TaskAssignments from './TaskAssignments';
import { getPriorities } from '../../shared/util/priority-api';
import { getTaskStatuses } from '../../shared/util/task-status-api';
import { changeTaskPriority, changeTaskStatus } from '../../shared/util/task-api';
import { useUserRole } from '../../shared/hooks/useUserRole';
import { useLoading } from '../../shared/hooks/useLoading';
import { useApiError } from '../../shared/hooks/useApiError';
import { getMembers, getWorkGroup } from '../../shared/util/work-group-api';
import { MemberWithRole } from '../../shared/model/member-with-role.model';
import { getProjects } from '../../shared/util/project-api';

interface Props {
  id: number;
  onBack: () => void;
}

const TaskDetail: React.FC<Props> = ({ id, onBack }) => {
  const { account, isAdmin } = useUserRole();
  const { loading, error, startLoading, stopLoading, setLoadingError } = useLoading(true);
  const { handleError } = useApiError();
  const [task, setTask] = useState<Task | null>(null);
  const [priorities, setPriorities] = useState([]);
  const [statuses, setStatuses] = useState([]);
  const [userRole, setUserRole] = useState<string | null>(null);
  const [workGroupName, setWorkGroupName] = useState<string | null>(null);
  const [projectTitle, setProjectTitle] = useState<string | null>(null);

  const loadTaskData = useCallback(async () => {
    startLoading();
    try {
      const [taskData, prioritiesData, statusesData] = await Promise.all([getTask(id), getPriorities(), getTaskStatuses()]);

      setTask(taskData);
      setPriorities(prioritiesData);
      setStatuses(statusesData);

      if (taskData.workGroup?.id) {
        try {
          const members = await getMembers(taskData.workGroup.id);
          const userMember = members.find(m => m.userLogin === account.login);
          setUserRole(userMember?.role || null);
        } catch (err) {
          handleError(err, 'Error al cargar miembros del grupo');
        }

        if (!taskData.workGroup.name) {
          try {
            const workGroup = await getWorkGroup(taskData.workGroup.id);
            setWorkGroupName(workGroup.name);
          } catch (err) {
            handleError(err, 'Error al cargar información del grupo');
          }
        } else {
          setWorkGroupName(taskData.workGroup.name);
        }
      }

      if (taskData.parentProject?.id && !taskData.parentProject.title) {
        try {
          const projects = await getProjects();
          const found = projects.find(p => p.id === taskData.parentProject.id);
          setProjectTitle(found?.title || null);
        } catch (err) {
          handleError(err, 'Error al cargar información del proyecto');
        }
      } else if (taskData.parentProject?.title) {
        setProjectTitle(taskData.parentProject.title);
      }

      stopLoading();
    } catch (err) {
      handleError(err, 'Error al cargar detalles de la tarea');
      setLoadingError('No se pudo cargar la información de la tarea');
    }
  }, [id, account.login, startLoading, stopLoading, setLoadingError, handleError]);

  useEffect(() => {
    loadTaskData();
  }, [loadTaskData]);

  const canManage = isAdmin || userRole === 'OWNER' || userRole === 'MODERADOR';

  const handlePriorityChange = useCallback(
    async (e: React.ChangeEvent<HTMLSelectElement>) => {
      if (!task) return;
      const newPriority = e.target.value;
      try {
        await changeTaskPriority(task.id, newPriority);
        const updated = await getTask(task.id);
        setTask(updated);
      } catch (err) {
        handleError(err, 'Error al cambiar prioridad');
      }
    },
    [task, handleError],
  );

  const handleStatusChange = useCallback(
    async (e: React.ChangeEvent<HTMLSelectElement>) => {
      if (!task) return;
      const newStatus = e.target.value;
      try {
        await changeTaskStatus(task.id, newStatus);
        const updated = await getTask(task.id);
        setTask(updated);
      } catch (err) {
        handleError(err, 'Error al cambiar estado');
      }
    },
    [task, handleError],
  );

  if (loading)
    return (
      <div className="text-center py-4">
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
        <p className="mt-2">Cargando detalles...</p>
      </div>
    );

  if (error)
    return (
      <div className="alert alert-danger">
        <i className="fas fa-exclamation-triangle me-2"></i>
        {error}
      </div>
    );

  if (!task)
    return (
      <div className="alert alert-warning">
        <i className="fas fa-exclamation-circle me-2"></i>
        No se encontró la tarea.
      </div>
    );

  return (
    <div>
      <h3>Detalle de Tarea</h3>
      <button className="btn btn-secondary mb-3" onClick={onBack} aria-label="Volver a la lista de tareas">
        <i className="fas fa-arrow-left me-2" aria-hidden="true"></i>
        Volver
      </button>
      <ul className="list-group">
        <li className="list-group-item">
          <b>ID:</b> {task.id}
        </li>
        <li className="list-group-item">
          <b>Título:</b> {task.title}
        </li>
        <li className="list-group-item">
          <b>Descripción:</b> {task.description}
        </li>
        <li className="list-group-item">
          <b>Fecha de creación:</b> {task.createTime}
        </li>
        <li className="list-group-item">
          <b>Fecha de actualización:</b> {task.updateTime}
        </li>
        <li className="list-group-item">
          <b>Fecha límite:</b> {task.deadline || 'Sin fecha'}
        </li>
        <li className="list-group-item">
          <b>Prioridad:</b>{' '}
          {task.priority?.name ||
            (task.priority?.id && priorities.length > 0 ? priorities.find((p: any) => p.id === task.priority.id)?.name : 'Sin prioridad') ||
            'Sin prioridad'}
          {canManage && (
            <select
              className="form-select mt-2"
              value={
                task.priority?.name ||
                (task.priority?.id && priorities.length > 0 ? priorities.find((p: any) => p.id === task.priority.id)?.name : '')
              }
              onChange={handlePriorityChange}
            >
              <option value="">Selecciona prioridad</option>
              {priorities.map((p: any) => (
                <option key={p.name} value={p.name}>
                  {p.name}
                </option>
              ))}
            </select>
          )}
        </li>
        <li className="list-group-item">
          <b>Estado:</b>{' '}
          {task.status?.name ||
            (task.status?.id && statuses.length > 0 ? statuses.find((s: any) => s.id === task.status.id)?.name : 'Sin estado') ||
            'Sin estado'}
          {canManage && (
            <select className="form-select mt-2" value={task.status?.name || ''} onChange={handleStatusChange}>
              <option value="">Selecciona estado</option>
              {statuses.map((s: any) => (
                <option key={s.name} value={s.name}>
                  {s.name}
                </option>
              ))}
            </select>
          )}
        </li>
        <li className="list-group-item">
          <b>Grupo de trabajo:</b> {task.workGroup?.name || workGroupName || 'Sin grupo'}
        </li>
        <li className="list-group-item">
          <b>Proyecto:</b> {task.parentProject?.title || projectTitle || 'Sin proyecto'}
        </li>
        <li className="list-group-item">
          <b>Archivada:</b> {task.archived ? 'Sí' : 'No'}
        </li>
      </ul>
      <TaskComments taskId={task.id} />
      {/* Solo ADMIN, OWNER y MODERADOR pueden gestionar asignaciones de usuarios, pero todos ven la lista */}
      <TaskAssignments taskId={task.id} canManage={canManage} />
    </div>
  );
};

export default TaskDetail;
