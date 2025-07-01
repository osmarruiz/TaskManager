import React, { useEffect, useState } from 'react';
import { getTask } from '../../shared/util/task-api';
import { Task } from '../../shared/model/task.model';
import TaskComments from './TaskComments';
import TaskAssignments from './TaskAssignments';

interface Props {
  id: number;
  onBack: () => void;
}

const TaskDetail: React.FC<Props> = ({ id, onBack }) => {
  const [task, setTask] = useState<Task | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getTask(id).then(data => {
      setTask(data);
      setLoading(false);
    });
  }, [id]);

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
          <b>Prioridad:</b> {task.priority?.name}
        </li>
        <li className="list-group-item">
          <b>Estado:</b> {task.status?.name}
        </li>
        <li className="list-group-item">
          <b>Grupo de trabajo:</b> {task.workGroup?.name}
        </li>
        <li className="list-group-item">
          <b>Proyecto:</b> {task.parentProject?.title || 'Sin proyecto'}
        </li>
        <li className="list-group-item">
          <b>Archivada:</b> {task.archived ? 'Sí' : 'No'}
        </li>
      </ul>
      <TaskComments taskId={task.id} />
      <TaskAssignments taskId={task.id} />
    </div>
  );
};

export default TaskDetail;
