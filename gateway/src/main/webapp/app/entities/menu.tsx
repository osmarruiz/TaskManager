import React from 'react';
// eslint-disable-line

import MenuItem from 'app/shared/layout/menus/menu-item'; // eslint-disable-line
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
      <MenuItem icon="asterisk" to="/tareas">
        Tareas
      </MenuItem>
      <MenuItem icon="asterisk" to="/proyectos">
        Proyectos
      </MenuItem>
      <MenuItem icon="asterisk" to="/grupos">
        Grupos de trabajo
      </MenuItem>
      {isAdmin && (
        <MenuItem icon="asterisk" to="/prioridades">
          Prioridades
        </MenuItem>
      )}
      <MenuItem icon="asterisk" to="/estados">
        Estados de Tarea
      </MenuItem>
    </>
  );
};

export default EntitiesMenu;
