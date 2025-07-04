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
import { getProjectsByWorkGroupId } from '../../shared/util/project-api';
import { useAppSelector } from 'app/config/store';
import { useIsAdmin } from '../../shared/util/role-utils';
import { Project } from '../../shared/model/project.model';
import ProjectForm from '../project/ProjectForm';
import ProjectDetail from '../project/ProjectDetail';

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
  const [projects, setProjects] = useState<Project[]>([]);
  const account = useAppSelector(state => state.authentication.account);
  const isAdmin = useIsAdmin();
  const [userRole, setUserRole] = useState<string>('');
  const [showProjectForm, setShowProjectForm] = useState(false);
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(null);

  const loadData = () => {
    setLoading(true);
    Promise.all([getWorkGroup(id), getMembers(id), getUsers(), getProjectsByWorkGroupId(id)]).then(([g, m, u, p]) => {
      setGroup(g);
      setMembers(m);
      setAllUsers(u);
      setProjects(p);
      const current = m.find(mem => mem.userLogin === account.login);
      setUserRole(current ? current.role : '');
      setLoading(false);
    });
  };

  useEffect(() => {
    loadData();
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

  const canTransferOwnership = isAdmin || userRole === 'OWNER';
  const canAddRemoveModerator = isAdmin || userRole === 'OWNER';
  const canAddRemoveMember = isAdmin || userRole === 'OWNER' || userRole === 'MODERADOR';
  const canLeaveGroup = isAdmin || userRole === 'MODERADOR' || userRole === 'MIEMBRO';

  // Filtrar miembros para transferir ownership (excluir al OWNER actual)
  const membersForOwnershipTransfer = members.filter(m => m.role !== 'OWNER');

  // Filtrar miembros para agregar como moderadores (excluir al OWNER actual y moderadores existentes)
  const membersForModerator = members.filter(m => m.role !== 'OWNER' && m.role !== 'MODERADOR');

  // Función para obtener usuarios disponibles para agregar como miembros
  const getAvailableUsers = () => {
    return allUsers.filter(user => {
      // Verificar si el usuario ya es miembro del grupo
      const isAlreadyMember = members.some(member => {
        // Comparar usando diferentes campos posibles
        return (
          member.userLogin === user.login || member.userId === user.id || member.user?.login === user.login || member.user?.id === user.id
        );
      });
      return !isAlreadyMember;
    });
  };

  const availableUsers = getAvailableUsers();

  if (selectedProjectId) {
    return <ProjectDetail id={selectedProjectId} onBack={() => setSelectedProjectId(null)} />;
  }

  return (
    <div style={{ maxWidth: 700, margin: '0 auto', padding: 24 }}>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3 style={{ color: '#1976d2' }}>Detalle de Grupo de Trabajo</h3>
        <button className="btn btn-secondary" onClick={onBack}>
          Volver
        </button>
      </div>
      {/* Botón para agregar proyecto */}
      {(isAdmin || userRole === 'OWNER') && (
        <button className="btn btn-success mb-3" onClick={() => setShowProjectForm(true)}>
          + Agregar proyecto
        </button>
      )}
      {/* Formulario modal para crear proyecto */}
      {showProjectForm && (
        <ProjectForm
          workGroupId={group.id}
          onSuccess={() => {
            setShowProjectForm(false);
            loadData();
          }}
          onCancel={() => setShowProjectForm(false)}
        />
      )}
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
        <li className="list-group-item">
          <b>Tu rol en este grupo:</b> {isAdmin ? 'ADMIN' : userRole || 'Sin rol'}
        </li>
      </ul>
      <h5 className="mt-4">Proyectos relacionados</h5>
      <ul className="list-group mb-3">
        {projects.map(p => (
          <li key={p.id} className="list-group-item d-flex justify-content-between align-items-center">
            <span>{p.title}</span>
            <button className="btn btn-info btn-sm" onClick={() => setSelectedProjectId(p.id)}>
              Ver
            </button>
          </li>
        ))}
        {projects.length === 0 && <li className="list-group-item">Sin proyectos</li>}
      </ul>
      <h5 className="mt-4">Agregar miembro</h5>
      <form onSubmit={handleAddMember} className="mb-3 d-flex">
        <select className="form-control me-2" value={selectedUser} onChange={e => setSelectedUser(e.target.value)} required>
          <option value="">Selecciona usuario</option>
          {availableUsers.map(u => (
            <option key={u.id} value={u.login}>
              {u.login} ({u.firstName} {u.lastName})
            </option>
          ))}
        </select>
        <button className="btn btn-success" type="submit" disabled={saving || !selectedUser}>
          Agregar
        </button>
      </form>
      {/* Debug info - remover en producción */}
      <div className="small text-muted mb-2">
        <strong>Debug:</strong> {allUsers.length} usuarios totales, {members.length} miembros actuales,
        {availableUsers.length} usuarios disponibles
      </div>
      <h5 className="mt-4">Miembros</h5>
      <ul className="list-group mb-3">
        {members.map(m => (
          <li key={m.userLogin} className="list-group-item d-flex justify-content-between align-items-center">
            <span>
              {m.userLogin} ({m.userName}) - {m.role}
            </span>
            {canAddRemoveMember && m.role !== 'OWNER' && (
              <button className="btn btn-danger btn-sm" onClick={() => handleRemoveMember(m.userLogin)}>
                Quitar
              </button>
            )}
          </li>
        ))}
        {members.length === 0 && <li className="list-group-item">Sin miembros</li>}
      </ul>
      {canTransferOwnership && (
        <>
          <h5>Transferir ownership</h5>
          <form onSubmit={handleTransferOwnership} className="mb-3 d-flex">
            <select className="form-control me-2" value={newOwner} onChange={e => setNewOwner(e.target.value)} required>
              <option value="">Selecciona nuevo owner</option>
              {membersForOwnershipTransfer.map(m => (
                <option key={m.userLogin} value={m.userLogin}>
                  {m.userLogin} ({m.userName})
                </option>
              ))}
            </select>
            <button className="btn btn-warning" type="submit" disabled={saving || !newOwner}>
              Transferir
            </button>
          </form>
        </>
      )}
      <h5 className="mt-4">Moderadores</h5>
      {canAddRemoveModerator && (
        <form onSubmit={handleAddModerator} className="mb-3 d-flex">
          <select className="form-control me-2" value={selectedModerator} onChange={e => setSelectedModerator(e.target.value)} required>
            <option value="">Selecciona usuario</option>
            {membersForModerator.map(m => (
              <option key={m.userLogin} value={m.userLogin}>
                {m.userLogin} ({m.userName})
              </option>
            ))}
          </select>
          <button className="btn btn-info" type="submit" disabled={saving || !selectedModerator}>
            Agregar moderador
          </button>
        </form>
      )}
      <ul className="list-group mb-3">
        {members
          .filter(m => m.role === 'MODERADOR')
          .map(m => (
            <li key={m.userLogin} className="list-group-item d-flex justify-content-between align-items-center">
              <span>
                {m.userLogin} ({m.userName})
              </span>
              {canAddRemoveModerator && (
                <button className="btn btn-danger btn-sm" onClick={() => handleRemoveModerator(m.userLogin)}>
                  Quitar moderador
                </button>
              )}
            </li>
          ))}
        {members.filter(m => m.role === 'MODERADOR').length === 0 && <li className="list-group-item">Sin moderadores</li>}
      </ul>
      {canLeaveGroup && (
        <button className="btn btn-outline-danger" onClick={handleLeaveGroup} disabled={saving}>
          Salir del grupo
        </button>
      )}
      {error && <div className="alert alert-danger mt-3">{error}</div>}
    </div>
  );
};

export default WorkGroupDetail;
