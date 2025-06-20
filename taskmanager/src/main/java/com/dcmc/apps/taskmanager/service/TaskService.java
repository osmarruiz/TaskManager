package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.*;
import com.dcmc.apps.taskmanager.domain.enumeration.Role;
import com.dcmc.apps.taskmanager.repository.*;
import com.dcmc.apps.taskmanager.security.SecurityUtils;
import com.dcmc.apps.taskmanager.service.dto.CreateTaskDTO;
import com.dcmc.apps.taskmanager.service.dto.TaskDTO;
import com.dcmc.apps.taskmanager.service.mapper.TaskMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dcmc.apps.taskmanager.domain.Task}.
 */
@Service
@Transactional
public class TaskService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final ProjectRepository projectRepository;

    private final UserRepository userRepository;

    private final WorkGroupMembershipRepository workGroupMembershipRepository;

    private final PriorityRepository priorityRepository;

    private final TaskStatusCatalogRepository statusRepository;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper, ProjectRepository projectRepository,
                          UserRepository userRepository,
                          WorkGroupMembershipRepository workGroupMembershipRepository,
                          PriorityRepository priorityRepository,
                          TaskStatusCatalogRepository statusRepository)  {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.workGroupMembershipRepository = workGroupMembershipRepository;
        this.priorityRepository = priorityRepository;
        this.statusRepository = statusRepository;
    }

    /**
     * Save a task.
     *
     * @param taskDTO the entity to save.
     * @return the persisted entity.
     */
    public TaskDTO save(TaskDTO taskDTO) {
        LOG.debug("Request to save Task : {}", taskDTO);
        Task task = taskMapper.toEntity(taskDTO);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    /**
     * Update a task.
     *
     * @param taskDTO the entity to save.
     * @return the persisted entity.
     */
    public TaskDTO update(TaskDTO taskDTO) {
        LOG.debug("Request to update Task : {}", taskDTO);
        Task task = taskMapper.toEntity(taskDTO);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    /**
     * Partially update a task.
     *
     * @param taskDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TaskDTO> partialUpdate(TaskDTO taskDTO) {
        LOG.debug("Request to partially update Task : {}", taskDTO);

        return taskRepository
            .findById(taskDTO.getId())
            .map(existingTask -> {
                taskMapper.partialUpdate(existingTask, taskDTO);

                return existingTask;
            })
            .map(taskRepository::save)
            .map(taskMapper::toDto);
    }

    /**
     * Get one task by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TaskDTO> findOne(Long id) {
        LOG.debug("Request to get Task : {}", id);
        return taskRepository.findById(id).map(taskMapper::toDto);
    }

    /**
     * Delete the task by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Task : {}", id);
        taskRepository.deleteById(id);
    }

    @Transactional
    public TaskDTO createTaskForProject(Long projectId, CreateTaskDTO taskDTO) {
        // 1. Validar proyecto
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + projectId));

        // 2. Validar permisos del usuario actual (sin SecurityService)
        String userLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        User user = userRepository.findOneByLogin(userLogin)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        WorkGroupMembership membership = workGroupMembershipRepository
            .findByWorkGroupIdAndUserLogin(project.getWorkGroup().getId(),user.getLogin())
            .orElseThrow(() -> new RuntimeException("No eres miembro del grupo"));

        if (!Set.of(Role.OWNER, Role.MODERADOR).contains(membership.getRole())) {
            throw new RuntimeException("Solo OWNER/MODERADOR pueden crear tareas");
        }

        // 3. Crear entidad
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDeadline(taskDTO.getDeadline());
        task.setUpdateTime(Instant.now());
        task.setWorkGroup(project.getWorkGroup());
        task.setParentProject(project);

        // 4. Establecer prioridad (por defecto o especificada)
        Priority priority = taskDTO.getPriorityId() != null ?
            priorityRepository.findById(taskDTO.getPriorityId())
                .orElseThrow(() -> new RuntimeException("Prioridad no encontrada")) :
            priorityRepository.findByName("NORMAL")
                .orElseThrow(() -> new IllegalStateException("Prioridad por defecto no configurada"));

        task.setPriority(priority);

        // 5. Establecer estado (por defecto o especificado)
        TaskStatusCatalog status = taskDTO.getStatusId() != null ?
            statusRepository.findById(taskDTO.getStatusId())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado")) :
            statusRepository.findByName("NOT_STARTED")
                .orElseThrow(() -> new IllegalStateException("Estado por defecto no configurado"));

        task.setStatus(status);

        // 6. Guardar
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    @Transactional
    public void deleteTaskFromProject(Long projectId, Long taskId) {
        // 1. Verificar que el proyecto existe
        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException("Project not found with id: " + projectId);
        }

        // 2. Obtener y validar la tarea
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        // 3. Verificar pertenencia al proyecto
        if (task.getParentProject() == null || !task.getParentProject().getId().equals(projectId)) {
            throw new RuntimeException("Task does not belong to the specified project");
        }

        // 4. Validar reglas de negocio
        if (task.getArchived() != null && task.getArchived()) {
            throw new RuntimeException("Cannot delete archived tasks");
        }

        // 5. Eliminar
        taskRepository.delete(task);
    }
    @Transactional(readOnly = true)
    public List<TaskDTO> findAllTasksByProjectId(Long projectId) {
        // Verificar que el proyecto existe
        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException("Project not found with id: " + projectId);
        }

        return taskRepository.findByParentProjectId(projectId)
            .stream()
            .map(taskMapper::toDto)
            .collect(Collectors.toList());
    }
}
