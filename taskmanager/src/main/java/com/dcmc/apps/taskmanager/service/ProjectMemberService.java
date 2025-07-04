package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.ProjectMember;
import com.dcmc.apps.taskmanager.repository.ProjectMemberRepository;
import com.dcmc.apps.taskmanager.service.dto.ProjectMemberDTO;
import com.dcmc.apps.taskmanager.service.mapper.ProjectMemberMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dcmc.apps.taskmanager.domain.ProjectMember}.
 */
@Service
@Transactional
public class ProjectMemberService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectMemberService.class);

    private final ProjectMemberRepository projectMemberRepository;

    private final ProjectMemberMapper projectMemberMapper;

    public ProjectMemberService(ProjectMemberRepository projectMemberRepository, ProjectMemberMapper projectMemberMapper) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectMemberMapper = projectMemberMapper;
    }

    /**
     * Save a projectMember.
     *
     * @param projectMemberDTO the entity to save.
     * @return the persisted entity.
     */
    public ProjectMemberDTO save(ProjectMemberDTO projectMemberDTO) {
        LOG.debug("Request to save ProjectMember : {}", projectMemberDTO);
        ProjectMember projectMember = projectMemberMapper.toEntity(projectMemberDTO);
        projectMember = projectMemberRepository.save(projectMember);
        return projectMemberMapper.toDto(projectMember);
    }

    /**
     * Update a projectMember.
     *
     * @param projectMemberDTO the entity to save.
     * @return the persisted entity.
     */
    public ProjectMemberDTO update(ProjectMemberDTO projectMemberDTO) {
        LOG.debug("Request to update ProjectMember : {}", projectMemberDTO);
        ProjectMember projectMember = projectMemberMapper.toEntity(projectMemberDTO);
        projectMember = projectMemberRepository.save(projectMember);
        return projectMemberMapper.toDto(projectMember);
    }

    /**
     * Partially update a projectMember.
     *
     * @param projectMemberDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProjectMemberDTO> partialUpdate(ProjectMemberDTO projectMemberDTO) {
        LOG.debug("Request to partially update ProjectMember : {}", projectMemberDTO);

        return projectMemberRepository
            .findById(projectMemberDTO.getId())
            .map(existingProjectMember -> {
                projectMemberMapper.partialUpdate(existingProjectMember, projectMemberDTO);

                return existingProjectMember;
            })
            .map(projectMemberRepository::save)
            .map(projectMemberMapper::toDto);
    }

    /**
     * Get one projectMember by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProjectMemberDTO> findOne(Long id) {
        LOG.debug("Request to get ProjectMember : {}", id);
        return projectMemberRepository.findById(id).map(projectMemberMapper::toDto);
    }

    /**
     * Delete the projectMember by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ProjectMember : {}", id);
        projectMemberRepository.deleteById(id);
    }
}
