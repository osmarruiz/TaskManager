package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.User;
import com.dcmc.apps.taskmanager.domain.WorkGroup;
import com.dcmc.apps.taskmanager.domain.WorkGroupMembership;
import com.dcmc.apps.taskmanager.domain.enumeration.Role;
import com.dcmc.apps.taskmanager.repository.UserRepository;
import com.dcmc.apps.taskmanager.repository.WorkGroupMembershipRepository;
import com.dcmc.apps.taskmanager.repository.WorkGroupRepository;
import com.dcmc.apps.taskmanager.security.SecurityUtils;
import com.dcmc.apps.taskmanager.service.dto.*;
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
                            WorkGroupMembershipRepository workGroupMembershipRepository) {
        this.workGroupRepository = workGroupRepository;
        this.workGroupMapper = workGroupMapper;
        this.userRepository = userRepository;
        this.workGroupMembershipRepository = workGroupMembershipRepository;
    }

    // Métodos auxiliares de validación
    private boolean isCurrentUserAdmin() {
        return SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN");
    }

    private void validateAdminOrGroupOwner(Long workGroupId) {
        if (isCurrentUserAdmin()) {
            return;
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow();
        if (!workGroupMembershipRepository.existsByWorkGroupIdAndUserLoginAndRole(
            workGroupId, currentUserLogin, Role.OWNER)) {
            throw new AccessDeniedException("Only admin or group owner can perform this action");
        }
    }

    private void validateAdminOrGroupOwnerOrModerator(Long workGroupId) {
        if (isCurrentUserAdmin()) {
            return;
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow();
        if (!workGroupMembershipRepository.existsByWorkGroupIdAndUserLoginAndRoleIn(
            workGroupId, currentUserLogin, List.of(Role.OWNER, Role.MODERADOR))) {
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

    // Métodos principales del servicio

    public WorkGroupDTO save(CreateWorkGroupDTO createWorkGroupDTO) {
        LOG.debug("Request to save WorkGroup : {}", createWorkGroupDTO);

        User currentUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new IllegalStateException("User not authenticated")))
            .orElseThrow(() -> new RuntimeException("User not found"));

        WorkGroup workGroup = new WorkGroup()
            .name(createWorkGroupDTO.getName())
            .description(createWorkGroupDTO.getDescription());

        workGroup = workGroupRepository.save(workGroup);

        // Crear la membresía del propietario
        WorkGroupMembership membership = new WorkGroupMembership()
            .workGroup(workGroup)
            .user(currentUser)
            .role(Role.OWNER)
            .joinDate(Instant.now());

        workGroupMembershipRepository.save(membership);
        LOG.info("WorkGroup created with ID: {}", workGroup.getId());

        return workGroupMapper.toDto(workGroup);
    }

    public WorkGroupDTO update(WorkGroupDTO workGroupDTO) {
        LOG.debug("Request to update WorkGroup : {}", workGroupDTO);

        // Validar que el usuario tiene permisos (admin o owner del grupo)
        validateAdminOrGroupOwner(workGroupDTO.getId());

        WorkGroup workGroup = workGroupMapper.toEntity(workGroupDTO);
        workGroup = workGroupRepository.save(workGroup);
        return workGroupMapper.toDto(workGroup);
    }

    public Optional<WorkGroupDTO> partialUpdate(WorkGroupDTO workGroupDTO) {
        LOG.debug("Request to partially update WorkGroup : {}", workGroupDTO);

        // Validar permisos antes de realizar la actualización
        validateAdminOrGroupOwner(workGroupDTO.getId());

        return workGroupRepository
            .findById(workGroupDTO.getId())
            .map(existingWorkGroup -> {
                workGroupMapper.partialUpdate(existingWorkGroup, workGroupDTO);
                return existingWorkGroup;
            })
            .map(workGroupRepository::save)
            .map(workGroupMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<WorkGroupDTO> findOne(Long id) {
        LOG.debug("Request to get WorkGroup : {}", id);
        return workGroupRepository.findById(id).map(workGroupMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete WorkGroup : {}", id);

        // Solo admin o owner pueden eliminar el grupo
        validateAdminOrGroupOwner(id);

        workGroupRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<UserWorkGroupDTO> getCurrentUserWorkGroups() {
        return workGroupMembershipRepository.findByUserIsCurrentUser()
            .stream()
            .map(membership -> new UserWorkGroupDTO(
                membership.getWorkGroup().getId(),
                membership.getWorkGroup().getName(),
                membership.getRole()))
            .collect(Collectors.toList());
    }

    @Transactional
    public void transferOwnership(Long workGroupId, String newOwnerLogin) {
        LOG.debug("Request to transfer ownership of work group {} to user {}", workGroupId, newOwnerLogin);

        // Validar que el usuario actual es ADMIN o OWNER del grupo
        if (!isCurrentUserAdmin()) {
            String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow();
            if (!workGroupMembershipRepository.existsByWorkGroupIdAndUserLoginAndRole(
                workGroupId, currentUserLogin, Role.OWNER)) {
                throw new AccessDeniedException("Only admin or group owner can transfer ownership");
            }
        }

        WorkGroup workGroup = validateWorkGroup(workGroupId);
        User newOwner = validateUser(newOwnerLogin);

        // Obtener el OWNER actual
        WorkGroupMembership currentOwner = workGroupMembershipRepository
            .findByWorkGroupAndRole(workGroup, Role.OWNER)
            .orElseThrow(() -> new BadRequestAlertException("Current owner not found", "workGroup", "no owner"));

        // Verificar si el nuevo OWNER ya es miembro
        Optional<WorkGroupMembership> newOwnerMembershipOpt = workGroupMembershipRepository
            .findByWorkGroupAndUser(workGroup, newOwner);

        WorkGroupMembership newOwnerMembership;

        if (newOwnerMembershipOpt.isPresent()) {
            newOwnerMembership = newOwnerMembershipOpt.get();
            // Validar que no sea el mismo usuario
            if (newOwnerMembership.getUser().getId().equals(currentOwner.getUser().getId())) {
                throw new BadRequestAlertException("User is already the owner", "workGroup", "already.owner");
            }
        } else {
            // Si no es miembro, crear nueva membresía
            newOwnerMembership = new WorkGroupMembership()
                .workGroup(workGroup)
                .user(newOwner)
                .joinDate(Instant.now());
        }

        // 1. Convertir al OWNER actual en MIEMBRO
        currentOwner.setRole(Role.MIEMBRO);
        workGroupMembershipRepository.save(currentOwner);

        // 2. Asignar el nuevo OWNER
        newOwnerMembership.setRole(Role.OWNER);
        workGroupMembershipRepository.save(newOwnerMembership);

        workGroupRepository.save(workGroup);

        LOG.info("Transferred ownership of work group {} from user {} to user {}",
            workGroupId, currentOwner.getUser().getLogin(), newOwnerLogin);
    }

    @Transactional
    public void addModerator(Long workGroupId, String userId) {
        LOG.debug("Adding user {} as moderator to work group {}", userId, workGroupId);

        validateAdminOrGroupOwnerOrModerator(workGroupId);

        WorkGroup workGroup = validateWorkGroup(workGroupId);
        User user = validateUser(userId);

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

        validateAdminOrGroupOwner(workGroupId);

        WorkGroup workGroup = validateWorkGroup(workGroupId);
        User user = validateUser(userId);

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

        validateAdminOrGroupOwnerOrModerator(workGroupId);

        WorkGroup workGroup = validateWorkGroup(workGroupId);
        User user = validateUser(userLogin);

        if (workGroupMembershipRepository.existsByWorkGroupAndUser(workGroup, user)) {
            throw new BadRequestAlertException("User is already a member", ENTITY_NAME, "already.member");
        }

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

        validateAdminOrGroupOwnerOrModerator(workGroupId);

        WorkGroup workGroup = validateWorkGroup(workGroupId);
        User user = validateUser(userLogin);

        WorkGroupMembership membership = workGroupMembershipRepository
            .findByWorkGroupAndUser(workGroup, user)
            .orElseThrow(() -> new BadRequestAlertException("User is not a member", ENTITY_NAME, "not.member"));

        if (membership.getRole() == Role.OWNER) {
            throw new BadRequestAlertException("Cannot remove owner", ENTITY_NAME, "cannot.remove.owner");
        }

        // Validar que moderadores solo pueden remover miembros, no otros moderadores
        if (!isCurrentUserAdmin()) {
            String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow();
            boolean isCurrentUserOwner = workGroupMembershipRepository.existsByWorkGroupIdAndUserLoginAndRole(
                workGroupId, currentUserLogin, Role.OWNER);

            if (membership.getRole() == Role.MODERADOR && !isCurrentUserOwner) {
                throw new AccessDeniedException("Moderators can only remove regular members");
            }
        }

        // Validar que no se esté eliminando a sí mismo
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow();
        if (user.getLogin().equals(currentUserLogin)) {
            throw new BadRequestAlertException("Cannot remove yourself", ENTITY_NAME, "cannot.remove.self");
        }

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
        // Validar que el usuario que intenta salir es el mismo que el autenticado
        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("User not logged in"));

        if (!userLogin.equals(currentUserLogin)) {
            if (!isCurrentUserAdmin()) {
                throw new AccessDeniedException("You can only leave a group for yourself");
            }
            // Admin puede sacar a otros usuarios del grupo
        }

        WorkGroup workGroup = validateWorkGroup(workGroupId);
        WorkGroupMembership membership = workGroupMembershipRepository
            .findByWorkGroupAndUserLogin(workGroup, userLogin)
            .orElseThrow(() -> new BadRequestAlertException("User is not a member", ENTITY_NAME, "not.member"));

        if (membership.getRole() == Role.OWNER) {
            throw new BadRequestAlertException("Owner cannot leave the group", ENTITY_NAME, "owner.cannot.leave");
        }

        workGroupMembershipRepository.delete(membership);
        workGroupRepository.save(workGroup);
    }
}
