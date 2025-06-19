package com.dcmc.apps.taskmanager.service.mapper;

import com.dcmc.apps.taskmanager.domain.Project;
import com.dcmc.apps.taskmanager.domain.ProjectMember;
import com.dcmc.apps.taskmanager.domain.User;
import com.dcmc.apps.taskmanager.service.dto.ProjectDTO;
import com.dcmc.apps.taskmanager.service.dto.ProjectMemberDTO;
import com.dcmc.apps.taskmanager.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectMember} and its DTO {@link ProjectMemberDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectMemberMapper extends EntityMapper<ProjectMemberDTO, ProjectMember> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    ProjectMemberDTO toDto(ProjectMember s);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
