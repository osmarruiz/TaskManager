package com.dcmc.apps.taskmanager.repository;

import com.dcmc.apps.taskmanager.domain.TaskStatusCatalog;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TaskStatusCatalog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskStatusCatalogRepository extends JpaRepository<TaskStatusCatalog, Long>, JpaSpecificationExecutor<TaskStatusCatalog> {
    @Query(
        "select taskStatusCatalog from TaskStatusCatalog taskStatusCatalog where taskStatusCatalog.createdBy.login = ?#{authentication.name}"
    )
    List<TaskStatusCatalog> findByCreatedByIsCurrentUser();

    @Override
    Optional<TaskStatusCatalog> findById(Long aLong);

    Optional<TaskStatusCatalog> findByName(String name);
}
