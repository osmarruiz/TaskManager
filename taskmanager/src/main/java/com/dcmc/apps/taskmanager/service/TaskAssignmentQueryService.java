package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.*; // for static metamodels
import com.dcmc.apps.taskmanager.domain.TaskAssignment;
import com.dcmc.apps.taskmanager.repository.TaskAssignmentRepository;
import com.dcmc.apps.taskmanager.service.criteria.TaskAssignmentCriteria;
import com.dcmc.apps.taskmanager.service.dto.TaskAssignmentDTO;
import com.dcmc.apps.taskmanager.service.mapper.TaskAssignmentMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TaskAssignment} entities in the database.
 * The main input is a {@link TaskAssignmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TaskAssignmentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TaskAssignmentQueryService extends QueryService<TaskAssignment> {

    private static final Logger LOG = LoggerFactory.getLogger(TaskAssignmentQueryService.class);

    private final TaskAssignmentRepository taskAssignmentRepository;

    private final TaskAssignmentMapper taskAssignmentMapper;

    public TaskAssignmentQueryService(TaskAssignmentRepository taskAssignmentRepository, TaskAssignmentMapper taskAssignmentMapper) {
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.taskAssignmentMapper = taskAssignmentMapper;
    }

    /**
     * Return a {@link Page} of {@link TaskAssignmentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TaskAssignmentDTO> findByCriteria(TaskAssignmentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TaskAssignment> specification = createSpecification(criteria);
        return taskAssignmentRepository.findAll(specification, page).map(taskAssignmentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TaskAssignmentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TaskAssignment> specification = createSpecification(criteria);
        return taskAssignmentRepository.count(specification);
    }

    /**
     * Function to convert {@link TaskAssignmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TaskAssignment> createSpecification(TaskAssignmentCriteria criteria) {
        Specification<TaskAssignment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), TaskAssignment_.id),
                buildRangeSpecification(criteria.getAssignedAt(), TaskAssignment_.assignedAt),
                buildSpecification(criteria.getTaskId(), root -> root.join(TaskAssignment_.task, JoinType.LEFT).get(Task_.id)),
                buildSpecification(criteria.getUserId(), root -> root.join(TaskAssignment_.user, JoinType.LEFT).get(User_.id))
            );
        }
        return specification;
    }
}
