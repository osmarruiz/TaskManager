package com.dcmc.apps.taskmanager.service.mapper;

import com.dcmc.apps.taskmanager.domain.Comment;
import com.dcmc.apps.taskmanager.domain.Task;
import com.dcmc.apps.taskmanager.domain.User;
import com.dcmc.apps.taskmanager.service.dto.CommentDTO;
import com.dcmc.apps.taskmanager.service.dto.TaskDTO;
import com.dcmc.apps.taskmanager.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {
    @Mapping(target = "task", source = "task", qualifiedByName = "taskId")
    @Mapping(target = "author", source = "author", qualifiedByName = "userId")
    CommentDTO toDto(Comment s);

    @Named("taskId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TaskDTO toDtoTaskId(Task task);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
