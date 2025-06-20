package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.TaskStatusCatalog;
import com.dcmc.apps.taskmanager.domain.User;
import com.dcmc.apps.taskmanager.domain.enumeration.Role;
import com.dcmc.apps.taskmanager.repository.TaskStatusCatalogRepository;
import com.dcmc.apps.taskmanager.repository.UserRepository;
import com.dcmc.apps.taskmanager.repository.WorkGroupMembershipRepository;
import com.dcmc.apps.taskmanager.security.SecurityUtils;
import com.dcmc.apps.taskmanager.service.dto.CreateTaskStatusCatalogDTO;
import com.dcmc.apps.taskmanager.service.dto.TaskStatusCatalogDTO;
import com.dcmc.apps.taskmanager.service.mapper.TaskStatusCatalogMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dcmc.apps.taskmanager.domain.TaskStatusCatalog}.
 */
@Service
@Transactional
public class TaskStatusCatalogService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskStatusCatalogService.class);

    private final TaskStatusCatalogRepository taskStatusCatalogRepository;

    private final TaskStatusCatalogMapper taskStatusCatalogMapper;

    private final WorkGroupMembershipRepository workGroupMembershipRepository;

    private final UserRepository userRepository;

    public TaskStatusCatalogService(
        TaskStatusCatalogRepository taskStatusCatalogRepository,
        TaskStatusCatalogMapper taskStatusCatalogMapper,
        WorkGroupMembershipRepository workGroupMembershipRepository,
        UserRepository userRepository
    ) {
        this.taskStatusCatalogRepository = taskStatusCatalogRepository;
        this.taskStatusCatalogMapper = taskStatusCatalogMapper;
        this.workGroupMembershipRepository = workGroupMembershipRepository;
        this.userRepository = userRepository;
    }

    private boolean isCurrentUserAdmin() {
        return SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN");
    }
    private void validateAdminOrGroupOwnerOrModerator() {
        if (isCurrentUserAdmin()) {
            return;
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow();
        if (!workGroupMembershipRepository.existsByUserLoginAndRoleIn(
            currentUserLogin, List.of(Role.OWNER, Role.MODERADOR))) {
            throw new AccessDeniedException("Insufficient privileges");
        }
    }

    /**
     * Save a new taskStatusCatalog.
     *
     * @param createTaskStatusCatalogDTO the DTO with data to create the entity.
     * @return the persisted entity as DTO.
     */
    public TaskStatusCatalogDTO save(CreateTaskStatusCatalogDTO createTaskStatusCatalogDTO) {
        LOG.debug("Request to save TaskStatusCatalog : {}", createTaskStatusCatalogDTO);
        validateAdminOrGroupOwnerOrModerator();

        // Obtener el usuario actual desde la base de datos
        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new IllegalStateException("Current user login not found"));

        User creator = userRepository.findOneByLogin(currentUserLogin)
            .orElseThrow(() -> new RuntimeException("User not found with login: " + currentUserLogin));

        // Crear la entidad y establecer los campos
        TaskStatusCatalog taskStatusCatalog = new TaskStatusCatalog();
        taskStatusCatalog.setName(createTaskStatusCatalogDTO.getName());
        taskStatusCatalog.setDescription(createTaskStatusCatalogDTO.getDescription());
        taskStatusCatalog.setCreatedAt(Instant.now());
        taskStatusCatalog.setUpdatedAt(Instant.now());
        taskStatusCatalog.setCreatedBy(creator);

        taskStatusCatalog = taskStatusCatalogRepository.save(taskStatusCatalog);
        return taskStatusCatalogMapper.toDto(taskStatusCatalog);
    }

    /**
     * Update an existing taskStatusCatalog.
     *
     * @param id the id of the entity to update.
     * @param createTaskStatusCatalogDTO the DTO with updated data.
     * @return the updated entity as DTO.
     */
    public TaskStatusCatalogDTO update(Long id, CreateTaskStatusCatalogDTO createTaskStatusCatalogDTO) {
        LOG.debug("Request to update TaskStatusCatalog with id {}: {}", id, createTaskStatusCatalogDTO);
        validateAdminOrGroupOwnerOrModerator();

        return taskStatusCatalogRepository.findById(id)
            .map(existingTaskStatus -> {
                // Actualizar solo los campos permitidos
                existingTaskStatus.setName(createTaskStatusCatalogDTO.getName());
                existingTaskStatus.setDescription(createTaskStatusCatalogDTO.getDescription());
                existingTaskStatus.setUpdatedAt(Instant.now());

                return existingTaskStatus;
            })
            .map(taskStatusCatalogRepository::save)
            .map(taskStatusCatalogMapper::toDto)
            .orElseThrow(() -> new RuntimeException("TaskStatusCatalog not found with id " + id));
    }

    /**
     * Partially update a taskStatusCatalog.
     *
     * @param taskStatusCatalogDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TaskStatusCatalogDTO> partialUpdate(TaskStatusCatalogDTO taskStatusCatalogDTO) {
        LOG.debug("Request to partially update TaskStatusCatalog : {}", taskStatusCatalogDTO);
        validateAdminOrGroupOwnerOrModerator();

        return taskStatusCatalogRepository
            .findById(taskStatusCatalogDTO.getId())
            .map(existingTaskStatusCatalog -> {
                taskStatusCatalogMapper.partialUpdate(existingTaskStatusCatalog, taskStatusCatalogDTO);
                return existingTaskStatusCatalog;
            })
            .map(taskStatusCatalogRepository::save)
            .map(taskStatusCatalogMapper::toDto);
    }

    /**
     * Get one taskStatusCatalog by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TaskStatusCatalogDTO> findOne(Long id) {
        LOG.debug("Request to get TaskStatusCatalog : {}", id);

            validateAdminOrGroupOwnerOrModerator();

        return taskStatusCatalogRepository.findById(id).map(taskStatusCatalogMapper::toDto);
    }

    /**
     * Delete the taskStatusCatalog by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TaskStatusCatalog : {}", id);
        validateAdminOrGroupOwnerOrModerator();
        taskStatusCatalogRepository.deleteById(id);
    }
}
