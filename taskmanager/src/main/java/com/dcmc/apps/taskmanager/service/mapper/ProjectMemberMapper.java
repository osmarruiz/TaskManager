package com.dcmc.apps.taskmanager.service.mapper;

import com.dcmc.apps.taskmanager.domain.ProjectMember;
import com.dcmc.apps.taskmanager.service.dto.ProjectMemberDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectMember} and its DTO {@link ProjectMemberDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectMemberMapper extends EntityMapper<ProjectMemberDTO, ProjectMember> {
    @Mapping(target = "project", source = "project")
    @Mapping(target = "user", source = "user")
    ProjectMemberDTO toDto(ProjectMember s);
}
