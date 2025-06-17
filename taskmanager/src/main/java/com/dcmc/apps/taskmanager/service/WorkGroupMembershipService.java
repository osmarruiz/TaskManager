package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.User;
import com.dcmc.apps.taskmanager.domain.WorkGroup;
import com.dcmc.apps.taskmanager.domain.WorkGroupMembership;
import com.dcmc.apps.taskmanager.repository.UserRepository;
import com.dcmc.apps.taskmanager.repository.WorkGroupMembershipRepository;
import com.dcmc.apps.taskmanager.repository.WorkGroupRepository;
import com.dcmc.apps.taskmanager.security.SecurityUtils;
import com.dcmc.apps.taskmanager.service.dto.CreateWorkGroupMembershipDTO;
import com.dcmc.apps.taskmanager.service.dto.WorkGroupMembershipDTO;
import com.dcmc.apps.taskmanager.service.mapper.WorkGroupMapper;
import com.dcmc.apps.taskmanager.service.mapper.WorkGroupMembershipMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dcmc.apps.taskmanager.domain.WorkGroupMembership}.
 */
@Service
@Transactional
public class WorkGroupMembershipService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkGroupMembershipService.class);

    private final WorkGroupMembershipRepository workGroupMembershipRepository;

    private final WorkGroupMembershipMapper workGroupMembershipMapper;

    private final WorkGroupRepository workGroupRepository;

    private final WorkGroupMapper workGroupMapper;
    private final UserRepository userRepository;

    public WorkGroupMembershipService(
        WorkGroupMembershipRepository workGroupMembershipRepository,
        WorkGroupMembershipMapper workGroupMembershipMapper,
        WorkGroupRepository workGroupRepository,
        WorkGroupMapper workGroupMapper,
        UserRepository userRepository
    ) {
        this.workGroupMembershipRepository = workGroupMembershipRepository;
        this.workGroupMembershipMapper = workGroupMembershipMapper;
        this.workGroupRepository = workGroupRepository;
        this.workGroupMapper = workGroupMapper;
        this.userRepository = userRepository;
    }

    /**
     * Save a workGroupMembership.
     *
     * @param createDTO the entity to save.
     * @return the persisted entity.
     */


    public WorkGroupMembershipDTO save(CreateWorkGroupMembershipDTO createDTO) {
        LOG.debug("Request to create WorkGroupMembership: {}", createDTO);

        // 1. Obtener el usuario actual
        User currentUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado")))
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        // 2. Obtener WorkGroup existente
        WorkGroup workGroup = workGroupRepository.findById(createDTO.getWorkGroupId())
            .orElseThrow(() -> new RuntimeException("WorkGroup no encontrado con id: " + createDTO.getWorkGroupId()));

        WorkGroupMembership membership = new WorkGroupMembership();
        membership.setRole(createDTO.getRole());
        membership.setUser(currentUser);
        membership.setWorkGroup(workGroup);

        membership = workGroupMembershipRepository.save(membership);
        return workGroupMembershipMapper.toDto(membership);
    }

    /**
     * Update a workGroupMembership.
     *
     * @param workGroupMembershipDTO the entity to save.
     * @return the persisted entity.
     */
    public WorkGroupMembershipDTO update(WorkGroupMembershipDTO workGroupMembershipDTO) {
        LOG.debug("Request to update WorkGroupMembership : {}", workGroupMembershipDTO);
        WorkGroupMembership workGroupMembership = workGroupMembershipMapper.toEntity(workGroupMembershipDTO);
        workGroupMembership = workGroupMembershipRepository.save(workGroupMembership);
        return workGroupMembershipMapper.toDto(workGroupMembership);
    }

    /**
     * Partially update a workGroupMembership.
     *
     * @param workGroupMembershipDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WorkGroupMembershipDTO> partialUpdate(WorkGroupMembershipDTO workGroupMembershipDTO) {
        LOG.debug("Request to partially update WorkGroupMembership : {}", workGroupMembershipDTO);

        return workGroupMembershipRepository
            .findById(workGroupMembershipDTO.getId())
            .map(existingWorkGroupMembership -> {
                workGroupMembershipMapper.partialUpdate(existingWorkGroupMembership, workGroupMembershipDTO);

                return existingWorkGroupMembership;
            })
            .map(workGroupMembershipRepository::save)
            .map(workGroupMembershipMapper::toDto);
    }

    /**
     * Get one workGroupMembership by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WorkGroupMembershipDTO> findOne(Long id) {
        LOG.debug("Request to get WorkGroupMembership : {}", id);
        return workGroupMembershipRepository.findById(id).map(workGroupMembershipMapper::toDto);
    }

    /**
     * Delete the workGroupMembership by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete WorkGroupMembership : {}", id);
        workGroupMembershipRepository.deleteById(id);
    }
}
