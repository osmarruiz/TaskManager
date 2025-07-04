import React, { useEffect, useState } from 'react';
import { Project } from '../../shared/model/project.model';
import { getProjects } from '../../shared/util/project-api';
import { getTasks, archiveTask, unarchiveTask, deleteTask } from '../../shared/util/task-api';
import { Task } from '../../shared/model/task.model';
import {
  getProjectTasks,
  addTaskToProject,
  removeTaskFromProject,
  assignUserToProject,
  getProjectMembers,
  removeMemberFromProject,
} from '../../shared/util/project-api';
import { getUsers } from '../../shared/util/user-api';
import { IUser } from '../../shared/model/user.model';
import { getWorkGroup, getMembers } from '../../shared/util/work-group-api';
import { WorkGroup } from '../../shared/model/work-group.model';
import { ProjectMember } from '../../shared/model/project-member.model';
import { MemberWithRole } from '../../shared/model/member-with-role.model';
import TaskDetail from '../task/TaskDetail';
import { useAppSelector } from '../../config/store';
import { hasAnyAuthority } from '../../shared/auth/private-route';
import { AUTHORITIES } from '../../config/constants';
import { WORKGROUP_ROLES } from '../../shared/util/role-utils';
import TaskForm from '../task/TaskForm';

interface Props {
  id: number;
  onBack: () => void;
}

