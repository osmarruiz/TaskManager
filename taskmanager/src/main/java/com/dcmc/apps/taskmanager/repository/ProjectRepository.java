package com.dcmc.apps.taskmanager.repository;

import com.dcmc.apps.taskmanager.domain.Project;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Project entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    List<Project> findByWorkGroupId(Long workGroupId);
}
