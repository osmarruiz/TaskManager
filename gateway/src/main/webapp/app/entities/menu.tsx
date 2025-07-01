import React from 'react';
// eslint-disable-line

import MenuItem from 'app/shared/layout/menus/menu-item'; // eslint-disable-line

const EntitiesMenu = () => {
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
    </>
  );
};

export default EntitiesMenu;
