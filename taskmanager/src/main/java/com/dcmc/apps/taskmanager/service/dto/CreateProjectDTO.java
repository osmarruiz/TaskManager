package com.dcmc.apps.taskmanager.service.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A DTO for creating {@link com.dcmc.apps.taskmanager.domain.Project} entities.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CreateProjectDTO implements Serializable {

    @NotNull
    private String title;

    @NotNull
    private String description;

    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    @NotNull
    private Long workGroupId;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    @Override
    public String toString() {
        return "ProjectCreateDTO{" +
            "title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", workGroupId=" + workGroupId +
            '}';
    }
}
