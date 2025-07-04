import React, { useEffect, useState } from 'react';
import { getStatuses, deleteStatus } from '../../shared/util/status-api';
import { TaskStatusCatalog } from '../../shared/model/status.model';
import StatusForm from './StatusForm';
import StatusPermissionGuard from './StatusPermissionGuard';
import { showApiError, showSuccessMessage } from '../../shared/util/error-utils';

// Estilos para la pantalla de acceso denegado
const accessDeniedStyles = {
  card: {
    boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
    border: 'none',
    borderRadius: '8px',
  },
  header: {
    borderTopLeftRadius: '8px',
    borderTopRightRadius: '8px',
  },
  icon: {
    color: '#6c757d',
    marginBottom: '1rem',
  },
  button: {
    borderRadius: '6px',
    padding: '8px 16px',
  },
};

const StatusList: React.FC = () => {
  const [statuses, setStatuses] = useState<TaskStatusCatalog[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [statusToEdit, setStatusToEdit] = useState<TaskStatusCatalog | null>(null);
  const [accessDenied, setAccessDenied] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const loadStatuses = () => {
    setLoading(true);
    setAccessDenied(false);
    setErrorMessage('');

    getStatuses()
      .then(data => {
        setStatuses(data);
        setLoading(false);
      })
      .catch(error => {
        setLoading(false);

        // Verificar si es un error de acceso denegado
        if (error.response?.status === 403) {
          setAccessDenied(true);
          setErrorMessage(
            'No tienes permisos para acceder a la gestión de estados de tarea. Solo los administradores, propietarios y moderadores de grupos de trabajo pueden gestionar los estados.',
          );
        } else {
          showApiError(error, 'Error al cargar estados de tarea');
        }
      });
  };

  useEffect(() => {
    loadStatuses();
  }, []);

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Seguro que deseas eliminar este estado?')) {
      try {
        await deleteStatus(id);
        showSuccessMessage('Estado eliminado exitosamente');
        loadStatuses();
      } catch (error) {
        // El error ya se maneja en el interceptor de axios
        console.error('Error al eliminar estado:', error);
      }
    }
  };

  // Mostrar pantalla de acceso denegado
  if (accessDenied) {
    return (
      <div className="container mt-4">
        <div className="row justify-content-center">
          <div className="col-md-8">
            <div className="card" style={accessDeniedStyles.card}>
              <div className="card-header bg-danger text-white" style={accessDeniedStyles.header}>
                <h4 className="mb-0">
                  <i className="fas fa-exclamation-triangle me-2"></i>
                  Acceso Denegado
                </h4>
              </div>
              <div className="card-body">
                <div className="alert alert-danger">
                  <h5>No tienes permisos para acceder a este módulo</h5>
                  <p className="mb-0">{errorMessage}</p>
                </div>
                <div className="text-center">
                  <i className="fas fa-lock fa-3x" style={accessDeniedStyles.icon}></i>
                  <p className="text-muted">
                    Solo los administradores, propietarios y moderadores de grupos de trabajo pueden gestionar los estados de tarea.
                  </p>
                  <div className="mt-3">
                    <small className="text-muted">
                      <i className="fas fa-info-circle me-1"></i>
                      Contacta a un administrador si necesitas acceso a esta funcionalidad.
                    </small>
                  </div>
                  <div className="mt-4">
                    <button className="btn btn-primary" style={accessDeniedStyles.button} onClick={() => window.history.back()}>
                      <i className="fas fa-arrow-left me-2"></i>
                      Volver
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (loading) return <div>Cargando estados...</div>;

  if (statusToEdit) {
    return (
      <StatusPermissionGuard>
        <StatusForm
          statusToEdit={statusToEdit}
          onSuccess={() => {
            setStatusToEdit(null);
            loadStatuses();
          }}
        />
      </StatusPermissionGuard>
    );
  }

  return (
    <StatusPermissionGuard>
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
    </StatusPermissionGuard>
  );
};

export default StatusList;
