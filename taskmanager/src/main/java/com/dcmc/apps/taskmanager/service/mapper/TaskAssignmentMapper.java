package com.dcmc.apps.taskmanager.service.mapper;

import com.dcmc.apps.taskmanager.domain.Task;
import com.dcmc.apps.taskmanager.domain.TaskAssignment;
import com.dcmc.apps.taskmanager.domain.User;
import com.dcmc.apps.taskmanager.service.dto.TaskAssignmentDTO;
import com.dcmc.apps.taskmanager.service.dto.TaskDTO;
import com.dcmc.apps.taskmanager.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TaskAssignment} and its DTO {@link TaskAssignmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskAssignmentMapper extends EntityMapper<TaskAssignmentDTO, TaskAssignment> {
    @Mapping(target = "task", source = "task", qualifiedByName = "taskId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    TaskAssignmentDTO toDto(TaskAssignment s);

    @Named("taskId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TaskDTO toDtoTaskId(Task task);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
