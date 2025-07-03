package com.dcmc.apps.taskmanager.service.mapper;

import com.dcmc.apps.taskmanager.domain.TaskStatusCatalog;
import com.dcmc.apps.taskmanager.domain.User;
import com.dcmc.apps.taskmanager.service.dto.TaskStatusCatalogDTO;
import com.dcmc.apps.taskmanager.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TaskStatusCatalog} and its DTO {@link TaskStatusCatalogDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskStatusCatalogMapper extends EntityMapper<TaskStatusCatalogDTO, TaskStatusCatalog> {
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "userId")
    TaskStatusCatalogDTO toDto(TaskStatusCatalog s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
