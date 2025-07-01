import React, { useEffect, useState } from 'react';
import {
  getWorkGroup,
  getMembers,
  transferOwnership,
  addMember,
  removeMember,
  addModerator,
  removeModerator,
  leaveWorkGroup,
} from '../../shared/util/work-group-api';
import { getUsers } from '../../shared/util/user-api';
import { WorkGroup } from '../../shared/model/work-group.model';
import { IUser } from '../../shared/model/user.model';

interface Props {
  id: number;
  onBack: () => void;
}

const WorkGroupDetail: React.FC<Props> = ({ id, onBack }) => {
  const [group, setGroup] = useState<WorkGroup | null>(null);
  const [members, setMembers] = useState<any[]>([]);
  const [allUsers, setAllUsers] = useState<IUser[]>([]);
  const [selectedUser, setSelectedUser] = useState('');
  const [newOwner, setNewOwner] = useState('');
  const [selectedModerator, setSelectedModerator] = useState('');
  const [removeModeratorUser, setRemoveModeratorUser] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadData = () => {
    setLoading(true);
    Promise.all([getWorkGroup(id), getMembers(id), getUsers()]).then(([g, m, u]) => {
      setGroup(g);
      setMembers(m);
      setAllUsers(u);
      setLoading(false);
    });
  };

  useEffect(() => {
    loadData();
    // eslint-disable-next-line
  }, [id]);

  const handleAddMember = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      await addMember(id, selectedUser);
      setSelectedUser('');
      loadData();
    } catch (err) {
      setError('Error al agregar miembro');
    } finally {
      setSaving(false);
    }
  };

  const handleRemoveMember = async (userLogin: string) => {
    if (!window.confirm('¿Seguro que deseas quitar este miembro?')) return;
    setSaving(true);
    setError(null);
    try {
      await removeMember(id, userLogin);
      loadData();
    } catch (err) {
      setError('Error al quitar miembro');
    } finally {
      setSaving(false);
    }
  };

  const handleTransferOwnership = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      await transferOwnership(id, newOwner);
      setNewOwner('');
      loadData();
    } catch (err) {
      setError('Error al transferir ownership');
    } finally {
      setSaving(false);
    }
  };

  const handleAddModerator = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      await addModerator(id, selectedModerator);
      setSelectedModerator('');
      loadData();
    } catch (err) {
      setError('Error al agregar moderador');
    } finally {
      setSaving(false);
    }
  };

  const handleRemoveModerator = async (userId: string) => {
    if (!window.confirm('¿Seguro que deseas quitar este moderador?')) return;
    setSaving(true);
    setError(null);
    try {
      await removeModerator(id, userId);
      loadData();
    } catch (err) {
      setError('Error al quitar moderador');
    } finally {
      setSaving(false);
    }
  };

  const handleLeaveGroup = async () => {
    if (!window.confirm('¿Seguro que deseas salir de este grupo?')) return;
    setSaving(true);
    setError(null);
    try {
      await leaveWorkGroup(id);
      onBack();
    } catch (err) {
      setError('Error al salir del grupo');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div>Cargando detalle...</div>;
  if (!group) return <div>No se encontró el grupo.</div>;

  return (
    <div>
      <h3>Detalle de Grupo de Trabajo</h3>
      <button className="btn btn-secondary mb-3" onClick={onBack}>
        Volver
      </button>
      <ul className="list-group mb-3">
        <li className="list-group-item">
          <b>ID:</b> {group.id}
        </li>
        <li className="list-group-item">
          <b>Nombre:</b> {group.name}
        </li>
        <li className="list-group-item">
          <b>Descripción:</b> {group.description}
        </li>
        <li className="list-group-item">
          <b>Fecha de creación:</b> {group.creationDate}
        </li>
      </ul>
      <h5>Miembros</h5>
      <form onSubmit={handleAddMember} className="mb-3 d-flex">
        <select className="form-control me-2" value={selectedUser} onChange={e => setSelectedUser(e.target.value)} required>
          <option value="">Selecciona usuario</option>
          {allUsers.map(u => (
            <option key={u.login} value={u.login}>
              {u.login} ({u.email})
            </option>
          ))}
        </select>
        <button className="btn btn-success" type="submit" disabled={saving || !selectedUser}>
          Agregar
        </button>
      </form>
      <ul className="list-group mb-3">
        {members.map(m => (
          <li key={m.userLogin} className="list-group-item d-flex justify-content-between align-items-center">
            <span>
              {m.userLogin} ({m.userName}) - {m.role}
            </span>
            <button className="btn btn-danger btn-sm" onClick={() => handleRemoveMember(m.userLogin)}>
              Quitar
            </button>
          </li>
        ))}
        {members.length === 0 && <li className="list-group-item">Sin miembros</li>}
      </ul>
      <h5>Transferir ownership</h5>
      <form onSubmit={handleTransferOwnership} className="mb-3 d-flex">
        <select className="form-control me-2" value={newOwner} onChange={e => setNewOwner(e.target.value)} required>
          <option value="">Selecciona nuevo owner</option>
          {members.map(m => (
            <option key={m.userLogin} value={m.userLogin}>
              {m.userLogin} ({m.userName})
            </option>
          ))}
        </select>
        <button className="btn btn-warning" type="submit" disabled={saving || !newOwner}>
          Transferir
        </button>
      </form>
      <h5>Moderadores</h5>
      <form onSubmit={handleAddModerator} className="mb-3 d-flex">
        <select className="form-control me-2" value={selectedModerator} onChange={e => setSelectedModerator(e.target.value)} required>
          <option value="">Selecciona usuario</option>
          {members.map(m => (
            <option key={m.userLogin} value={m.userLogin}>
              {m.userLogin} ({m.userName})
            </option>
          ))}
        </select>
        <button className="btn btn-info" type="submit" disabled={saving || !selectedModerator}>
          Agregar moderador
        </button>
      </form>
      <ul className="list-group mb-3">
        {members
          .filter(m => m.role === 'MODERADOR')
          .map(m => (
            <li key={m.userLogin} className="list-group-item d-flex justify-content-between align-items-center">
              <span>
                {m.userLogin} ({m.userName})
              </span>
              <button className="btn btn-danger btn-sm" onClick={() => handleRemoveModerator(m.userLogin)}>
                Quitar moderador
              </button>
            </li>
          ))}
        {members.filter(m => m.role === 'MODERADOR').length === 0 && <li className="list-group-item">Sin moderadores</li>}
      </ul>
      <button className="btn btn-outline-danger" onClick={handleLeaveGroup} disabled={saving}>
        Salir del grupo
      </button>
      {error && <div className="alert alert-danger">{error}</div>}
    </div>
  );
};

export default WorkGroupDetail;
