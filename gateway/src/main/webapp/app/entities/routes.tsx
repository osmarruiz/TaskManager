import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
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
        <Route path="prioridades" element={<PriorityList />} />
        <Route path="estados" element={<StatusList />} />
      </ErrorBoundaryRoutes>
    </div>
  );
};
