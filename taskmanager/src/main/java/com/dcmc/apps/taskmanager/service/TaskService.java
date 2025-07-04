package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.*;
import com.dcmc.apps.taskmanager.domain.enumeration.Role;
import com.dcmc.apps.taskmanager.repository.*;
import com.dcmc.apps.taskmanager.security.SecurityUtils;
import com.dcmc.apps.taskmanager.service.dto.CommentDTO;
import com.dcmc.apps.taskmanager.service.dto.CreateTaskDTO;
import com.dcmc.apps.taskmanager.service.dto.TaskAssignmentDTO;
import com.dcmc.apps.taskmanager.service.dto.TaskDTO;
import com.dcmc.apps.taskmanager.service.mapper.CommentMapper;
import com.dcmc.apps.taskmanager.service.mapper.TaskAssignmentMapper;
import com.dcmc.apps.taskmanager.service.mapper.TaskMapper;
import com.dcmc.apps.taskmanager.web.rest.errors.BadRequestAlertException;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
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
    private final TaskAssignmentRepository taskAssignmentRepository;

    private final TaskAssignmentMapper taskAssignmentMapper;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper,
                       ProjectRepository projectRepository, UserRepository userRepository,
                       WorkGroupMembershipRepository workGroupMembershipRepository,
                       PriorityRepository priorityRepository,
                       TaskStatusCatalogRepository statusRepository,
                       TaskAssignmentRepository taskAssignmentRepository,
                       TaskAssignmentMapper taskAssignmentMapper,
                       CommentRepository commentRepository, CommentMapper commentMapper) {

        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.workGroupMembershipRepository = workGroupMembershipRepository;
        this.priorityRepository = priorityRepository;
        this.statusRepository = statusRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.taskAssignmentMapper = taskAssignmentMapper;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    // ========== MÉTODOS AUXILIARES ==========

    private boolean isCurrentUserAdmin() {
        return SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN");
    }

    private String getCurrentUserLogin() {
        return SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("User not authenticated"));
    }

    private void validateAdminOrProjectOwnerOrModerator(Long projectId) {
        if (isCurrentUserAdmin()) {
            return;
        }

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new BadRequestAlertException("Project not found", ENTITY_NAME, "idnotfound"));

        validateAdminOrGroupOwnerOrModerator(project.getWorkGroup().getId());
    }

    private void validateAdminOrGroupOwnerOrModerator(Long workGroupId) {
        if (isCurrentUserAdmin()) {
            return;
        }

        String currentUserLogin = getCurrentUserLogin();
        if (!workGroupMembershipRepository.existsByWorkGroupIdAndUserLoginAndRoleIn(
            workGroupId, currentUserLogin, List.of(Role.OWNER, Role.MODERADOR))) {
            throw new AccessDeniedException("Insufficient privileges");
        }
    }

    private void validateTaskNotArchived(Task task) {
        if (task.getArchived()) {
            throw new IllegalStateException("Cannot modify an archived task");
        }
    }

    private Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
    }

    private User getUserByLogin(String userLogin) {
        return userRepository.findOneByLogin(userLogin)
            .orElseThrow(() -> new RuntimeException("User not found with login: " + userLogin));
    }

    // ========== MÉTODOS CRUD BÁSICOS ==========

    public TaskDTO save(TaskDTO taskDTO) {
        LOG.debug("Request to save Task : {}", taskDTO);
        Task task = taskMapper.toEntity(taskDTO);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    public TaskDTO update(Long id, CreateTaskDTO taskDTO) {
        LOG.debug("Request to update Task with id {}: {}", id, taskDTO);

        // Verificar si la tarea existe
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        if(task.getArchived())
            throw new IllegalStateException("Cannot update an archived task");

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDeadline(taskDTO.getDeadline());
        task.setUpdateTime(Instant.now());


        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
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

    // ========== MÉTODOS DE PROYECTO ==========

    @Transactional
    public TaskDTO createTaskForProject(Long projectId, CreateTaskDTO taskDTO) {
        LOG.debug("Request to create task for project {}: {}", projectId, taskDTO);

        // Validar proyecto y permisos
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new BadRequestAlertException("Project not found", ENTITY_NAME, "idnotfound"));
        validateAdminOrProjectOwnerOrModerator(projectId);

        // Crear entidad
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDeadline(taskDTO.getDeadline());
        task.setArchived(false);
        task.setUpdateTime(Instant.now());
        task.setWorkGroup(project.getWorkGroup());
        task.setParentProject(project);

        // Establecer prioridad y estado por defecto
        task.setPriority(priorityRepository.findByName("NORMAL")
            .orElseThrow(() -> new IllegalStateException("Default priority not configured")));
        task.setStatus(statusRepository.findByName("NOT_STARTED")
            .orElseThrow(() -> new IllegalStateException("Default status not configured")));

        // Guardar
        Task savedTask = taskRepository.save(task);
        LOG.info("Task created with ID: {}", savedTask.getId());
        return taskMapper.toDto(savedTask);
    }

    @Transactional
    public void deleteTaskFromProject(Long projectId, Long taskId) {
        LOG.debug("Request to delete task {} from project {}", taskId, projectId);

        // Validar proyecto y permisos
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new BadRequestAlertException("Project not found", ENTITY_NAME, "idnotfound"));
        validateAdminOrProjectOwnerOrModerator(projectId);

        // Obtener y validar la tarea
        Task task = getTaskById(taskId);
        if (task.getParentProject() == null || !task.getParentProject().getId().equals(projectId)) {
            throw new BadRequestAlertException("Task does not belong to project", ENTITY_NAME, "invalid.task");
        }
        if (task.getArchived()) {
            throw new BadRequestAlertException("Cannot delete archived tasks", ENTITY_NAME, "archived.task");
        }

        // Eliminar
        taskRepository.delete(task);
        LOG.info("Task {} deleted from project {}", taskId, projectId);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> findAllTasksByProjectId(Long projectId) {
        LOG.debug("Request to get all tasks for project {}", projectId);

        if (!projectRepository.existsById(projectId)) {
            throw new BadRequestAlertException("Project not found", ENTITY_NAME, "idnotfound");
        }

        return taskRepository.findByParentProjectId(projectId)
            .stream()
            .map(taskMapper::toDto)
            .collect(Collectors.toList());
    }

    // ========== MÉTODOS DE ASIGNACIÓN ==========

    public void assignTask(Long taskId, String userLogin) {
        LOG.debug("Request to assign user {} to task {}", userLogin, taskId);

        Task task = getTaskById(taskId);
        validateTaskNotArchived(task);
        validateAdminOrGroupOwnerOrModerator(task.getWorkGroup().getId());

        User user = getUserByLogin(userLogin);

        // Verificar que el usuario pertenece al grupo de trabajo
        if (!workGroupMembershipRepository.existsByWorkGroupAndUser(task.getWorkGroup(), user)) {
            throw new IllegalStateException("User does not belong to the task's work group");
        }

        // Verificar que no está ya asignado
        if (taskAssignmentRepository.existsByTaskAndUser(task, user)) {
            throw new IllegalStateException("User is already assigned to this task");
        }

        TaskAssignment assignment = new TaskAssignment();
        assignment.setTask(task);
        assignment.setUser(user);
        assignment.setAssignedAt(Instant.now());
        taskAssignmentRepository.save(assignment);
    }

    public void unassignTask(Long taskId, String userLogin) {
        LOG.debug("Request to unassign user {} from task {}", userLogin, taskId);

        Task task = getTaskById(taskId);
        validateTaskNotArchived(task);
        validateAdminOrGroupOwnerOrModerator(task.getWorkGroup().getId());

        User user = getUserByLogin(userLogin);
        TaskAssignment assignment = taskAssignmentRepository.findByTaskAndUser(task, user)
            .orElseThrow(() -> new RuntimeException("Assignment not found" ));

        taskAssignmentRepository.delete(assignment);
    }

    public List<TaskAssignmentDTO> getTaskAssignments(Long taskId) {
        LOG.debug("Request to get assignments for task {}", taskId);

        Task task = getTaskById(taskId);

        return taskAssignmentRepository.findByTask(task).stream()
            .map(taskAssignmentMapper::toDto)
            .collect(Collectors.toList());
    }

    public CommentDTO addCommentToTask(Long taskId, String content) {
        LOG.debug("Request to add comment to task {}: {}", taskId, content);

        Task task = getTaskById(taskId);
        validateTaskNotArchived(task);

        // Verificar que el usuario actual es miembro del grupo de trabajo
        String currentUserLogin = getCurrentUserLogin();

        User author = getUserByLogin(currentUserLogin);

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateTime(Instant.now());
        comment.setTask(task);
        comment.setAuthor(author);

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    public List<CommentDTO> getTaskComments(Long taskId) {
        LOG.debug("Request to get comments for task {}", taskId);

        Task task = getTaskById(taskId);


        return commentRepository.findByTaskOrderByCreateTimeDesc(task).stream()
            .map(commentMapper::toDto)
            .collect(Collectors.toList());
    }



    // ========== MÉTODOS DE ESTADO Y PRIORIDAD ==========

    public void changePriority(Long taskId, String priorityName) {
        LOG.debug("Request to change priority of task {} to {}", taskId, priorityName);

        Task task = getTaskById(taskId);
        validateTaskNotArchived(task);
        validateAdminOrGroupOwnerOrModerator(task.getWorkGroup().getId());

        Priority priority = priorityRepository.findByName(priorityName)
            .orElseThrow(() -> new RuntimeException("Priority not found: " + priorityName));

        task.setPriority(priority);
        task.setUpdateTime(Instant.now());
        taskRepository.save(task);
    }

    public void changeStatus(Long taskId, String statusName) {
        LOG.debug("Request to change status of task {} to {}", taskId, statusName);

        Task task = getTaskById(taskId);
        validateTaskNotArchived(task);
        validateAdminOrGroupOwnerOrModerator(task.getWorkGroup().getId());

        TaskStatusCatalog status = statusRepository.findByName(statusName)
            .orElseThrow(() -> new RuntimeException("Status not found: " + statusName));

        task.setStatus(status);
        task.setUpdateTime(Instant.now());
        taskRepository.save(task);
    }

    // ========== MÉTODOS DE ARCHIVADO ==========

    public void archiveTask(Long taskId) {
        LOG.debug("Request to archive task {}", taskId);

        Task task = getTaskById(taskId);
        validateAdminOrGroupOwnerOrModerator(task.getWorkGroup().getId());

        if (!"DONE".equals(task.getStatus().getName())) {
            throw new IllegalStateException("Only DONE tasks can be archived");
        }

        task.setArchived(true);
        task.setArchivedDate(ZonedDateTime.now());
        taskRepository.save(task);
    }

    public void unarchiveTask(Long taskId) {
        LOG.debug("Request to unarchive task {}", taskId);

        Task task = taskRepository.findByIdAndArchivedTrue(taskId)
            .orElseThrow(() -> new RuntimeException("Archived task not found with id: " + taskId));
        validateAdminOrGroupOwnerOrModerator(task.getWorkGroup().getId());

        task.setArchived(false);
        task.setArchivedDate(null);
        taskRepository.save(task);
    }

    public void deleteArchivedTask(Long taskId) {
        LOG.debug("Request to delete archived task {}", taskId);

        Task task = taskRepository.findByIdAndArchivedTrue(taskId)
            .orElseThrow(() -> new RuntimeException("Archived task not found with id: " + taskId));

        // Validar permisos usando la misma lógica que deleteTaskFromProject
        validateAdminOrGroupOwnerOrModerator(task.getWorkGroup().getId());

        taskRepository.delete(task);
    }
}
