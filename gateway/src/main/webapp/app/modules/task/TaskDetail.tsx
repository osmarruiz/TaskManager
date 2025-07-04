import React, { useEffect, useState } from 'react';
import { getTask } from '../../shared/util/task-api';
import { Task } from '../../shared/model/task.model';
import TaskComments from './TaskComments';
import TaskAssignments from './TaskAssignments';
import { getPriorities } from '../../shared/util/priority-api';
import { getTaskStatuses } from '../../shared/util/task-status-api';
import { changeTaskPriority, changeTaskStatus } from '../../shared/util/task-api';
import { useAppSelector } from '../../config/store';
import { getMembers, getWorkGroup } from '../../shared/util/work-group-api';
import { MemberWithRole } from '../../shared/model/member-with-role.model';
import { getProjects } from '../../shared/util/project-api';

interface Props {
  id: number;
  onBack: () => void;
}

const TaskDetail: React.FC<Props> = ({ id, onBack }) => {
  const account = useAppSelector(state => state.authentication.account);
  const [task, setTask] = useState<Task | null>(null);
  const [loading, setLoading] = useState(true);
  const [priorities, setPriorities] = useState([]);
  const [statuses, setStatuses] = useState([]);
  const [userRole, setUserRole] = useState<string | null>(null);
  const [workGroupName, setWorkGroupName] = useState<string | null>(null);
  const [projectTitle, setProjectTitle] = useState<string | null>(null);

  useEffect(() => {
    getTask(id).then(data => {
      setTask(data);
      setLoading(false);
      if (data.workGroup?.id) {
        getMembers(data.workGroup.id).then(members => {
          const userMember = members.find(m => m.userLogin === account.login);
          setUserRole(userMember?.role || null);
        });
        if (!data.workGroup.name) {
          getWorkGroup(data.workGroup.id).then(wg => setWorkGroupName(wg.name));
        } else {
          setWorkGroupName(data.workGroup.name);
        }
      }
      if (data.parentProject?.id) {
        if (!data.parentProject.title) {
          getProjects().then(projects => {
            const found = projects.find(p => p.id === data.parentProject.id);
            setProjectTitle(found?.title || null);
          });
        } else {
          setProjectTitle(data.parentProject.title);
        }
      }
    });
    getPriorities().then(setPriorities);
    getTaskStatuses().then(setStatuses);
  }, [id, account.login]);

  const isAdmin = account.authorities?.includes('ROLE_ADMIN');
  const canManage = isAdmin || userRole === 'OWNER' || userRole === 'MODERADOR';

  const handlePriorityChange = async (e: React.ChangeEvent<HTMLSelectElement>) => {
    if (!task) return;
    const newPriority = e.target.value;
    await changeTaskPriority(task.id, newPriority);
    const updated = await getTask(task.id);
    setTask(updated);
  };

  const handleStatusChange = async (e: React.ChangeEvent<HTMLSelectElement>) => {
    if (!task) return;
    const newStatus = e.target.value;
    await changeTaskStatus(task.id, newStatus);
    const updated = await getTask(task.id);
    setTask(updated);
  };

  if (loading) return <div>Cargando detalle...</div>;
  if (!task) return <div>No se encontró la tarea.</div>;

  return (
    <div>
      <h3>Detalle de Tarea</h3>
      <button className="btn btn-secondary mb-3" onClick={onBack}>
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
            <select className="form-select mt-2" value={task.priority?.name || ''} onChange={handlePriorityChange}>
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
