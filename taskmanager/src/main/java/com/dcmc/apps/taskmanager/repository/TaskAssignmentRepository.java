package com.dcmc.apps.taskmanager.repository;

import com.dcmc.apps.taskmanager.domain.TaskAssignment;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TaskAssignment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long>, JpaSpecificationExecutor<TaskAssignment> {
    @Query("select taskAssignment from TaskAssignment taskAssignment where taskAssignment.user.login = ?#{authentication.name}")
    List<TaskAssignment> findByUserIsCurrentUser();
}
