import React from 'react';
import { useCanManageTaskStatus, getPermissionErrorMessage } from '../../shared/util/role-utils';

interface StatusPermissionGuardProps {
  children: React.ReactNode;
}

const StatusPermissionGuard: React.FC<StatusPermissionGuardProps> = ({ children }) => {
  const canManageTaskStatus = useCanManageTaskStatus();

  if (!canManageTaskStatus) {
    return (
      <div className="container mt-4">
        <div className="row justify-content-center">
          <div className="col-md-8">
            <div className="card access-denied-card">
              <div className="card-header bg-danger text-white">
                <h4 className="mb-0">
                  <i className="fas fa-exclamation-triangle me-2"></i>
                  Acceso Denegado
                </h4>
              </div>
              <div className="card-body">
                <div className="alert alert-danger">
                  <h5>No tienes permisos para acceder a este módulo</h5>
                  <p className="mb-0">{getPermissionErrorMessage()}</p>
                </div>
                <div className="text-center">
                  <i className="fas fa-lock fa-3x text-muted mb-3"></i>
                  <p className="text-muted">
                    Solo los administradores, propietarios y moderadores de grupos de trabajo pueden gestionar los estados de tarea.
                  </p>
                  <div className="mt-3">
                    <small className="text-muted">
                      <i className="fas fa-info-circle me-1"></i>
                      Contacta a un administrador si necesitas acceso a esta funcionalidad.
                    </small>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return <>{children}</>;
};

export default StatusPermissionGuard;
