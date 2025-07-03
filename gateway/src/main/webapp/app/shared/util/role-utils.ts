import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

// Constantes para roles de grupo de trabajo
export const WORKGROUP_ROLES = {
  OWNER: 'ROLE_WORKGROUP_OWNER',
  MODERADOR: 'ROLE_WORKGROUP_MODERADOR',
  MIEMBRO: 'ROLE_WORKGROUP_MIEMBRO',
} as const;

// Hook para verificar si el usuario es administrador
export const useIsAdmin = (): boolean => {
  const account = useAppSelector(state => state.authentication.account);
  return hasAnyAuthority(account.authorities, [AUTHORITIES.ADMIN]);
};

// Hook para verificar si el usuario tiene roles de gestión en grupos de trabajo
export const useHasWorkGroupManagementRole = (): boolean => {
  const account = useAppSelector(state => state.authentication.account);
  const isAdmin = useIsAdmin();

  // Si es admin, tiene acceso completo
  if (isAdmin) {
    return true;
  }

  // Verificar si tiene roles de OWNER o MODERADOR en algún grupo
  return hasAnyAuthority(account.authorities, [WORKGROUP_ROLES.OWNER, WORKGROUP_ROLES.MODERADOR]);
};

// Hook para verificar si el usuario puede gestionar estados de tarea
export const useCanManageTaskStatus = (): boolean => {
  return useHasWorkGroupManagementRole();
};

// Función para obtener el mensaje de error de permisos
export const getPermissionErrorMessage = (): string => {
  return 'No tienes permisos para realizar esta acción. Solo los administradores, propietarios y moderadores de grupos de trabajo pueden gestionar estados de tarea.';
};

// Función para verificar permisos en componentes
export const checkWorkGroupManagementPermission = (authorities: string[]): boolean => {
  if (!authorities || authorities.length === 0) {
    return false;
  }

  // Verificar si es admin
  if (authorities.includes(AUTHORITIES.ADMIN)) {
    return true;
  }

  // Verificar si tiene roles de gestión en grupos
  return authorities.some(auth => auth === WORKGROUP_ROLES.OWNER || auth === WORKGROUP_ROLES.MODERADOR);
};
