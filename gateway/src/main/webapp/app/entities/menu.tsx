import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

const EntitiesMenu = () => {
  const account = useAppSelector(state => state.authentication.account);
  const isAdmin = hasAnyAuthority(account.authorities, [AUTHORITIES.ADMIN]);

  return (
    <>
      {/* prettier-ignore */}
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}

      {/* Encabezado del primer grupo */}
      <div className="dropdown-header">
        <i className="fas fa-tasks me-2"></i>
        Gestión Principal
      </div>

      <MenuItem icon="users" to="/grupos">
        Grupos de trabajo
      </MenuItem>
      {isAdmin && (
        <MenuItem icon="project-diagram" to="/proyectos">
          Proyectos
        </MenuItem>
      )}
      {/* Submenú de Gestión Principal */}
      <MenuItem icon="tasks" to="/tareas">
        Tareas
      </MenuItem>

      {/* Separador visual */}
      <hr className="dropdown-divider" />

      {/* Encabezado del segundo grupo */}
      <div className="dropdown-header">
        <i className="fas fa-cogs me-2"></i>
        Configuración
      </div>

      {/* Submenú de Configuración */}
      {isAdmin && (
        <MenuItem icon="cog" to="/prioridades">
          Prioridades
        </MenuItem>
      )}
      <MenuItem icon="list-check" to="/estados">
        Estados de Tarea
      </MenuItem>
    </>
  );
};

export default EntitiesMenu;
