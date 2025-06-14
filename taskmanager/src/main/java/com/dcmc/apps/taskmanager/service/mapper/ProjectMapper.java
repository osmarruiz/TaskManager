package com.dcmc.apps.taskmanager.service.mapper;

import com.dcmc.apps.taskmanager.domain.Project;
import com.dcmc.apps.taskmanager.domain.WorkGroup;
import com.dcmc.apps.taskmanager.service.dto.ProjectDTO;
import com.dcmc.apps.taskmanager.service.dto.WorkGroupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Project} and its DTO {@link ProjectDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper extends EntityMapper<ProjectDTO, Project> {
    @Mapping(target = "workGroup", source = "workGroup", qualifiedByName = "workGroupId")
    ProjectDTO toDto(Project s);

    @Named("workGroupId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WorkGroupDTO toDtoWorkGroupId(WorkGroup workGroup);
}
