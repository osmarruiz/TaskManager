package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.TaskAssignment;
import com.dcmc.apps.taskmanager.repository.TaskAssignmentRepository;
import com.dcmc.apps.taskmanager.service.dto.TaskAssignmentDTO;
import com.dcmc.apps.taskmanager.service.mapper.TaskAssignmentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dcmc.apps.taskmanager.domain.TaskAssignment}.
 */
@Service
@Transactional
public class TaskAssignmentService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskAssignmentService.class);

    private final TaskAssignmentRepository taskAssignmentRepository;

    private final TaskAssignmentMapper taskAssignmentMapper;

    public TaskAssignmentService(TaskAssignmentRepository taskAssignmentRepository, TaskAssignmentMapper taskAssignmentMapper) {
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.taskAssignmentMapper = taskAssignmentMapper;
    }

    /**
     * Save a taskAssignment.
     *
     * @param taskAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public TaskAssignmentDTO save(TaskAssignmentDTO taskAssignmentDTO) {
        LOG.debug("Request to save TaskAssignment : {}", taskAssignmentDTO);
        TaskAssignment taskAssignment = taskAssignmentMapper.toEntity(taskAssignmentDTO);
        taskAssignment = taskAssignmentRepository.save(taskAssignment);
        return taskAssignmentMapper.toDto(taskAssignment);
    }

    /**
     * Update a taskAssignment.
     *
     * @param taskAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public TaskAssignmentDTO update(TaskAssignmentDTO taskAssignmentDTO) {
        LOG.debug("Request to update TaskAssignment : {}", taskAssignmentDTO);
        TaskAssignment taskAssignment = taskAssignmentMapper.toEntity(taskAssignmentDTO);
        taskAssignment = taskAssignmentRepository.save(taskAssignment);
        return taskAssignmentMapper.toDto(taskAssignment);
    }

    /**
     * Partially update a taskAssignment.
     *
     * @param taskAssignmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TaskAssignmentDTO> partialUpdate(TaskAssignmentDTO taskAssignmentDTO) {
        LOG.debug("Request to partially update TaskAssignment : {}", taskAssignmentDTO);

        return taskAssignmentRepository
            .findById(taskAssignmentDTO.getId())
            .map(existingTaskAssignment -> {
                taskAssignmentMapper.partialUpdate(existingTaskAssignment, taskAssignmentDTO);

                return existingTaskAssignment;
            })
            .map(taskAssignmentRepository::save)
            .map(taskAssignmentMapper::toDto);
    }

    /**
     * Get one taskAssignment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TaskAssignmentDTO> findOne(Long id) {
        LOG.debug("Request to get TaskAssignment : {}", id);
        return taskAssignmentRepository.findById(id).map(taskAssignmentMapper::toDto);
    }

    /**
     * Delete the taskAssignment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TaskAssignment : {}", id);
        taskAssignmentRepository.deleteById(id);
    }
}
