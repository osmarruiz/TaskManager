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


}
