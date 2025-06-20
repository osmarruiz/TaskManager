package com.dcmc.apps.taskmanager.repository;

import com.dcmc.apps.taskmanager.domain.User;
import com.dcmc.apps.taskmanager.domain.WorkGroup;
import com.dcmc.apps.taskmanager.domain.WorkGroupMembership;
import java.util.List;
import java.util.Optional;

import com.dcmc.apps.taskmanager.domain.enumeration.Role;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WorkGroupMembership entity.
 */

@Repository
public interface WorkGroupMembershipRepository
    extends JpaRepository<WorkGroupMembership, Long>, JpaSpecificationExecutor<WorkGroupMembership> {
    @Query(
        "select workGroupMembership from WorkGroupMembership workGroupMembership where workGroupMembership.user.login = ?#{authentication.name}"
    )
    List<WorkGroupMembership> findByUserIsCurrentUser();

    Optional<WorkGroupMembership> findByWorkGroupAndUser(WorkGroup workGroup, User user);

    Optional<WorkGroupMembership> findByWorkGroupAndRole(WorkGroup workGroup, Role role);

    boolean existsByWorkGroupAndUser(WorkGroup workGroup, User user);

    @Query("SELECT wgm FROM WorkGroupMembership wgm WHERE wgm.workGroup = :workGroup AND wgm.user.login = :login")
    Optional<WorkGroupMembership> findByWorkGroupAndUserLogin(
        @Param("workGroup") WorkGroup workGroup,
        @Param("login") String login);

    @Query("SELECT CASE WHEN COUNT(wgm) > 0 THEN true ELSE false END " +
        "FROM WorkGroupMembership wgm " +
        "WHERE wgm.workGroup.id = :workGroupId AND wgm.user.login = :login AND wgm.role = :role")
    boolean existsByWorkGroupIdAndUserLoginAndRole(
        @Param("workGroupId") Long workGroupId,
        @Param("login") String login,
        @Param("role") Role role);

    @Query("SELECT CASE WHEN COUNT(wgm) > 0 THEN true ELSE false END " +
        "FROM WorkGroupMembership wgm " +
        "WHERE wgm.workGroup.id = :workGroupId AND wgm.user.login = :login " +
        "AND wgm.role IN :roles")
    boolean existsByWorkGroupIdAndUserLoginAndRoleIn(
        @Param("workGroupId") Long workGroupId,
        @Param("login") String login,
        @Param("roles") List<Role> roles);

    @Query("SELECT wgm FROM WorkGroupMembership wgm WHERE wgm.workGroup.id = :workGroupId")
    List<WorkGroupMembership> findByWorkGroupId(@Param("workGroupId") Long workGroupId);

    @Query("SELECT wgm FROM WorkGroupMembership wgm JOIN FETCH wgm.workGroup WHERE wgm.user.login = :login")
    List<WorkGroupMembership> findByUserLogin(@Param("login") String login);

    @Query("SELECT wgm FROM WorkGroupMembership wgm WHERE wgm.workGroup.id = :workGroupId AND wgm.role = :role")
    List<WorkGroupMembership> findByWorkGroupIdAndRole(@Param("workGroupId") Long workGroupId, @Param("role") Role role);

    @Query("SELECT wgm FROM WorkGroupMembership wgm WHERE wgm.workGroup.id = :workGroupId AND wgm.user.login = :login")
    Optional<WorkGroupMembership> findByWorkGroupIdAndUserLogin(@Param("workGroupId") Long workGroupId, @Param("login") String login);


    boolean existsByWorkGroupIdAndUserLogin(Long id, String userLogin);
}
