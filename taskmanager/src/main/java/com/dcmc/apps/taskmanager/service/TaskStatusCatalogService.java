package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.TaskStatusCatalog;
import com.dcmc.apps.taskmanager.repository.TaskStatusCatalogRepository;
import com.dcmc.apps.taskmanager.service.dto.TaskStatusCatalogDTO;
import com.dcmc.apps.taskmanager.service.mapper.TaskStatusCatalogMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public TaskStatusCatalogService(
        TaskStatusCatalogRepository taskStatusCatalogRepository,
        TaskStatusCatalogMapper taskStatusCatalogMapper
    ) {
        this.taskStatusCatalogRepository = taskStatusCatalogRepository;
        this.taskStatusCatalogMapper = taskStatusCatalogMapper;
    }

    /**
     * Save a taskStatusCatalog.
     *
     * @param taskStatusCatalogDTO the entity to save.
     * @return the persisted entity.
     */
    public TaskStatusCatalogDTO save(TaskStatusCatalogDTO taskStatusCatalogDTO) {
        LOG.debug("Request to save TaskStatusCatalog : {}", taskStatusCatalogDTO);
        TaskStatusCatalog taskStatusCatalog = taskStatusCatalogMapper.toEntity(taskStatusCatalogDTO);
        taskStatusCatalog = taskStatusCatalogRepository.save(taskStatusCatalog);
        return taskStatusCatalogMapper.toDto(taskStatusCatalog);
    }

    /**
     * Update a taskStatusCatalog.
     *
     * @param taskStatusCatalogDTO the entity to save.
     * @return the persisted entity.
     */
    public TaskStatusCatalogDTO update(TaskStatusCatalogDTO taskStatusCatalogDTO) {
        LOG.debug("Request to update TaskStatusCatalog : {}", taskStatusCatalogDTO);
        TaskStatusCatalog taskStatusCatalog = taskStatusCatalogMapper.toEntity(taskStatusCatalogDTO);
        taskStatusCatalog = taskStatusCatalogRepository.save(taskStatusCatalog);
        return taskStatusCatalogMapper.toDto(taskStatusCatalog);
    }

    /**
     * Partially update a taskStatusCatalog.
     *
     * @param taskStatusCatalogDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TaskStatusCatalogDTO> partialUpdate(TaskStatusCatalogDTO taskStatusCatalogDTO) {
        LOG.debug("Request to partially update TaskStatusCatalog : {}", taskStatusCatalogDTO);

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
        return taskStatusCatalogRepository.findById(id).map(taskStatusCatalogMapper::toDto);
    }

    /**
     * Delete the taskStatusCatalog by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TaskStatusCatalog : {}", id);
        taskStatusCatalogRepository.deleteById(id);
    }
}
