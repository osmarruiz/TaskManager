package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.User;
import com.dcmc.apps.taskmanager.domain.WorkGroup;
import com.dcmc.apps.taskmanager.domain.WorkGroupMembership;
import com.dcmc.apps.taskmanager.domain.enumeration.Role;
import com.dcmc.apps.taskmanager.repository.UserRepository;
import com.dcmc.apps.taskmanager.repository.WorkGroupMembershipRepository;
import com.dcmc.apps.taskmanager.repository.WorkGroupRepository;
import com.dcmc.apps.taskmanager.security.SecurityUtils;
import com.dcmc.apps.taskmanager.service.dto.CreateWorkGroupDTO;
import com.dcmc.apps.taskmanager.service.dto.MemberWithRoleDTO;
import com.dcmc.apps.taskmanager.service.dto.UserWorkGroupDTO;
import com.dcmc.apps.taskmanager.service.dto.WorkGroupDTO;
import com.dcmc.apps.taskmanager.service.mapper.WorkGroupMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.dcmc.apps.taskmanager.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

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

    //    ****************************************************************************************************************

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

    @Transactional
    public void addModerator(Long workGroupId, String userId) {
        LOG.debug("Adding user {} as moderator to work group {}", userId, workGroupId);

        WorkGroup workGroup = validateWorkGroup(workGroupId);
        User user = validateUser(userId);

        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("User not logged in"));

        if (!workGroupMembershipRepository.existsByWorkGroupIdAndUserLoginAndRole(workGroupId, currentUserLogin, Role.OWNER) &&
            !workGroupMembershipRepository.existsByWorkGroupIdAndUserLoginAndRole(workGroupId, currentUserLogin, Role.MODERADOR)) {
            throw new AccessDeniedException("Only the group owner or moderator can add moderators");
        }


        WorkGroupMembership membership = workGroupMembershipRepository
            .findByWorkGroupAndUser(workGroup, user)
            .orElseThrow(() -> new BadRequestAlertException("User is not a group member", ENTITY_NAME, "notmember"));

        if (membership.getRole() == Role.MODERADOR) {
            throw new BadRequestAlertException("User is already a moderator", ENTITY_NAME, "already.moderator");
        }

        if (membership.getRole() == Role.OWNER) {
            throw new BadRequestAlertException("Cannot modify owner role", ENTITY_NAME, "is.owner");
        }

        membership.setRole(Role.MODERADOR);
        workGroupMembershipRepository.save(membership);
        updateWorkGroupAudit(workGroup);
    }

    @Transactional
    public void removeModerator(Long workGroupId, String userId) {
        LOG.debug("Removing moderator {} from work group {}", userId, workGroupId);

        WorkGroup workGroup = validateWorkGroup(workGroupId);
        User user = validateUser(userId);

        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("User not logged in"));

        if (!workGroupMembershipRepository.existsByWorkGroupIdAndUserLoginAndRole(
            workGroupId, currentUserLogin, Role.OWNER)) {
            throw new AccessDeniedException("Only the group owner can add moderators");
        }

        WorkGroupMembership membership = workGroupMembershipRepository
            .findByWorkGroupAndUser(workGroup, user)
            .orElseThrow(() -> new BadRequestAlertException("User is not a group member", ENTITY_NAME, "notmember"));

        if (membership.getRole() != Role.MODERADOR) {
            throw new BadRequestAlertException("User is not a moderator", ENTITY_NAME, "not.moderator");
        }

        membership.setRole(Role.MIEMBRO);
        workGroupMembershipRepository.save(membership);
        updateWorkGroupAudit(workGroup);
    }

    @Transactional
    public void addMember(Long workGroupId, String userLogin) {
        LOG.debug("Adding user {} to work group {}", userLogin, workGroupId);

        // Validaciones
        WorkGroup workGroup = validateWorkGroup(workGroupId);
        User user = validateUser(userLogin);
        validateCurrentUserPrivileges(workGroupId);

        // Verificar si ya es miembro
        if (workGroupMembershipRepository.existsByWorkGroupAndUser(workGroup, user)) {
            throw new BadRequestAlertException("User is already a member", ENTITY_NAME, "already.member");
        }

        // Crear nueva membresía
        WorkGroupMembership membership = new WorkGroupMembership()
            .workGroup(workGroup)
            .user(user)
            .role(Role.MIEMBRO)
            .joinDate(Instant.now());

        workGroupMembershipRepository.save(membership);
        updateWorkGroupAudit(workGroup);
    }

    @Transactional
    public void removeMember(Long workGroupId, String userLogin) {
        LOG.debug("Removing user {} from work group {}", userLogin, workGroupId);

        // Validaciones
        WorkGroup workGroup = validateWorkGroup(workGroupId);
        User user = validateUser(userLogin);
        validateCurrentUserPrivileges(workGroupId);

        // Obtener membresía
        WorkGroupMembership membership = workGroupMembershipRepository
            .findByWorkGroupAndUser(workGroup, user)
            .orElseThrow(() -> new BadRequestAlertException("User is not a member", ENTITY_NAME, "not.member"));

        // Validar que no sea OWNER
        if (membership.getRole() == Role.OWNER) {
            throw new BadRequestAlertException("Cannot remove owner", ENTITY_NAME, "cannot.remove.owner");
        }

        // Validar que no se esté eliminando a sí mismo
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow();
        if (user.getLogin().equals(currentUserLogin)) {
            throw new BadRequestAlertException("Cannot remove yourself", ENTITY_NAME, "cannot.remove.self");
        }

        // Eliminar membresía
        workGroupMembershipRepository.delete(membership);
        updateWorkGroupAudit(workGroup);
    }

    @Transactional(readOnly = true)
    public List<MemberWithRoleDTO> getAllMembersWithRoles(Long workGroupId) {
        return workGroupMembershipRepository.findByWorkGroupId(workGroupId)
            .stream()
            .map(membership -> new MemberWithRoleDTO(
                membership.getUser().getLogin(),
                membership.getUser().getFirstName() + " " + membership.getUser().getLastName(),
                membership.getRole(),
                membership.getJoinDate()))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserWorkGroupDTO> getUserWorkGroups(String userLogin) {
        return workGroupMembershipRepository.findByUserLogin(userLogin)
            .stream()
            .map(membership -> new UserWorkGroupDTO(
                membership.getWorkGroup().getId(),
                membership.getWorkGroup().getName(),
                membership.getRole()))
            .collect(Collectors.toList());
    }

    @Transactional
    public void leaveWorkGroup(Long workGroupId, String userLogin) {
        // Validar que el grupo existe
        WorkGroup workGroup = workGroupRepository.findById(workGroupId)
            .orElseThrow(() -> new BadRequestAlertException("WorkGroup not found", ENTITY_NAME, "idnotfound"));

        // Obtener la membresía
        WorkGroupMembership membership = workGroupMembershipRepository
            .findByWorkGroupAndUserLogin(workGroup, userLogin)
            .orElseThrow(() -> new BadRequestAlertException("User is not a member", ENTITY_NAME, "not.member"));

        // Validar que no es el owner
        if (membership.getRole() == Role.OWNER) {
            throw new BadRequestAlertException("Owner cannot leave the group", ENTITY_NAME, "owner.cannot.leave");
        }

        // Eliminar membresía
        workGroupMembershipRepository.delete(membership);

        workGroupRepository.save(workGroup);
    }

    // Métodos auxiliares
    private void validateCurrentUserPrivileges(Long workGroupId) {
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow();

        if (!workGroupMembershipRepository.existsByWorkGroupIdAndUserLoginAndRoleIn(
            workGroupId,
            currentUserLogin,
            List.of(Role.OWNER, Role.MODERADOR))) {
            throw new AccessDeniedException("Insufficient privileges");
        }
    }

    private User validateUser(String login) {
        return userRepository.findOneByLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("User not found", "user", "usernotfound"));
    }

    private WorkGroup validateWorkGroup(Long id) {
        return workGroupRepository.findById(id)
            .orElseThrow(() -> new BadRequestAlertException("WorkGroup not found", ENTITY_NAME, "idnotfound"));
    }

    private void updateWorkGroupAudit(WorkGroup workGroup) {
        workGroupRepository.save(workGroup);
    }
}
