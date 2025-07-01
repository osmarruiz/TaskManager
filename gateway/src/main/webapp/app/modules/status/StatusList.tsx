import React, { useEffect, useState } from 'react';
import { getStatuses, deleteStatus } from '../../shared/util/status-api';
import { TaskStatusCatalog } from '../../shared/model/status.model';
import StatusForm from './StatusForm';

const StatusList: React.FC = () => {
  const [statuses, setStatuses] = useState<TaskStatusCatalog[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [statusToEdit, setStatusToEdit] = useState<TaskStatusCatalog | null>(null);

  const loadStatuses = () => {
    setLoading(true);
    getStatuses().then(data => {
      setStatuses(data);
      setLoading(false);
    });
  };

  useEffect(() => {
    loadStatuses();
  }, []);

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Seguro que deseas eliminar este estado?')) {
      await deleteStatus(id);
      loadStatuses();
    }
  };

  if (loading) return <div>Cargando estados...</div>;
  if (statusToEdit) {
    return (
      <StatusForm
        statusToEdit={statusToEdit}
        onSuccess={() => {
          setStatusToEdit(null);
          loadStatuses();
        }}
      />
    );
  }

  return (
    <div>
      <h2>Estados de Tarea</h2>
      <button className="btn btn-success mb-3" onClick={() => setShowForm(!showForm)}>
        {showForm ? 'Cerrar formulario' : 'Nuevo estado'}
      </button>
      {showForm && (
        <StatusForm
          onSuccess={() => {
            setShowForm(false);
            loadStatuses();
          }}
        />
      )}
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Descripción</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {statuses.map(status => (
            <tr key={status.id}>
              <td>{status.id}</td>
              <td>{status.name}</td>
              <td>{status.description}</td>
              <td>
                <button className="btn btn-primary btn-sm me-2" onClick={() => setStatusToEdit(status)}>
                  Editar
                </button>
                <button className="btn btn-danger btn-sm" onClick={() => handleDelete(status.id)}>
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

export default StatusList;
