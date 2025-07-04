package com.dcmc.apps.taskmanager.repository;

import com.dcmc.apps.taskmanager.domain.Task;
import com.dcmc.apps.taskmanager.domain.TaskAssignment;
import com.dcmc.apps.taskmanager.domain.User;
import java.util.List;
import java.util.Optional;
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

    boolean existsByTaskAndUser(Task task, User user);

    Optional<TaskAssignment> findByTaskAndUser(Task task, User user);

    List<TaskAssignment> findByTask(Task task);

    List<TaskAssignment> findByUser(User user);
}
