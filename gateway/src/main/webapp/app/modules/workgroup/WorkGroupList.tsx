import React, { useEffect, useState } from 'react';
import { getWorkGroups, deleteWorkGroup } from '../../shared/util/work-group-api';
import { WorkGroup } from '../../shared/model/work-group.model';
import WorkGroupForm from './WorkGroupForm';
import WorkGroupDetail from './WorkGroupDetail';

const WorkGroupList: React.FC = () => {
  const [groups, setGroups] = useState<WorkGroup[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [groupToEdit, setGroupToEdit] = useState<WorkGroup | null>(null);
  const [selectedGroupId, setSelectedGroupId] = useState<number | null>(null);

  const loadGroups = () => {
    setLoading(true);
    getWorkGroups().then(data => {
      setGroups(data);
      setLoading(false);
    });
  };

  useEffect(() => {
    loadGroups();
  }, []);

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Seguro que deseas eliminar este grupo de trabajo?')) {
      await deleteWorkGroup(id);
      loadGroups();
    }
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
    <div>
      <h2>Grupos de Trabajo</h2>
      <button className="btn btn-success mb-3" onClick={() => setShowForm(!showForm)}>
        {showForm ? 'Cerrar formulario' : 'Nuevo grupo'}
      </button>
      {showForm && (
        <WorkGroupForm
          onSuccess={() => {
            setShowForm(false);
            loadGroups();
          }}
        />
      )}
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
                <button className="btn btn-primary btn-sm me-2" onClick={() => setGroupToEdit(group)}>
                  Editar
                </button>
                <button className="btn btn-danger btn-sm" onClick={() => handleDelete(group.id)}>
                  Eliminar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default WorkGroupList;
