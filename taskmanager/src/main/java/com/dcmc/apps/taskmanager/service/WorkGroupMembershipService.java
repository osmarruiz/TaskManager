package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.WorkGroupMembership;
import com.dcmc.apps.taskmanager.repository.WorkGroupMembershipRepository;
import com.dcmc.apps.taskmanager.service.dto.WorkGroupMembershipDTO;
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

    public WorkGroupMembershipService(
        WorkGroupMembershipRepository workGroupMembershipRepository,
        WorkGroupMembershipMapper workGroupMembershipMapper
    ) {
        this.workGroupMembershipRepository = workGroupMembershipRepository;
        this.workGroupMembershipMapper = workGroupMembershipMapper;
    }

    /**
     * Save a workGroupMembership.
     *
     * @param workGroupMembershipDTO the entity to save.
     * @return the persisted entity.
     */
    public WorkGroupMembershipDTO save(WorkGroupMembershipDTO workGroupMembershipDTO) {
        LOG.debug("Request to save WorkGroupMembership : {}", workGroupMembershipDTO);
        WorkGroupMembership workGroupMembership = workGroupMembershipMapper.toEntity(workGroupMembershipDTO);
        workGroupMembership = workGroupMembershipRepository.save(workGroupMembership);
        return workGroupMembershipMapper.toDto(workGroupMembership);
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
