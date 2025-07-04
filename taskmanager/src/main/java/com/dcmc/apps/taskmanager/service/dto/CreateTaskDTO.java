package com.dcmc.apps.taskmanager.service.dto;

import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public class CreateTaskDTO {

    @NotNull
    private String title;

    @NotNull
    private String description;

    private ZonedDateTime deadline;

    // Getters y Setters
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

    public ZonedDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(ZonedDateTime deadline) {
        this.deadline = deadline;
    }

}
