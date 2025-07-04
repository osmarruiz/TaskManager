package com.dcmc.apps.taskmanager.repository;

import com.dcmc.apps.taskmanager.domain.Priority;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Priority entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PriorityRepository extends JpaRepository<Priority, Long>, JpaSpecificationExecutor<Priority> {
    // Additional query methods can be defined here if needed
    Optional<Priority> findByName(String name);
}
