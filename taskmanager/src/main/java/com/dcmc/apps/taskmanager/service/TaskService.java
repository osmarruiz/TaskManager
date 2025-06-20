package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.*;
import com.dcmc.apps.taskmanager.domain.enumeration.Role;
import com.dcmc.apps.taskmanager.repository.*;
import com.dcmc.apps.taskmanager.security.SecurityUtils;
import com.dcmc.apps.taskmanager.service.dto.CreateTaskDTO;
import com.dcmc.apps.taskmanager.service.dto.TaskDTO;
import com.dcmc.apps.taskmanager.service.mapper.TaskMapper;
import com.dcmc.apps.taskmanager.web.rest.errors.BadRequestAlertException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

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

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper,
                       ProjectRepository projectRepository, UserRepository userRepository,
                       WorkGroupMembershipRepository workGroupMembershipRepository,
                       PriorityRepository priorityRepository,
                       TaskStatusCatalogRepository statusRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.workGroupMembershipRepository = workGroupMembershipRepository;
        this.priorityRepository = priorityRepository;
        this.statusRepository = statusRepository;
    }

    // Métodos auxiliares
    private boolean isCurrentUserAdmin() {
        return SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN");
    }

    private void validateAdminOrProjectOwnerOrModerator(Long projectId) {
        if (isCurrentUserAdmin()) {
            return;
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("User not authenticated"));

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new BadRequestAlertException("Project not found", ENTITY_NAME, "idnotfound"));

        WorkGroupMembership membership = workGroupMembershipRepository
            .findByWorkGroupIdAndUserLogin(project.getWorkGroup().getId(), currentUserLogin)
            .orElseThrow(() -> new AccessDeniedException("User is not member of work group"));

        if (!Set.of(Role.OWNER, Role.MODERADOR).contains(membership.getRole())) {
            throw new AccessDeniedException("Insufficient privileges");
        }
    }

    private User getCurrentUser() {
        return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AccessDeniedException("User not authenticated")))
            .orElseThrow(() -> new BadRequestAlertException("User not found", "user", "usernotfound"));
    }

    // Métodos principales
    public TaskDTO save(TaskDTO taskDTO) {
        LOG.debug("Request to save Task : {}", taskDTO);
        Task task = taskMapper.toEntity(taskDTO);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    public TaskDTO update(TaskDTO taskDTO) {
        LOG.debug("Request to update Task : {}", taskDTO);
        Task task = taskMapper.toEntity(taskDTO);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

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

    @Transactional(readOnly = true)
    public Optional<TaskDTO> findOne(Long id) {
        LOG.debug("Request to get Task : {}", id);
        return taskRepository.findById(id).map(taskMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete Task : {}", id);
        taskRepository.deleteById(id);
    }

    @Transactional
    public TaskDTO createTaskForProject(Long projectId, CreateTaskDTO taskDTO) {
        LOG.debug("Request to create task for project {}: {}", projectId, taskDTO);

        // Validar proyecto
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new BadRequestAlertException("Project not found", ENTITY_NAME, "idnotfound"));

        // Validar permisos (ADMIN, OWNER o MODERADOR)
        validateAdminOrProjectOwnerOrModerator(projectId);

        // Crear entidad
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDeadline(taskDTO.getDeadline());
        task.setUpdateTime(Instant.now());
        task.setWorkGroup(project.getWorkGroup());
        task.setParentProject(project);

        // Establecer prioridad
        Priority priority =
            priorityRepository.findByName("NORMAL")
                .orElseThrow(() -> new IllegalStateException("Default priority not configured"));

        task.setPriority(priority);

        // Establecer estado
        TaskStatusCatalog status =
            statusRepository.findByName("NOT_STARTED")
                .orElseThrow(() -> new IllegalStateException("Default status not configured"));

        task.setStatus(status);

        // Guardar
        Task savedTask = taskRepository.save(task);
        LOG.info("Task created with ID: {}", savedTask.getId());
        return taskMapper.toDto(savedTask);
    }

    @Transactional
    public void deleteTaskFromProject(Long projectId, Long taskId) {
        LOG.debug("Request to delete task {} from project {}", taskId, projectId);

        // Verificar que el proyecto existe
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new BadRequestAlertException("Project not found", ENTITY_NAME, "idnotfound"));

        // Validar permisos (ADMIN, OWNER o MODERADOR)
        validateAdminOrProjectOwnerOrModerator(projectId);

        // Obtener y validar la tarea
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new BadRequestAlertException("Task not found", "task", "idnotfound"));

        // Verificar pertenencia al proyecto
        if (task.getParentProject() == null || !task.getParentProject().getId().equals(projectId)) {
            throw new BadRequestAlertException("Task does not belong to project", ENTITY_NAME, "invalid.task");
        }

        // Validar reglas de negocio
        if (task.getArchived() != null && task.getArchived()) {
            throw new BadRequestAlertException("Cannot delete archived tasks", ENTITY_NAME, "archived.task");
        }

        // Eliminar
        taskRepository.delete(task);
        LOG.info("Task {} deleted from project {}", taskId, projectId);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> findAllTasksByProjectId(Long projectId) {
        LOG.debug("Request to get all tasks for project {}", projectId);

        // Verificar que el proyecto existe
        if (!projectRepository.existsById(projectId)) {
            throw new BadRequestAlertException("Project not found", ENTITY_NAME, "idnotfound");
        }

        return taskRepository.findByParentProjectId(projectId)
            .stream()
            .map(taskMapper::toDto)
            .collect(Collectors.toList());
    }

//    @Transactional
//    public void assignTask(Long taskId, String userLogin) {
//        LOG.debug("Request to assign task {} to user {}", taskId, userLogin);
//
//        // Validar que la tarea existe
//        Task task = taskRepository.findById(taskId)
//            .orElseThrow(() -> new BadRequestAlertException("Task not found", "task", "idnotfound"));
//
//        // Validar permisos (ADMIN, OWNER o MODERADOR del grupo de trabajo)
//        validateAdminOrProjectOwnerOrModerator(task.getParentProject().getId());
//
//        // Validar que el usuario existe
//        User user = userRepository.findOneByLogin(userLogin)
//            .orElseThrow(() -> new BadRequestAlertException("User not found", "user", "usernotfound"));
//
//        // Validar que el usuario es miembro del grupo de trabajo
//        if (!workGroupMembershipRepository.existsByWorkGroupAndUser(task.getWorkGroup(), user)) {
//            throw new BadRequestAlertException("User is not member of work group", ENTITY_NAME, "not.member");
//        }
//
//        // Asignar tarea
//        task.setAssignedTo(user);
//        task.setUpdateTime(Instant.now());
//        taskRepository.save(task);
//        LOG.info("Task {} assigned to user {}", taskId, userLogin);
//    }
}
