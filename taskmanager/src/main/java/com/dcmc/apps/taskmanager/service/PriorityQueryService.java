package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.*; // for static metamodels
import com.dcmc.apps.taskmanager.domain.Priority;
import com.dcmc.apps.taskmanager.repository.PriorityRepository;
import com.dcmc.apps.taskmanager.service.criteria.PriorityCriteria;
import com.dcmc.apps.taskmanager.service.dto.PriorityDTO;
import com.dcmc.apps.taskmanager.service.mapper.PriorityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Priority} entities in the database.
 * The main input is a {@link PriorityCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PriorityDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PriorityQueryService extends QueryService<Priority> {

    private static final Logger LOG = LoggerFactory.getLogger(PriorityQueryService.class);

    private final PriorityRepository priorityRepository;

    private final PriorityMapper priorityMapper;

    public PriorityQueryService(PriorityRepository priorityRepository, PriorityMapper priorityMapper) {
        this.priorityRepository = priorityRepository;
        this.priorityMapper = priorityMapper;
    }

    /**
     * Return a {@link Page} of {@link PriorityDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PriorityDTO> findByCriteria(PriorityCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Priority> specification = createSpecification(criteria);
        return priorityRepository.findAll(specification, page).map(priorityMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PriorityCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Priority> specification = createSpecification(criteria);
        return priorityRepository.count(specification);
    }

    /**
     * Function to convert {@link PriorityCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Priority> createSpecification(PriorityCriteria criteria) {
        Specification<Priority> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Priority_.id),
                buildStringSpecification(criteria.getName(), Priority_.name),
                buildStringSpecification(criteria.getDescription(), Priority_.description),
                buildSpecification(criteria.getVisible(), Priority_.visible),
                buildRangeSpecification(criteria.getCreatedAt(), Priority_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Priority_.updatedAt)
            );
        }
        return specification;
    }
}
