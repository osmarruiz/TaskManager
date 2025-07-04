import React, { useEffect, useState } from 'react';
import { getProjects } from '../../shared/util/project-api';
import { getWorkGroup } from '../../shared/util/work-group-api';
import { Project } from '../../shared/model/project.model';
import { WorkGroup } from '../../shared/model/work-group.model';
import ProjectForm from './ProjectForm';
import ProjectDetail from './ProjectDetail';

const ProjectList: React.FC = () => {
  const [projects, setProjects] = useState<Project[]>([]);
  const [workGroups, setWorkGroups] = useState<Map<number, WorkGroup>>(new Map());
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(null);

  const loadProjects = async () => {
    setLoading(true);
    try {
      const projectsData = await getProjects();
      setProjects(projectsData);

      // Obtener información de WorkGroups únicos
      const uniqueWorkGroupIds = [...new Set(projectsData.filter(project => project.workGroup?.id).map(project => project.workGroup.id))];

      const workGroupsMap = new Map<number, WorkGroup>();

      // Obtener información de cada WorkGroup en paralelo
      const workGroupPromises = uniqueWorkGroupIds.map(async workGroupId => {
        try {
          const workGroup = await getWorkGroup(workGroupId);
          return { id: workGroupId, workGroup };
        } catch (error) {
          console.error(`Error obteniendo WorkGroup ${workGroupId}:`, error);
          return null;
        }
      });

      const workGroupResults = await Promise.all(workGroupPromises);
      workGroupResults.forEach(result => {
        if (result) {
          workGroupsMap.set(result.id, result.workGroup);
        }
      });

      setWorkGroups(workGroupsMap);
    } catch (error) {
      console.error('Error cargando proyectos:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProjects();
  }, []);

  if (loading) return <div>Cargando proyectos...</div>;

  if (selectedProjectId) {
    return <ProjectDetail id={selectedProjectId} onBack={() => setSelectedProjectId(null)} />;
  }

  return (
    <div>
      <h2>Lista de Proyectos</h2>
      <button className="btn btn-success mb-3" onClick={() => setShowForm(!showForm)}>
        {showForm ? 'Cerrar formulario' : 'Nuevo proyecto'}
      </button>
      {showForm && (
        <ProjectForm
          onSuccess={() => {
            setShowForm(false);
            loadProjects();
          }}
        />
      )}
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Título</th>
            <th>Descripción</th>
            <th>Fecha inicio</th>
            <th>Fecha fin</th>
            <th>Grupo de trabajo</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {projects.map(project => (
            <tr key={project.id}>
              <td>{project.id}</td>
              <td>{project.title}</td>
              <td>{project.description}</td>
              <td>{project.startDate}</td>
              <td>{project.endDate}</td>
              <td>{project.workGroup?.id ? workGroups.get(project.workGroup.id)?.name || 'Cargando...' : 'Sin grupo asignado'}</td>
              <td>
                <button className="btn btn-info btn-sm me-2" onClick={() => setSelectedProjectId(project.id)}>
                  Ver
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ProjectList;
