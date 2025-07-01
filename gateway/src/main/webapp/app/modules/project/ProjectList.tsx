import React, { useEffect, useState } from 'react';
import { getProjects, deleteProject } from '../../shared/util/project-api';
import { Project } from '../../shared/model/project.model';
import ProjectForm from './ProjectForm';
import ProjectDetail from './ProjectDetail';

const ProjectList: React.FC = () => {
  const [projects, setProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [projectToEdit, setProjectToEdit] = useState<Project | null>(null);
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(null);

  const loadProjects = () => {
    setLoading(true);
    getProjects().then(data => {
      setProjects(data);
      setLoading(false);
    });
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Seguro que deseas eliminar este proyecto?')) {
      await deleteProject(id);
      loadProjects();
    }
  };

  useEffect(() => {
    loadProjects();
  }, []);

  if (loading) return <div>Cargando proyectos...</div>;

  if (projectToEdit) {
    return (
      <ProjectForm
        projectToEdit={projectToEdit}
        onSuccess={() => {
          setProjectToEdit(null);
          loadProjects();
        }}
      />
    );
  }

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
              <td>{project.workGroup?.name}</td>
              <td>
                <button className="btn btn-info btn-sm me-2" onClick={() => setSelectedProjectId(project.id)}>
                  Ver
                </button>
                <button className="btn btn-primary btn-sm me-2" onClick={() => setProjectToEdit(project)}>
                  Editar
                </button>
                <button className="btn btn-danger btn-sm" onClick={() => handleDelete(project.id)}>
                  Eliminar
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
