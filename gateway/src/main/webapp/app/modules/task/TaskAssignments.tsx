import React, { useEffect, useState, useCallback } from 'react';
import { getTaskAssignments, assignTask } from '../../shared/util/task-assignment-api';
import { getUsers } from '../../shared/util/user-api';
import { TaskAssignment } from '../../shared/model/task-assignment.model';
import { IUser } from '../../shared/model/user.model';
import { useLoading } from '../../shared/hooks/useLoading';
import { useApiError } from '../../shared/hooks/useApiError';

interface Props {
  taskId: number;
  canManage: boolean;
}

const TaskAssignments: React.FC<Props> = ({ taskId, canManage }) => {
  const { loading, error, startLoading, stopLoading, setLoadingError } = useLoading(true);
  const { handleError } = useApiError();
  const [assignments, setAssignments] = useState<TaskAssignment[]>([]);
  const [users, setUsers] = useState<IUser[]>([]);
  const [selectedUser, setSelectedUser] = useState('');
  const [saving, setSaving] = useState(false);

  const loadAssignments = useCallback(() => {
    startLoading();
    Promise.all([
      getTaskAssignments(taskId).catch(assignmentErr => {
        handleError(assignmentErr, 'Error al cargar asignaciones');
        return [];
      }),
      getUsers().catch(userErr => {
        handleError(userErr, 'Error al cargar usuarios');
        return [];
      }),
    ])
      .then(([asgs, usrs]) => {
        setAssignments(asgs);
        setUsers(usrs);
        stopLoading();
      })
      .catch(err => {
        handleError(err, 'Error al cargar datos');
        setLoadingError('No se pudieron cargar las asignaciones');
      });
  }, [taskId, startLoading, stopLoading, setLoadingError, handleError]);

  useEffect(() => {
    loadAssignments();
  }, [loadAssignments]);

  const handleAssign = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();
      if (!selectedUser) return;
      setSaving(true);
      try {
        await assignTask(taskId, selectedUser);
        setSelectedUser('');
        loadAssignments();
      } catch (err) {
        handleError(err, 'Error al asignar usuario');
      } finally {
        setSaving(false);
      }
    },
    [taskId, selectedUser, loadAssignments, handleError],
  );

  if (loading)
    return (
      <div className="text-center py-3">
        <div className="spinner-border spinner-border-sm" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
        <span className="ms-2">Cargando asignaciones...</span>
      </div>
    );

  if (error)
    return (
      <div className="alert alert-danger">
        <i className="fas fa-exclamation-triangle me-2"></i>
        {error}
      </div>
    );

  return (
    <div>
      <h5>Usuarios asignados</h5>
      <ul className="list-group mb-3">
        {assignments.map(a => (
          <li key={a.id} className="list-group-item">
            <span>
              {a.user?.id ? `ID: ${a.user.id}` : 'Usuario'} ({a.user?.email})
            </span>
          </li>
        ))}
        {assignments.length === 0 && <li className="list-group-item">Sin usuarios asignados</li>}
      </ul>
      {canManage && (
        <form onSubmit={handleAssign} className="d-flex">
          <select
            className="form-control me-2"
            value={selectedUser}
            onChange={e => setSelectedUser(e.target.value)}
            aria-label="Seleccionar usuario para asignar"
          >
            <option value="">Selecciona usuario</option>
            {users.map(u => (
              <option key={u.login} value={u.login}>
                {u.login} ({u.email})
              </option>
            ))}
          </select>
          <button
            className="btn btn-primary"
            type="submit"
            disabled={saving || !selectedUser}
            aria-label={saving ? 'Asignando usuario...' : 'Asignar usuario'}
          >
            {saving ? (
              <>
                <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                Asignando...
              </>
            ) : (
              <>
                <i className="fas fa-user-plus me-2" aria-hidden="true"></i>
                Asignar
              </>
            )}
          </button>
        </form>
      )}
    </div>
  );
};

export default TaskAssignments;
