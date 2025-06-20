package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.Project;
import com.dcmc.apps.taskmanager.domain.ProjectMember;
import com.dcmc.apps.taskmanager.domain.User;
import com.dcmc.apps.taskmanager.domain.WorkGroup;
import com.dcmc.apps.taskmanager.repository.ProjectMemberRepository;
import com.dcmc.apps.taskmanager.repository.ProjectRepository;
import com.dcmc.apps.taskmanager.repository.UserRepository;
import com.dcmc.apps.taskmanager.repository.WorkGroupRepository;
import com.dcmc.apps.taskmanager.service.dto.AssignProjectToUserDTO;
import com.dcmc.apps.taskmanager.service.dto.CreateProjectDTO;
import com.dcmc.apps.taskmanager.service.dto.ProjectDTO;
import com.dcmc.apps.taskmanager.service.dto.ProjectMemberDTO;
import com.dcmc.apps.taskmanager.service.mapper.ProjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.dcmc.apps.taskmanager.service.mapper.ProjectMemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.OpenApiResourceNotFoundException;
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

    public ProjectService(ProjectRepository projectRepository, ProjectMapper projectMapper,
                          WorkGroupRepository workGroupRepository,
                          ProjectMemberRepository projectMemberRepository,
                          ProjectMemberMapper projectMemberMapper,
                          UserRepository userRepository) {

        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.workGroupRepository = workGroupRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectMemberMapper = projectMemberMapper;
        this.userRepository = userRepository;
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

        WorkGroup workGroup = workGroupRepository.findById(projectDTO.getWorkGroupId())
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

        return projectRepository.findByWorkGroupId(workGroupId)
            .stream()
            .map(projectMapper::toDto)
            .collect(Collectors.toList());
    }

    public ProjectMemberDTO assignUserToProject(AssignProjectToUserDTO assignDTO) {
        // 1. Validar que el proyecto existe
        Project project = projectRepository.findById(assignDTO.getProjectId())
            .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // 2. Validar que el usuario existe
        User user = userRepository.findOneByLogin(assignDTO.getUserLogin())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Validar que el usuario no esté ya asignado
        if (projectMemberRepository.existsByProjectAndUser(project, user)) {
            throw new RuntimeException("El usuario ya está asignado a este proyecto");
        }

        // 4. Crear la asignación
        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setAssignedAt(Instant.now());

        ProjectMember savedMember = projectMemberRepository.save(member);
        return projectMemberMapper.toDto(savedMember);
    }


}
