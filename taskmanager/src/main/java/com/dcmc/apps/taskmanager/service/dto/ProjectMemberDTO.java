package com.dcmc.apps.taskmanager.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.dcmc.apps.taskmanager.domain.ProjectMember} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectMemberDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant assignedAt;

    @NotNull
    private ProjectDTO project;

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

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
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
        if (!(o instanceof ProjectMemberDTO)) {
            return false;
        }

        ProjectMemberDTO projectMemberDTO = (ProjectMemberDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectMemberDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMemberDTO{" +
            "id=" + getId() +
            ", assignedAt='" + getAssignedAt() + "'" +
            ", project=" + getProject() +
            ", user=" + getUser() +
            "}";
    }
}
