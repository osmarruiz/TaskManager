package com.dcmc.apps.taskmanager.repository;

import com.dcmc.apps.taskmanager.domain.WorkGroupMembership;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WorkGroupMembership entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkGroupMembershipRepository
    extends JpaRepository<WorkGroupMembership, Long>, JpaSpecificationExecutor<WorkGroupMembership> {
    @Query(
        "select workGroupMembership from WorkGroupMembership workGroupMembership where workGroupMembership.user.login = ?#{authentication.name}"
    )
    List<WorkGroupMembership> findByUserIsCurrentUser();
}
