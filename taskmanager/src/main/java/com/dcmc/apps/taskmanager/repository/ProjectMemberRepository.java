package com.dcmc.apps.taskmanager.repository;

import com.dcmc.apps.taskmanager.domain.ProjectMember;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProjectMember entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long>, JpaSpecificationExecutor<ProjectMember> {
    @Query("select projectMember from ProjectMember projectMember where projectMember.user.login = ?#{authentication.name}")
    List<ProjectMember> findByUserIsCurrentUser();
}
