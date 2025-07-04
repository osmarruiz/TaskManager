package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.*; // for static metamodels
import com.dcmc.apps.taskmanager.domain.TaskStatusCatalog;
import com.dcmc.apps.taskmanager.repository.TaskStatusCatalogRepository;
import com.dcmc.apps.taskmanager.service.criteria.TaskStatusCatalogCriteria;
import com.dcmc.apps.taskmanager.service.dto.TaskStatusCatalogDTO;
import com.dcmc.apps.taskmanager.service.mapper.TaskStatusCatalogMapper;
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
 * Service for executing complex queries for {@link TaskStatusCatalog} entities in the database.
 * The main input is a {@link TaskStatusCatalogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TaskStatusCatalogDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TaskStatusCatalogQueryService extends QueryService<TaskStatusCatalog> {

    private static final Logger LOG = LoggerFactory.getLogger(TaskStatusCatalogQueryService.class);

    private final TaskStatusCatalogRepository taskStatusCatalogRepository;

    private final TaskStatusCatalogMapper taskStatusCatalogMapper;

    public TaskStatusCatalogQueryService(
        TaskStatusCatalogRepository taskStatusCatalogRepository,
        TaskStatusCatalogMapper taskStatusCatalogMapper
    ) {
        this.taskStatusCatalogRepository = taskStatusCatalogRepository;
        this.taskStatusCatalogMapper = taskStatusCatalogMapper;
    }

    /**
     * Return a {@link Page} of {@link TaskStatusCatalogDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TaskStatusCatalogDTO> findByCriteria(TaskStatusCatalogCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TaskStatusCatalog> specification = createSpecification(criteria);
        return taskStatusCatalogRepository.findAll(specification, page).map(taskStatusCatalogMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TaskStatusCatalogCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TaskStatusCatalog> specification = createSpecification(criteria);
        return taskStatusCatalogRepository.count(specification);
    }

    /**
     * Function to convert {@link TaskStatusCatalogCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TaskStatusCatalog> createSpecification(TaskStatusCatalogCriteria criteria) {
        Specification<TaskStatusCatalog> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), TaskStatusCatalog_.id),
                buildStringSpecification(criteria.getName(), TaskStatusCatalog_.name),
                buildStringSpecification(criteria.getDescription(), TaskStatusCatalog_.description),
                buildRangeSpecification(criteria.getCreatedAt(), TaskStatusCatalog_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), TaskStatusCatalog_.updatedAt),
                buildSpecification(criteria.getCreatedById(), root -> root.join(TaskStatusCatalog_.createdBy, JoinType.LEFT).get(User_.id))
            );
        }
        return specification;
    }
}
