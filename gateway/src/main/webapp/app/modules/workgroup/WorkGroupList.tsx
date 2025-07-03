import React, { useEffect, useState } from 'react';
import { getWorkGroups, deleteWorkGroup, getMyWorkGroups } from '../../shared/util/work-group-api';
import { WorkGroup } from '../../shared/model/work-group.model';
import { UserWorkGroup } from '../../shared/model/user-work-group.model';
import WorkGroupForm from './WorkGroupForm';
import WorkGroupDetail from './WorkGroupDetail';
import { FaCrown } from 'react-icons/fa';
import { FaUserShield } from 'react-icons/fa';
import { FaUserFriends } from 'react-icons/fa';
import { useIsAdmin } from '../../shared/util/role-utils';

const WorkGroupList: React.FC = () => {
  const [groups, setGroups] = useState<WorkGroup[]>([]);
  const [myGroups, setMyGroups] = useState<UserWorkGroup[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [groupToEdit, setGroupToEdit] = useState<WorkGroup | null>(null);
  const [selectedGroupId, setSelectedGroupId] = useState<number | null>(null);
  const isAdmin = useIsAdmin();
  const [showAll, setShowAll] = useState(false);

  const loadGroups = () => {
    setLoading(true);
    getWorkGroups().then(data => {
      setGroups(data);
      setLoading(false);
    });
  };

  const loadMyGroups = () => {
    setLoading(true);
    getMyWorkGroups().then(data => {
      setMyGroups(data);
      setLoading(false);
    });
  };

  useEffect(() => {
    if (isAdmin && showAll) {
      loadGroups();
    } else {
      loadMyGroups();
    }
  }, [isAdmin, showAll]);

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Seguro que deseas eliminar este grupo de trabajo?')) {
      await deleteWorkGroup(id);
      loadGroups();
    }
  };

  const ownerGroups = myGroups.filter(g => g.userRole === 'OWNER');
  const moderatorGroups = myGroups.filter(g => g.userRole === 'MODERADOR');
  const memberGroups = myGroups.filter(g => g.userRole === 'MIEMBRO');

  const cardStyle = {
    borderRadius: '8px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
    padding: '16px',
    marginBottom: '16px',
    background: '#fff',
    borderLeft: '6px solid',
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
  };
  const ownerColor = '#ffd700';
  const moderatorColor = '#4fc3f7';
  const memberColor = '#81c784';

  const renderGroupCard = (g: UserWorkGroup, role: string) => {
    let color;
    let icon;
    if (role === 'OWNER') {
      color = ownerColor;
      icon = '🏆';
    } else if (role === 'MODERADOR') {
      color = moderatorColor;
      icon = '🛡️';
    } else {
      color = memberColor;
      icon = '👥';
    }
    return (
      <div key={g.workGroupId} style={{ ...cardStyle, borderLeftColor: color, position: 'relative' }}>
        <span style={{ fontSize: 28, marginRight: 8 }}>{icon}</span>
        <div style={{ flex: 1 }}>
          <div style={{ fontWeight: 'bold', fontSize: '1.1rem' }}>{g.workGroupName}</div>
          <div style={{ fontSize: '0.95rem', color: '#888' }}>ID: {g.workGroupId}</div>
        </div>
        <button
          className="btn btn-info btn-sm"
          style={{ position: 'absolute', right: 16, top: 16 }}
          onClick={() => setSelectedGroupId(g.workGroupId)}
        >
          Ver
        </button>
      </div>
    );
  };

  if (loading) return <div>Cargando grupos de trabajo...</div>;
  if (groupToEdit) {
    return (
      <WorkGroupForm
        workGroupToEdit={groupToEdit}
        onSuccess={() => {
          setGroupToEdit(null);
          loadGroups();
        }}
      />
    );
  }

  if (selectedGroupId) {
    return <WorkGroupDetail id={selectedGroupId} onBack={() => setSelectedGroupId(null)} />;
  }

  return (
    <div style={{ maxWidth: 600, margin: '0 auto', padding: 24 }}>
      {isAdmin && (
        <button className="btn btn-warning mb-3" onClick={() => setShowAll(s => !s)} style={{ float: 'right' }}>
          {showAll ? 'Ver solo mis grupos' : 'Ver todos los grupos'}
        </button>
      )}
      {!showAll && (
        <>
          <h2 style={{ color: ownerColor, marginTop: 32 }}>Grupos donde eres OWNER</h2>
          <button className="btn btn-success mb-3" onClick={() => setShowForm(!showForm)}>
            {showForm ? 'Cerrar formulario' : 'Nuevo grupo'}
          </button>
          {showForm && (
            <WorkGroupForm
              onSuccess={() => {
                setShowForm(false);
                loadMyGroups();
              }}
            />
          )}
          {ownerGroups.length === 0 && <div style={{ color: '#aaa' }}>No perteneces como OWNER a ningún grupo.</div>}
          {ownerGroups.map(g => renderGroupCard(g, 'OWNER'))}

          <h2 style={{ color: moderatorColor, marginTop: 32 }}>Grupos donde eres MODERADOR</h2>
          {moderatorGroups.length === 0 && <div style={{ color: '#aaa' }}>No perteneces como MODERADOR a ningún grupo.</div>}
          {moderatorGroups.map(g => renderGroupCard(g, 'MODERADOR'))}

          <h2 style={{ color: memberColor, marginTop: 32 }}>Grupos donde eres MIEMBRO</h2>
          {memberGroups.length === 0 && <div style={{ color: '#aaa' }}>No perteneces como MIEMBRO a ningún grupo.</div>}
          {memberGroups.map(g => renderGroupCard(g, 'MIEMBRO'))}
        </>
      )}
      {showAll && (
        <>
          <h2 style={{ color: '#ff9800', marginTop: 32 }}>Todos los grupos de trabajo</h2>
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Descripción</th>
                <th>Fecha de creación</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {groups.map(group => (
                <tr key={group.id}>
                  <td>{group.id}</td>
                  <td>{group.name}</td>
                  <td>{group.description}</td>
                  <td>{group.creationDate}</td>
                  <td>
                    <button className="btn btn-info btn-sm me-2" onClick={() => setSelectedGroupId(group.id)}>
                      Ver
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </div>
  );
};

export default WorkGroupList;
