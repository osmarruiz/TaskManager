import React, { useEffect, useState } from 'react';
import { getTasks, deleteTask, archiveTask } from '../../shared/util/task-api';
import { Task } from '../../shared/model/task.model';
import TaskForm from './TaskForm';
import TaskDetail from './TaskDetail';

const TaskList: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [showForm, setShowForm] = useState(false);
  const [selectedTaskId, setSelectedTaskId] = useState<number | null>(null);
  const [taskToEdit, setTaskToEdit] = useState<Task | null>(null);

  const loadTasks = () => {
    setLoading(true);
    getTasks().then(data => {
      setTasks(data);
      setLoading(false);
    });
  };

  useEffect(() => {
    loadTasks();
  }, []);

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Seguro que deseas eliminar esta tarea?')) {
      await deleteTask(id);
      loadTasks();
    }
  };

  const handleArchive = async (id: number) => {
    if (window.confirm('¿Seguro que deseas archivar esta tarea?')) {
      await archiveTask(id);
      loadTasks();
    }
  };

  if (loading) return <div>Cargando tareas...</div>;

  if (selectedTaskId) {
    return <TaskDetail id={selectedTaskId} onBack={() => setSelectedTaskId(null)} />;
  }

  if (taskToEdit) {
    return (
      <TaskForm
        taskToEdit={taskToEdit}
        onSuccess={() => {
          setTaskToEdit(null);
          loadTasks();
        }}
      />
    );
  }

  return (
    <div>
      <h2>Lista de Tareas</h2>
      <button className="btn btn-success mb-3" onClick={() => setShowForm(!showForm)}>
        {showForm ? 'Cerrar formulario' : 'Nueva tarea'}
      </button>
      {showForm && (
        <TaskForm
          onSuccess={() => {
            setShowForm(false);
            loadTasks();
          }}
        />
      )}
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Título</th>
            <th>Descripción</th>
            <th>Estado</th>
            <th>Prioridad</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {tasks.map(task => (
            <tr key={task.id}>
              <td>{task.id}</td>
              <td>{task.title}</td>
              <td>{task.description}</td>
              <td>{task.status?.name}</td>
              <td>{task.priority?.name}</td>
              <td>
                <button className="btn btn-info btn-sm me-2" onClick={() => setSelectedTaskId(task.id)}>
                  Ver
                </button>
                <button className="btn btn-primary btn-sm me-2" onClick={() => setTaskToEdit(task)}>
                  Editar
                </button>
                <button className="btn btn-warning btn-sm me-2" onClick={() => handleArchive(task.id)}>
                  Archivar
                </button>
                <button className="btn btn-danger btn-sm" onClick={() => handleDelete(task.id)}>
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

export default TaskList;