const ProjectDetail: React.FC<Props> = ({ id, onBack }) => {
  const account = useAppSelector(state => state.authentication.account);
  const [project, setProject] = useState<Project | null>(null);
  const [workGroup, setWorkGroup] = useState<WorkGroup | null>(null);
  const [workGroupMembers, setWorkGroupMembers] = useState<MemberWithRole[]>([]);
  const [projectMembers, setProjectMembers] = useState<ProjectMember[]>([]);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [projectTasks, setProjectTasks] = useState<Task[]>([]);
  const [selectedTaskId, setSelectedTaskId] = useState<number | null>(null);
  const [allUsers, setAllUsers] = useState<IUser[]>([]);
  const [selectedUser, setSelectedUser] = useState('');
  const [newTaskTitle, setNewTaskTitle] = useState('');
  const [newTaskDescription, setNewTaskDescription] = useState('');
  const [saving, setSaving] = useState(false);
  const [taskToEdit, setTaskToEdit] = useState<Task | null>(null);

  // Estados para permisos
  const [userRoleInWorkGroup, setUserRoleInWorkGroup] = useState<string | undefined>(undefined);
  const [isOwner, setIsOwner] = useState(false);
  const [isModerator, setIsModerator] = useState(false);
  const [isMember, setIsMember] = useState(false);
  const [canAddMembers, setCanAddMembers] = useState(false);
  const [canRemoveMembers, setCanRemoveMembers] = useState(false);
  const [canManageTasks, setCanManageTasks] = useState(false);

  // Funciones de verificación de permisos
  const isAdmin = hasAnyAuthority(account.authorities, [AUTHORITIES.ADMIN]);

  // Calcular permisos cuando cambien los miembros del grupo
  useEffect(() => {
    const userMember = workGroupMembers.find(member => member.userLogin === account.login);
    const role = userMember?.role;
    setUserRoleInWorkGroup(role);

    // Comparación de roles
    const owner = role === 'OWNER';
    const moderator = role === 'MODERADOR';
    const member = role === 'MIEMBRO';

    setIsOwner(owner);
    setIsModerator(moderator);
    setIsMember(member);

    // Verificar si puede agregar miembros (ADMIN, OWNER, MODERADOR)
    setCanAddMembers(isAdmin || owner || moderator);

    // Verificar si puede quitar miembros (ADMIN, OWNER)
    setCanRemoveMembers(isAdmin || owner);

    // Verificar si puede gestionar tareas (ADMIN, OWNER, MODERADOR)
    setCanManageTasks(isAdmin || owner || moderator);

    // Debug: Mostrar roles del usuario
    console.warn('=== DEBUG PERMISOS ===');
    console.warn('User login:', account.login);
    console.warn('WorkGroup members length:', workGroupMembers.length);
    console.warn('WorkGroup members:', workGroupMembers);
    console.warn('User member found:', userMember);
    console.warn('User role in workgroup:', role);
    console.warn('Role type:', typeof role);
    console.warn('Role comparison - OWNER:', role === 'OWNER');
    console.warn('Role comparison - MODERADOR:', role === 'MODERADOR');
    console.warn('Role comparison - MIEMBRO:', role === 'MIEMBRO');
    console.warn('isAdmin:', isAdmin);
    console.warn('isOwner:', owner);
    console.warn('isModerator:', moderator);
    console.warn('isMember:', member);
    console.warn('canAddMembers:', isAdmin || owner || moderator);
    console.warn('canRemoveMembers:', isAdmin || owner);
    console.warn('canManageTasks:', isAdmin || owner || moderator);
    console.warn('=== FIN DEBUG ===');
  }, [workGroupMembers, account.login, isAdmin]);

  // Función para quitar miembros del proyecto
  const handleRemoveMember = async (memberId: number): Promise<void> => {
    if (window.confirm('¿Seguro que deseas quitar este miembro del proyecto?')) {
      setSaving(true);
      try {
        await removeMemberFromProject(id, memberId);
        // Recargar miembros del proyecto
        const updatedMembers = await getProjectMembers(id);
        setProjectMembers(updatedMembers);
      } catch (error) {
        console.error('Error al quitar miembro del proyecto:', error);
      } finally {
        setSaving(false);
      }
    }
  };

  useEffect(() => {
    const loadProjectData = async () => {
      try {
        const projectsData = await getProjects();
        const found = projectsData.find(p => p.id === id);
        setProject(found || null);

        if (found?.workGroup?.id) {
          // Cargar información completa del WorkGroup
          try {
            const workGroupData = await getWorkGroup(found.workGroup.id);
            setWorkGroup(workGroupData);

            // Cargar miembros del WorkGroup
            const membersData = await getMembers(found.workGroup.id);
            console.warn('Members data loaded:', membersData);
            setWorkGroupMembers(membersData);
          } catch (error) {
            console.error('Error cargando información del WorkGroup:', error);
          }
        }

        const projectTasksData = await getProjectTasks(id);
        setProjectTasks(projectTasksData);

        const projectMembersData = await getProjectMembers(id);
        setProjectMembers(projectMembersData);

        const usersData = await getUsers();
        setAllUsers(usersData);
      } catch (error) {
        console.error('Error cargando datos del proyecto:', error);
      } finally {
        setLoading(false);
      }
    };

    loadProjectData();
  }, [id]);

  const handleAddTask = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    await addTaskToProject(id, { title: newTaskTitle, description: newTaskDescription, workGroup: project?.workGroup } as Task);
    setNewTaskTitle('');
    setNewTaskDescription('');
    setSaving(false);
    getProjectTasks(id).then(setProjectTasks);
  };

  const handleRemoveTask = async (taskId: number) => {
    if (window.confirm('¿Seguro que deseas eliminar esta tarea?')) {
      try {
        await deleteTask(taskId);
        getProjectTasks(id).then(setProjectTasks);
      } catch (error) {
        console.error('Error al eliminar tarea:', error);
        alert('No se puede eliminar esta tarea. Asegúrate de que esté archivada primero.');
      }
    }
  };

  const handleArchive = async (taskId: number) => {
    if (window.confirm('¿Seguro que deseas archivar esta tarea?')) {
      try {
        await archiveTask(taskId);
        getProjectTasks(id).then(setProjectTasks);
      } catch (error) {
        console.error('Error al archivar tarea:', error);
        alert('No se puede archivar esta tarea. Asegúrate de que esté en estado "DONE".');
      }
    }
  };

  const handleUnarchive = async (taskId: number) => {
    if (window.confirm('¿Seguro que deseas desarchivar esta tarea?')) {
      try {
        await unarchiveTask(taskId);
        getProjectTasks(id).then(setProjectTasks);
      } catch (error) {
        console.error('Error al desarchivar tarea:', error);
        alert('Error al desarchivar la tarea.');
      }
    }
  };

  const handleEditTask = (task: Task) => {
    setTaskToEdit(task);
  };

  const handleAssignUser = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedUser) return;
    setSaving(true);
    await assignUserToProject(id, selectedUser);
    setSelectedUser('');
    setSaving(false);
    // Recargar miembros del proyecto
    const updatedMembers = await getProjectMembers(id);
    setProjectMembers(updatedMembers);
  };

  // Filtrar usuarios que son miembros del grupo de trabajo
  const getWorkGroupUserLogins = () => {
    return workGroupMembers.map(member => member.userLogin);
  };

  const filteredUsers = allUsers.filter(
    user => getWorkGroupUserLogins().includes(user.login) && !projectMembers.some(member => member.user.login === user.login),
  );

  if (loading) return <div>Cargando detalle...</div>;
  if (!project) return <div>No se encontró el proyecto.</div>;

  if (selectedTaskId) {
    return <TaskDetail id={selectedTaskId} onBack={() => setSelectedTaskId(null)} />;
  }

  if (taskToEdit) {
    return (
      <TaskForm
        taskToEdit={taskToEdit}
        onSuccess={() => {
          setTaskToEdit(null);
          getProjectTasks(id).then(setProjectTasks);
        }}
      />
    );
  }

  return (
    <div>
      <h3>Detalle de Proyecto</h3>
      <button className="btn btn-secondary mb-3" onClick={onBack}>
        Volver
      </button>
      <ul className="list-group mb-3">
        <li className="list-group-item">
          <b>ID:</b> {project.id}
        </li>
        <li className="list-group-item">
          <b>Título:</b> {project.title}
        </li>
        <li className="list-group-item">
          <b>Descripción:</b> {project.description}
        </li>
        <li className="list-group-item">
          <b>Fecha inicio:</b> {project.startDate}
        </li>
        <li className="list-group-item">
          <b>Fecha fin:</b> {project.endDate}
        </li>
        <li className="list-group-item">
          <b>Grupo de trabajo:</b> {workGroup?.name || project.workGroup?.name || 'Sin grupo asignado'}
          {workGroupMembers.length > 0 && <span className="text-muted ms-2">({workGroupMembers.length} miembros)</span>}
        </li>
      </ul>
      <h5>Tareas asociadas</h5>
      {canManageTasks && (
        <form onSubmit={handleAddTask} className="mb-3 d-flex">
          <input
            className="form-control me-2"
            placeholder="Título de la tarea"
            value={newTaskTitle}
            onChange={e => setNewTaskTitle(e.target.value)}
            required
          />
          <input
            className="form-control me-2"
            placeholder="Descripción"
            value={newTaskDescription}
            onChange={e => setNewTaskDescription(e.target.value)}
            required
          />
          <button className="btn btn-success" type="submit" disabled={saving}>
            Agregar tarea
          </button>
        </form>
      )}
      <ul className="list-group mb-3">
        {projectTasks.map(t => (
          <li key={t.id} className="list-group-item d-flex justify-content-between align-items-center">
            <span>
              {t.title} - {t.status?.name}
              {t.archived && <span className="badge bg-secondary ms-2">Archivada</span>}
            </span>
            <div>
              <button className="btn btn-info btn-sm me-2" onClick={() => setSelectedTaskId(t.id)}>
                Ver
              </button>
              {canManageTasks && (
                <>
                  <button className="btn btn-primary btn-sm me-2" onClick={() => handleEditTask(t)}>
                    Editar
                  </button>
                  {t.archived ? (
                    <button className="btn btn-success btn-sm me-2" onClick={() => handleUnarchive(t.id)}>
                      Desarchivar
                    </button>
                  ) : (
                    <button className="btn btn-warning btn-sm me-2" onClick={() => handleArchive(t.id)}>
                      Archivar
                    </button>
                  )}
                  <button className="btn btn-danger btn-sm" onClick={() => handleRemoveTask(t.id)}>
                    Eliminar
                  </button>
                </>
              )}
            </div>
          </li>
        ))}
        {projectTasks.length === 0 && <li className="list-group-item">Sin tareas asociadas</li>}
      </ul>
      {/* Solo ADMIN, OWNER y MODERADOR pueden asignar usuarios al proyecto */}
      {canAddMembers && (
        <>
          <h5>Asignar usuario al proyecto</h5>
          <form onSubmit={handleAssignUser} className="mb-3 d-flex">
            <select className="form-control me-2" value={selectedUser} onChange={e => setSelectedUser(e.target.value)} required>
              <option value="">Selecciona usuario del grupo de trabajo</option>
              {filteredUsers.map(u => (
                <option key={u.login} value={u.login}>
                  {u.login} ({u.email})
                </option>
              ))}
            </select>
            <button className="btn btn-primary" type="submit" disabled={saving || !selectedUser}>
              Asignar
            </button>
          </form>
          {filteredUsers.length === 0 && workGroupMembers.length > 0 && (
            <div className="alert alert-warning">
              No se encontraron usuarios del sistema que coincidan con los miembros del grupo de trabajo.
            </div>
          )}
          {workGroupMembers.length === 0 && <div className="alert alert-info">Este grupo de trabajo no tiene miembros asignados.</div>}
        </>
      )}

      <h5>Miembros asignados al proyecto</h5>
      <ul className="list-group mb-3">
        {projectMembers.map(member => (
          <li key={member.id} className="list-group-item d-flex justify-content-between align-items-center">
            <span>
              <strong>{member.user.login}</strong> ({member.user.email})
              <br />
              <small className="text-muted">Asignado: {new Date(member.assignedAt).toLocaleDateString()}</small>
            </span>
            {canRemoveMembers && (
              <button className="btn btn-danger btn-sm" onClick={() => handleRemoveMember(member.id)} title="Quitar del proyecto">
                Quitar
              </button>
            )}
          </li>
        ))}
        {projectMembers.length === 0 && <li className="list-group-item">No hay miembros asignados al proyecto</li>}
      </ul>
    </div>
  );
};

export default ProjectDetail;
