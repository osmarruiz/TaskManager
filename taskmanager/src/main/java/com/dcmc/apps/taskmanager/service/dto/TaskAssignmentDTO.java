package com.dcmc.apps.taskmanager.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.dcmc.apps.taskmanager.domain.TaskAssignment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaskAssignmentDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant assignedAt;

    @NotNull
    private TaskDTO task;

    @NotNull
    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    public TaskDTO getTask() {
        return task;
    }

    public void setTask(TaskDTO task) {
        this.task = task;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskAssignmentDTO)) {
            return false;
        }

        TaskAssignmentDTO taskAssignmentDTO = (TaskAssignmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, taskAssignmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskAssignmentDTO{" +
            "id=" + getId() +
            ", assignedAt='" + getAssignedAt() + "'" +
            ", task=" + getTask() +
            ", user=" + getUser() +
            "}";
    }
}
