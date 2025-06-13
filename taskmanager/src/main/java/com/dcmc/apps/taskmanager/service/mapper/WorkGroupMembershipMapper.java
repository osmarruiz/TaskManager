package com.dcmc.apps.taskmanager.service.mapper;

import com.dcmc.apps.taskmanager.domain.User;
import com.dcmc.apps.taskmanager.domain.WorkGroup;
import com.dcmc.apps.taskmanager.domain.WorkGroupMembership;
import com.dcmc.apps.taskmanager.service.dto.UserDTO;
import com.dcmc.apps.taskmanager.service.dto.WorkGroupDTO;
import com.dcmc.apps.taskmanager.service.dto.WorkGroupMembershipDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WorkGroupMembership} and its DTO {@link WorkGroupMembershipDTO}.
 */
@Mapper(componentModel = "spring")
public interface WorkGroupMembershipMapper extends EntityMapper<WorkGroupMembershipDTO, WorkGroupMembership> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "workGroup", source = "workGroup", qualifiedByName = "workGroupId")
    WorkGroupMembershipDTO toDto(WorkGroupMembership s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("workGroupId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WorkGroupDTO toDtoWorkGroupId(WorkGroup workGroup);
}
