package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.*; // for static metamodels
import com.dcmc.apps.taskmanager.domain.WorkGroupMembership;
import com.dcmc.apps.taskmanager.repository.WorkGroupMembershipRepository;
import com.dcmc.apps.taskmanager.service.criteria.WorkGroupMembershipCriteria;
import com.dcmc.apps.taskmanager.service.dto.WorkGroupMembershipDTO;
import com.dcmc.apps.taskmanager.service.mapper.WorkGroupMembershipMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link WorkGroupMembership} entities in the database.
 * The main input is a {@link WorkGroupMembershipCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link WorkGroupMembershipDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WorkGroupMembershipQueryService extends QueryService<WorkGroupMembership> {

    private static final Logger LOG = LoggerFactory.getLogger(WorkGroupMembershipQueryService.class);

    private final WorkGroupMembershipRepository workGroupMembershipRepository;

    private final WorkGroupMembershipMapper workGroupMembershipMapper;

    public WorkGroupMembershipQueryService(
        WorkGroupMembershipRepository workGroupMembershipRepository,
        WorkGroupMembershipMapper workGroupMembershipMapper
    ) {
        this.workGroupMembershipRepository = workGroupMembershipRepository;
        this.workGroupMembershipMapper = workGroupMembershipMapper;
    }

    /**
     * Return a {@link List} of {@link WorkGroupMembershipDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<WorkGroupMembershipDTO> findByCriteria(WorkGroupMembershipCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<WorkGroupMembership> specification = createSpecification(criteria);
        return workGroupMembershipMapper.toDto(workGroupMembershipRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WorkGroupMembershipCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<WorkGroupMembership> specification = createSpecification(criteria);
        return workGroupMembershipRepository.count(specification);
    }

    /**
     * Function to convert {@link WorkGroupMembershipCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<WorkGroupMembership> createSpecification(WorkGroupMembershipCriteria criteria) {
        Specification<WorkGroupMembership> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), WorkGroupMembership_.id),
                buildSpecification(criteria.getRole(), WorkGroupMembership_.role),
                buildRangeSpecification(criteria.getJoinDate(), WorkGroupMembership_.joinDate),
                buildSpecification(criteria.getUserId(), root -> root.join(WorkGroupMembership_.user, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getWorkGroupId(), root ->
                    root.join(WorkGroupMembership_.workGroup, JoinType.LEFT).get(WorkGroup_.id)
                )
            );
        }
        return specification;
    }
}
