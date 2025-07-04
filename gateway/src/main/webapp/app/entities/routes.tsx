import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import PrivateRoute from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';
import TaskList from '../modules/task/TaskList';
import ProjectList from '../modules/project/ProjectList';
import WorkGroupList from '../modules/workgroup/WorkGroupList';
import PriorityList from '../modules/priority/PriorityList';
import StatusList from '../modules/status/StatusList';

/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
        <Route path="tareas" element={<TaskList />} />
        <Route path="proyectos" element={<ProjectList />} />
        <Route path="grupos" element={<WorkGroupList />} />
        <Route
          path="prioridades"
          element={
            <PrivateRoute hasAnyAuthorities={[AUTHORITIES.ADMIN]}>
              <PriorityList />
            </PrivateRoute>
          }
        />
        <Route
          path="estados"
          element={
            <PrivateRoute hasAnyAuthorities={[AUTHORITIES.USER]}>
              <StatusList />
            </PrivateRoute>
          }
        />
      </ErrorBoundaryRoutes>
    </div>
  );
};
