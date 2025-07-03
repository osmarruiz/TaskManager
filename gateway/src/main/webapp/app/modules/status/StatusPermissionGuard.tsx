import React from 'react';
import { useIsAdmin } from '../../shared/util/role-utils';

interface StatusPermissionGuardProps {
  children: React.ReactNode;
}

const StatusPermissionGuard: React.FC<StatusPermissionGuardProps> = ({ children }) => {
  const isAdmin = useIsAdmin();

  // Permitir acceso a todos los usuarios autenticados
  // La verificación real de permisos se hace en el backend
  // basándose en los roles de WorkGroupMembership
  //
  // Nota: Los usuarios verán una pantalla de acceso denegado si no tienen permisos
  // cuando intenten cargar los datos o realizar operaciones
  return <>{children}</>;
};

export default StatusPermissionGuard;
