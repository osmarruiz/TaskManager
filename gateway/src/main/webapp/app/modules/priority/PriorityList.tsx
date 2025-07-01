import React, { useEffect, useState } from 'react';
import { getPriorities, deletePriority } from '../../shared/util/priority-api';
import { Priority } from '../../shared/model/priority.model';
import PriorityForm from './PriorityForm';

const PriorityList: React.FC = () => {
  const [priorities, setPriorities] = useState<Priority[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [priorityToEdit, setPriorityToEdit] = useState<Priority | null>(null);

  const loadPriorities = () => {
    setLoading(true);
    getPriorities().then(data => {
      setPriorities(data);
      setLoading(false);
    });
  };

  useEffect(() => {
    loadPriorities();
  }, []);

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Seguro que deseas eliminar esta prioridad?')) {
      await deletePriority(id);
      loadPriorities();
    }
  };

  if (loading) return <div>Cargando prioridades...</div>;
  if (priorityToEdit) {
    return (
      <PriorityForm
        priorityToEdit={priorityToEdit}
        onSuccess={() => {
          setPriorityToEdit(null);
          loadPriorities();
        }}
      />
    );
  }

  return (
    <div>
      <h2>Prioridades</h2>
      <button className="btn btn-success mb-3" onClick={() => setShowForm(!showForm)}>
        {showForm ? 'Cerrar formulario' : 'Nueva prioridad'}
      </button>
      {showForm && (
        <PriorityForm
          onSuccess={() => {
            setShowForm(false);
            loadPriorities();
          }}
        />
      )}
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Descripción</th>
            <th>Visible</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {priorities.map(priority => (
            <tr key={priority.id}>
              <td>{priority.id}</td>
              <td>{priority.name}</td>
              <td>{priority.description}</td>
              <td>{priority.visible ? 'Sí' : 'No'}</td>
              <td>
                <button className="btn btn-info btn-sm me-2">Ver</button>
                <button className="btn btn-primary btn-sm me-2" onClick={() => setPriorityToEdit(priority)}>
                  Editar
                </button>
                <button className="btn btn-danger btn-sm" onClick={() => handleDelete(priority.id)}>
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

export default PriorityList;
