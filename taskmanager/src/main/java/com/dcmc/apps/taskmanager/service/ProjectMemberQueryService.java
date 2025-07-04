package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.*; // for static metamodels
import com.dcmc.apps.taskmanager.domain.ProjectMember;
import com.dcmc.apps.taskmanager.repository.ProjectMemberRepository;
import com.dcmc.apps.taskmanager.service.criteria.ProjectMemberCriteria;
import com.dcmc.apps.taskmanager.service.dto.ProjectMemberDTO;
import com.dcmc.apps.taskmanager.service.mapper.ProjectMemberMapper;
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
 * Service for executing complex queries for {@link ProjectMember} entities in the database.
 * The main input is a {@link ProjectMemberCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ProjectMemberDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProjectMemberQueryService extends QueryService<ProjectMember> {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectMemberQueryService.class);

    private final ProjectMemberRepository projectMemberRepository;

    private final ProjectMemberMapper projectMemberMapper;

    public ProjectMemberQueryService(ProjectMemberRepository projectMemberRepository, ProjectMemberMapper projectMemberMapper) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectMemberMapper = projectMemberMapper;
    }

    /**
     * Return a {@link Page} of {@link ProjectMemberDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProjectMemberDTO> findByCriteria(ProjectMemberCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProjectMember> specification = createSpecification(criteria);
        return projectMemberRepository.findAll(specification, page).map(projectMemberMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProjectMemberCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ProjectMember> specification = createSpecification(criteria);
        return projectMemberRepository.count(specification);
    }

    /**
     * Function to convert {@link ProjectMemberCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProjectMember> createSpecification(ProjectMemberCriteria criteria) {
        Specification<ProjectMember> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ProjectMember_.id),
                buildRangeSpecification(criteria.getAssignedAt(), ProjectMember_.assignedAt),
                buildSpecification(criteria.getProjectId(), root -> root.join(ProjectMember_.project, JoinType.LEFT).get(Project_.id)),
                buildSpecification(criteria.getUserId(), root -> root.join(ProjectMember_.user, JoinType.LEFT).get(User_.id))
            );
        }
        return specification;
    }
}
