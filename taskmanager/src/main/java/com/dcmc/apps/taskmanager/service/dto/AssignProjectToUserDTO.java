package com.dcmc.apps.taskmanager.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AssignProjectToUserDTO {
    @NotNull
    private Long projectId;

    @NotBlank
    private String userLogin; // Username del usuario a asignar

    // Getters y Setters
    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
}
