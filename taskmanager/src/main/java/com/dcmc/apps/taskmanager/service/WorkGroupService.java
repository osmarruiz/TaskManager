package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.User;
import com.dcmc.apps.taskmanager.domain.WorkGroup;
import com.dcmc.apps.taskmanager.domain.WorkGroupMembership;
import com.dcmc.apps.taskmanager.domain.enumeration.Role;
import com.dcmc.apps.taskmanager.repository.UserRepository;
import com.dcmc.apps.taskmanager.repository.WorkGroupMembershipRepository;
import com.dcmc.apps.taskmanager.repository.WorkGroupRepository;
import com.dcmc.apps.taskmanager.service.dto.CreateWorkGroupDTO;
import com.dcmc.apps.taskmanager.service.dto.WorkGroupDTO;
import com.dcmc.apps.taskmanager.service.mapper.WorkGroupMapper;
import java.util.Optional;

import com.dcmc.apps.taskmanager.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dcmc.apps.taskmanager.domain.WorkGroup}.
 */
@Service
@Transactional
public class WorkGroupService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkGroupService.class);

    private final WorkGroupRepository workGroupRepository;

    private final WorkGroupMapper workGroupMapper;

    private final UserRepository userRepository;
    private final WorkGroupMembershipRepository workGroupMembershipRepository;

    public WorkGroupService(WorkGroupRepository workGroupRepository, WorkGroupMapper workGroupMapper,
                            UserRepository userRepository,
                            WorkGroupMembershipRepository workGroupMembershipRepository)  {
        this.workGroupRepository = workGroupRepository;
        this.workGroupMapper = workGroupMapper;
        this.userRepository = userRepository;
        this.workGroupMembershipRepository = workGroupMembershipRepository;
    }

    /**
     * Save a workGroup.
     *
     * @param createWorkGroupDTO the entity to save.
     * @return the persisted entity.
     */
    public WorkGroupDTO save(CreateWorkGroupDTO createWorkGroupDTO) {
        LOG.debug("Request to save WorkGroup : {}", createWorkGroupDTO);

        WorkGroup workGroup = new WorkGroup();
        workGroup.setName(createWorkGroupDTO.getName());
        workGroup.setDescription(createWorkGroupDTO.getDescription());

        workGroup = workGroupRepository.save(workGroup);
        return workGroupMapper.toDto(workGroup);
    }

    /**
     * Transfiere la propiedad de un grupo a otro usuario.
     *
     * @param workGroupId el ID del grupo
     * @param newOwnerId el ID del nuevo propietario
     * @throws BadRequestAlertException si la transferencia no es válida
     */
    public void transferOwnership(Long workGroupId, String newOwnerId) {
        LOG.debug("Request to transfer ownership of work group {} to user {}", workGroupId, newOwnerId);

        // 1. Validar existencia del grupo
        WorkGroup workGroup = workGroupRepository.findById(workGroupId)
            .orElseThrow(() -> new BadRequestAlertException("WorkGroup not found", "workGroup", "idnotfound"));

        // 2. Validar existencia del nuevo propietario
        User newOwner = userRepository.findById(newOwnerId)
            .orElseThrow(() -> new BadRequestAlertException("User not found", "user", "idnotfound"));

        // 3. Obtener el propietario actual (usando el QueryService de JHipster)
        WorkGroupMembership currentOwner = workGroupMembershipRepository
            .findByWorkGroupAndRole(workGroup, Role.OWNER)
            .orElseThrow(() -> new BadRequestAlertException("Current owner not found", "workGroup", "noowner"));

        // 4. Verificar que el nuevo propietario sea miembro del grupo
        Optional<WorkGroupMembership> newOwnerMembershipOpt = workGroupMembershipRepository
            .findByWorkGroupAndUser(workGroup, newOwner);

        if (!newOwnerMembershipOpt.isPresent()) {
            throw new BadRequestAlertException("User is not a group member", "workGroup", "notmember");
        }

        // 5. Realizar la transferencia (transacción atómica)
        currentOwner.setRole(Role.MIEMBRO);
        workGroupMembershipRepository.save(currentOwner);

        WorkGroupMembership newOwnerMembership = newOwnerMembershipOpt.get();
        newOwnerMembership.setRole(Role.OWNER);
        workGroupMembershipRepository.save(newOwnerMembership);


        workGroupRepository.save(workGroup);

        LOG.info("Transferred ownership of work group {} from user {} to user {}",
            workGroupId, currentOwner.getUser().getId(), newOwnerId);
    }


    /**
     * Update a workGroup.
     *
     * @param workGroupDTO the entity to save.
     * @return the persisted entity.
     */
    public WorkGroupDTO update(WorkGroupDTO workGroupDTO) {
        LOG.debug("Request to update WorkGroup : {}", workGroupDTO);
        WorkGroup workGroup = workGroupMapper.toEntity(workGroupDTO);
        workGroup = workGroupRepository.save(workGroup);
        return workGroupMapper.toDto(workGroup);
    }

    /**
     * Partially update a workGroup.
     *
     * @param workGroupDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WorkGroupDTO> partialUpdate(WorkGroupDTO workGroupDTO) {
        LOG.debug("Request to partially update WorkGroup : {}", workGroupDTO);

        return workGroupRepository
            .findById(workGroupDTO.getId())
            .map(existingWorkGroup -> {
                workGroupMapper.partialUpdate(existingWorkGroup, workGroupDTO);

                return existingWorkGroup;
            })
            .map(workGroupRepository::save)
            .map(workGroupMapper::toDto);
    }

    /**
     * Get one workGroup by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WorkGroupDTO> findOne(Long id) {
        LOG.debug("Request to get WorkGroup : {}", id);
        return workGroupRepository.findById(id).map(workGroupMapper::toDto);
    }

    /**
     * Delete the workGroup by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete WorkGroup : {}", id);
        workGroupRepository.deleteById(id);
    }
}
