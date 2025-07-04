import React, { useEffect, useState } from 'react';
import { getTaskAssignments, assignTask, unassignTask } from '../../shared/util/task-assignment-api';
import { getUsers } from '../../shared/util/user-api';
import { TaskAssignment } from '../../shared/model/task-assignment.model';
import { IUser } from '../../shared/model/user.model';

interface Props {
  taskId: number;
  canManage: boolean;
}

const TaskAssignments: React.FC<Props> = ({ taskId, canManage }) => {
  const [assignments, setAssignments] = useState<TaskAssignment[]>([]);
  const [users, setUsers] = useState<IUser[]>([]);
  const [selectedUser, setSelectedUser] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const loadAssignments = () => {
    setLoading(true);
    Promise.all([
      getTaskAssignments(taskId).catch(err => {
        console.error('Error loading task assignments:', err);
        return [];
      }),
      getUsers().catch(err => {
        console.error('Error loading users:', err);
        return [];
      }),
    ])
      .then(([asgs, usrs]) => {
        setAssignments(asgs);
        setUsers(usrs);
        setLoading(false);
      })
      .catch(err => {
        console.error('Error in loadAssignments:', err);
        setLoading(false);
      });
  };

  useEffect(() => {
    loadAssignments();
  }, [taskId]);

  const handleAssign = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedUser) return;
    setSaving(true);
    await assignTask(taskId, selectedUser);
    setSelectedUser('');
    setSaving(false);
    loadAssignments();
  };

  const handleUnassign = async (userLogin: string) => {
    if (!window.confirm('¿Seguro que deseas desasignar este usuario?')) return;
    setSaving(true);
    await unassignTask(taskId, userLogin);
    setSaving(false);
    loadAssignments();
  };

  if (loading) return <div>Cargando asignaciones...</div>;

  return (
    <div>
      <h5>Usuarios asignados</h5>
      <ul className="list-group mb-3">
        {assignments.map(a => (
          <li key={a.id} className="list-group-item d-flex justify-content-between align-items-center">
            <span>
              {a.user?.id ? `ID: ${a.user.id}` : 'Usuario'} ({a.user?.email})
            </span>
            {canManage && (
              <button className="btn btn-sm btn-danger" onClick={() => handleUnassign(a.user?.login || '')}>
                Desasignar
              </button>
            )}
          </li>
        ))}
        {assignments.length === 0 && <li className="list-group-item">Sin usuarios asignados</li>}
      </ul>
      {canManage && (
        <form onSubmit={handleAssign} className="d-flex">
          <select className="form-control me-2" value={selectedUser} onChange={e => setSelectedUser(e.target.value)}>
            <option value="">Selecciona usuario</option>
            {users.map(u => (
              <option key={u.login} value={u.login}>
                {u.login} ({u.email})
              </option>
            ))}
          </select>
          <button className="btn btn-primary" type="submit" disabled={saving || !selectedUser}>
            {saving ? 'Asignando...' : 'Asignar'}
          </button>
        </form>
      )}
    </div>
  );
};

export default TaskAssignments;
