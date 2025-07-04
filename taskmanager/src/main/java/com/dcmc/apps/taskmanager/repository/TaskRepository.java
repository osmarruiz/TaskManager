package com.dcmc.apps.taskmanager.repository;

import com.dcmc.apps.taskmanager.domain.Task;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Task entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findByParentProjectId(Long projectId);

    Optional<Task> findByIdAndArchivedTrue(Long taskId);
}
