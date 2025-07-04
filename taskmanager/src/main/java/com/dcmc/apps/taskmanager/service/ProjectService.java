package com.dcmc.apps.taskmanager.service;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

import com.dcmc.apps.taskmanager.domain.*;
import com.dcmc.apps.taskmanager.domain.enumeration.Role;
import com.dcmc.apps.taskmanager.repository.*;
import com.dcmc.apps.taskmanager.security.SecurityUtils;
import com.dcmc.apps.taskmanager.service.dto.AssignProjectToUserDTO;
import com.dcmc.apps.taskmanager.service.dto.CreateProjectDTO;
import com.dcmc.apps.taskmanager.service.dto.ProjectDTO;
import com.dcmc.apps.taskmanager.service.dto.ProjectMemberDTO;
import com.dcmc.apps.taskmanager.service.mapper.ProjectMapper;
import com.dcmc.apps.taskmanager.service.mapper.ProjectMemberMapper;
import com.dcmc.apps.taskmanager.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dcmc.apps.taskmanager.domain.Project}.
 */
@Service
@Transactional
public class ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    private final WorkGroupRepository workGroupRepository;

    private final ProjectMemberRepository projectMemberRepository;

    private final ProjectMemberMapper projectMemberMapper;

    private final UserRepository userRepository;

    private final WorkGroupMembershipRepository workGroupMembershipRepository;

    public ProjectService(
        ProjectRepository projectRepository,
        ProjectMapper projectMapper,
        WorkGroupRepository workGroupRepository,
        ProjectMemberRepository projectMemberRepository,
        ProjectMemberMapper projectMemberMapper,
        UserRepository userRepository,
        WorkGroupMembershipRepository workGroupMembershipRepository
    ) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.workGroupRepository = workGroupRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectMemberMapper = projectMemberMapper;
        this.userRepository = userRepository;
        this.workGroupMembershipRepository = workGroupMembershipRepository;
    }

    private boolean isCurrentUserAdmin() {
        return SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN");
    }

    private void validateAdminOrProjectOwnerOrModerator(Long projectId) {
        if (isCurrentUserAdmin()) {
            return;
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("User not authenticated"));

        Project project = projectRepository
            .findById(projectId)
            .orElseThrow(() -> new BadRequestAlertException("Project not found", ENTITY_NAME, "idnotfound"));

        WorkGroupMembership membership = workGroupMembershipRepository
            .findByWorkGroupIdAndUserLogin(project.getWorkGroup().getId(), currentUserLogin)
            .orElseThrow(() -> new AccessDeniedException("User is not member of work group"));

        if (!Set.of(Role.OWNER, Role.MODERADOR).contains(membership.getRole())) {
            throw new AccessDeniedException("Insufficient privileges");
        }
    }

    /**
     * Save a project.
     *
     * @param projectDTO the entity to save.
     * @return the persisted entity.
     */
    public ProjectDTO save(CreateProjectDTO projectDTO) {
        Project project = new Project();
        project.setTitle(projectDTO.getTitle());
        project.setDescription(projectDTO.getDescription());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());

        WorkGroup workGroup = workGroupRepository
            .findById(projectDTO.getWorkGroupId())
            .orElseThrow(() -> new RuntimeException("WorkGroup not found"));
        project.setWorkGroup(workGroup);

        Project savedProject = projectRepository.save(project);
        return projectMapper.toDto(savedProject);
    }

    /**
     * Update a project.
     *
     * @param projectDTO the entity to save.
     * @return the persisted entity.
     */
    public ProjectDTO update(ProjectDTO projectDTO) {
        LOG.debug("Request to update Project : {}", projectDTO);
        Project project = projectMapper.toEntity(projectDTO);
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    /**
     * Partially update a project.
     *
     * @param projectDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProjectDTO> partialUpdate(ProjectDTO projectDTO) {
        LOG.debug("Request to partially update Project : {}", projectDTO);

        return projectRepository
            .findById(projectDTO.getId())
            .map(existingProject -> {
                projectMapper.partialUpdate(existingProject, projectDTO);

                return existingProject;
            })
            .map(projectRepository::save)
            .map(projectMapper::toDto);
    }

    /**
     * Get one project by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProjectDTO> findOne(Long id) {
        LOG.debug("Request to get Project : {}", id);
        return projectRepository.findById(id).map(projectMapper::toDto);
    }

    /**
     * Delete the project by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Project : {}", id);
        projectRepository.deleteById(id);
    }

    // Additional methods for project management can be added here

    @Transactional(readOnly = true)
    public List<ProjectDTO> findAllByWorkGroupId(Long workGroupId) {
        // Verificar si el workGroup existe
        if (!workGroupRepository.existsById(workGroupId)) {
            throw new RuntimeException("WorkGroup no encontrado con id: " + workGroupId);
        }

        return projectRepository.findByWorkGroupId(workGroupId).stream().map(projectMapper::toDto).collect(Collectors.toList());
    }

    public ProjectMemberDTO assignUserToProject(Long id, AssignProjectToUserDTO assignDTO) {
        // 1. Validate user has permission (admin, owner or moderator)
        validateAdminOrProjectOwnerOrModerator(id);

        // 2. Validate that the project exists
        Project project = projectRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Project not found", ENTITY_NAME, "idnotfound"));

        // 3. Validate that the user exists
        User user = userRepository
            .findOneByLogin(assignDTO.getUserLogin())
            .orElseThrow(() -> new BadRequestAlertException("User not found", "user", "loginnotfound"));

        // 4. Validate that the user is not already assigned
        if (projectMemberRepository.existsByProjectAndUser(project, user)) {
            throw new BadRequestAlertException("User already assigned to project", ENTITY_NAME, "userexists");
        }

        // 5. Validate that the user is a member of the work group
        if (!workGroupMembershipRepository.existsByWorkGroupIdAndUserLogin(project.getWorkGroup().getId(), assignDTO.getUserLogin())) {
            throw new BadRequestAlertException("User is not member of work group", ENTITY_NAME, "notworkgroupmember");
        }

        // 6. Create the assignment
        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setAssignedAt(Instant.now());

        ProjectMember savedMember = projectMemberRepository.save(member);
        return projectMemberMapper.toDto(savedMember);
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberDTO> getProjectMembers(Long projectId) {
        LOG.debug("Request to get members for Project : {}", projectId);

        // Validate that the project exists
        if (!projectRepository.existsById(projectId)) {
            throw new BadRequestAlertException("Project not found", ENTITY_NAME, "idnotfound");
        }

        return projectMemberRepository.findByProjectId(projectId).stream().map(projectMemberMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsForCurrentUser() {
        LOG.debug("Request to get projects for current user");

        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("User not authenticated"));

        User currentUser = userRepository
            .findOneByLogin(currentUserLogin)
            .orElseThrow(() -> new BadRequestAlertException("User not found", "user", "loginnotfound"));

        return projectMemberRepository
            .findByUser(currentUser)
            .stream()
            .map(ProjectMember::getProject)
            .map(projectMapper::toDto)
            .collect(Collectors.toList());
    }
}
